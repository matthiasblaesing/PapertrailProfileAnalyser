/*
 * Copyright 2019 Matthias Bläsing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.doppel_helix.papertrail.papertrailprofileranalysis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PapertrailParser {

    private static final Pattern SYMBOL_LINE = Pattern.compile("0x([0-9a-fA-F]{16})\\s+(.*)");
    private static final long[] START_MARKER = new long[] {0, 3, 0, 1, 0};
    private final Charset charsetOfData;
    private final Path input;
    private String binary;
    private final Map<Long,String> symbolMap = new HashMap<>();
    private final List<StackTrace> stackTraces = new ArrayList<>();
    private final byte[] newLine;

    public PapertrailParser(Path input, Charset charsetOfData) throws IOException {
        ByteBuffer newLineBuffer = charsetOfData.encode("\n");
        newLine = new byte[newLineBuffer.capacity()];
        newLineBuffer.get(newLine);
	this.charsetOfData = charsetOfData;
	this.input = input;
	parse();
    }

    public String getBinary() {
	return binary;
    }

    public Map<Long, String> getSymbolMap() {
	return Collections.unmodifiableMap(symbolMap);
    }

    public List<StackTrace> getStackTraces() {
	return Collections.unmodifiableList(stackTraces);
    }

    private void parse() throws IOException {
        try (InputStream is0 = Files.newInputStream(input, StandardOpenOption.READ)) {
	    String line;
	    line = readLine(is0);
	    if(! "--- symbol".equals(line)) {
		throw new IOException(String.format("Expected '--- symbol', got \n'%s'", line.substring(0, Math.min(line.length(), 128))));
	    }
	    line = readLine(is0);
	    if(! line.startsWith("binary=")) {
		throw new IOException(String.format("Expected line to start with 'binary=', got '%s'", line));
	    } else {
		binary = line.substring(7);
	    }
	    while(true) {
		line = readLine(is0);
		if("---".equals(line)) {
		    break;
		} else {
		    Matcher matcher = SYMBOL_LINE.matcher(line);
		    if(! matcher.matches()) {
			throw new IOException("Found empty line in symbol section");
		    }
		    symbolMap.put(Long.valueOf(matcher.group(1), 16), matcher.group(2));
		}
	    }
	    line = readLine(is0);
	    if(! "--- profile".equals(line)) {
		throw new IOException(String.format("Expected '--- profile', got '%s'", line));
	    }

	    long[] startSignature = readLongArray(is0, 5);
	    if(!Arrays.equals(START_MARKER, startSignature)) {
		throw new IOException(String.format("Expected start marker '%s', got '%s'", Arrays.toString(START_MARKER), Arrays.toString(startSignature)));
	    }



	    ArrayList<StackTrace> data = new ArrayList<>();
	    do {
		int count = (int) readLong(is0);
		int depth = (int) readLong(is0);
		long eofMark = (int) readLong(is0);
		if(count == 0 && depth == 1 && eofMark == 0) {
		    this.stackTraces.addAll(data);
		    return;
		} else {
		    long[] refs = readLongArray(is0, depth - 1);
		    List<String> trace = new ArrayList<>(depth);
                    trace.add(symbolMap.get(eofMark));
		    for(long ref: refs) {
                        String steLine = symbolMap.get(ref);
                        if(trace.isEmpty() || (! trace.get(trace.size() - 1).equals(steLine))) {
                            trace.add(steLine);
                        }
		    }
		    StackTrace ste = new StackTrace(count, trace);
		    data.add(ste);
		}
	    } while (true);
	}
    }

    private long readLong(final InputStream isr) throws IOException {
	long result = (isr.read() & 0xFFL);
	result |= (isr.read() & 0xFFL) << 8;
	result |= (isr.read() & 0xFFL) << 16;
	result |= (isr.read() & 0xFFL) << 24;
	result |= (isr.read() & 0xFFL) << 32;
	result |= (isr.read() & 0xFFL) << 40;
	result |= (isr.read() & 0xFFL) << 48;
	result |= (isr.read() & 0xFFL) << 56;
	return result;
    }

    private long[] readLongArray(final InputStream isr, int count) throws IOException {
	long[] result = new long[(int) count];
	for(int i = 0; i < count; i++) {
	    result[i] = readLong(isr);
	}
	return result;
    }

    private String readLine(final InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] newLineSearch = new byte[newLine.length];
        readFully(newLineSearch, is);

        while(true) {
            if(Arrays.equals(newLineSearch, newLine)) {
                return baos.toString(charsetOfData.name());
            }

            int readByte = is.read();

            if (readByte < 0) {
                throw new IOException("Reached EOF while looking for EOL");
            }

            baos.write(newLineSearch[0]);
            System.arraycopy(newLineSearch, 1, newLineSearch, 0, newLineSearch.length - 1);
            newLineSearch[newLineSearch.length - 1] = (byte) readByte;
        }
    }

    private void readFully(byte[] target, InputStream source) throws IOException {
        int read = 0;
        do {
            int currentRead = source.read(target, read, target.length - read);
            read += currentRead;
            if(currentRead < 0) {
                throw new IOException("No bytes left in stream");
            }
        } while( read < target.length );
    }
}
