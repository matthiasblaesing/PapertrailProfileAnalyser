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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StackTrace {

    private final long count;
    private final List<String> traceElements;

    public StackTrace(long count, List<String> traceElements) {
        this.count = count;
        this.traceElements = Collections.unmodifiableList(new ArrayList<>(traceElements));
    }

    public long getCount() {
        return count;
    }

    public List<String> getTraceElements() {
        return traceElements;
    }
}
