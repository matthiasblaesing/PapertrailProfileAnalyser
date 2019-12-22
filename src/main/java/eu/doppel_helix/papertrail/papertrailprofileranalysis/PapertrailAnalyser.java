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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import net.sourceforge.argparse4j.inf.Namespace;

public class PapertrailAnalyser {

    public static void main(String[] argv) {
        ArgumentParser parser = ArgumentParsers
            .newFor("PapertrailProfilerAnalysis")
            .build();
        parser.addArgument("-c", "--charset")
            .dest("charset")
            .metavar("charset")
            .help("Charset to use for interpretation of profiler file")
            .type(new ArgumentType<Charset>() {
                @Override
                public Charset convert(ArgumentParser arg0, Argument arg1, String arg2) throws ArgumentParserException {
                    try {
                        return Charset.forName(arg2);
                    } catch (IllegalArgumentException ex) {
                        throw new ArgumentParserException(ex, arg0);
                    }
                }
            })
            .setDefault(StandardCharsets.UTF_8);
        parser.addArgument("-f", "--file")
            .dest("file")
            .metavar("file")
            .help("File to parse as papertrail profiler file (pprof)")
            .type(File.class);
        parser.addArgument("-g", "--gui")
            .dest("gui")
            .action(Arguments.storeTrue());
        Namespace namespace = parser.parseArgsOrFail(argv);
        Charset selectedCharset = namespace.<Charset>get("charset");
        File file = namespace.<File>get("file");
        if (namespace.<Boolean>get("gui")) {
            PapertrailUI.start(file, selectedCharset);
        } else {
            if (file == null) {
                parser.handleError(new ArgumentParserException("file must be specified when run on CLI", parser));
                System.exit(1);
            }
            try {
                PapertrailParser papertrailParser = new PapertrailParser(file.toPath(), selectedCharset);
                System.out.println(papertrailParser);
            } catch (IOException ex) {
                System.err.printf("Failed to parse file %s with charset %s%n%n", file, selectedCharset);
                ex.printStackTrace(System.err);
                System.exit(1);
            }
        }
    }
}
