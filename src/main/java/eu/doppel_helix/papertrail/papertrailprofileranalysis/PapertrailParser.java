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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public PapertrailParser(Path input, Charset charsetOfData) throws IOException {
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
	try (InputStream is = Files.newInputStream(input, StandardOpenOption.READ);
		InputStreamReader r = new InputStreamReader(is, charsetOfData);
		BufferedReader isr = new BufferedReader(r)) {
	    String line;
	    line = readLine(isr);
	    if(! "--- symbol".equals(line)) {
		throw new IOException(String.format("Expected '--- symbol', got \n'%s'", line.substring(0, Math.min(line.length(), 128))));
	    }
	    line = readLine(isr);
	    if(! line.startsWith("binary=")) {
		throw new IOException(String.format("Expected line to start with 'binary=', got '%s'", line));
	    } else {
		binary = line.substring(7);
	    }
	    while(true) {
		line = readLine(isr);
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
	    line = readLine(isr);
	    if(! "--- profile".equals(line)) {
		throw new IOException(String.format("Expected '--- profile', got '%s'", line));
	    }
	    long[] startSignature = readLongArray(isr, 5);
	    if(!Arrays.equals(START_MARKER, startSignature)) {
		throw new IOException(String.format("Expected start marker '%s', got '%s'", Arrays.toString(START_MARKER), Arrays.toString(startSignature)));
	    }

	    ArrayList<StackTrace> data = new ArrayList<>();
	    do {
		int count = (int) readLong(isr);
		int depth = (int) readLong(isr);
		isr.mark(8);
		long eofMark = (int) readLong(isr);
		isr.reset();
		if(count == 0 && depth == 1 && eofMark == 0) {
		    this.stackTraces.addAll(data);
		    return;
		} else {
		    long[] refs = readLongArray(isr, depth);
		    List<String> trace = new ArrayList<>(depth);
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

    private long readLong(final Reader isr) throws IOException {
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

    private long[] readLongArray(final Reader isr, int count) throws IOException {
	long[] result = new long[(int) count];
	for(int i = 0; i < count; i++) {
	    result[i] = readLong(isr);
	}
	return result;
    }

    private String readLine(final Reader isr) throws IOException {
	StringBuilder sb = new StringBuilder();
	int cur;
	while(true) {
	    cur = isr.read();
	    if(cur == -1 || cur == '\n') {
		break;
	    } else {
		sb.append((char) cur);
	    }
	}
	return sb.toString();
    }
}
