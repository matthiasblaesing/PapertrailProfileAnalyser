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

public class StackTraceSummarizer {

    private StackTraceSummarizer() {
    }

    public static StackTraceElementNode summarize(PapertrailParser pp) {
        StackTraceElementNode rootNode = new StackTraceElementNode();
        rootNode.setLocation("<>");
        rootNode.setCount(0);
        rootNode.setTotal(0);
        if (pp == null) {
            return rootNode;
        }
        int total = 0;
        for (StackTrace st : pp.getStackTraces()) {
            total += st.getCount();
        }
        rootNode.setCount(total);
        rootNode.setTotal(total);
        for (StackTrace st : pp.getStackTraces()) {
            ArrayList<String> elements = new ArrayList<>(st.getTraceElements());
            Collections.reverse(elements);
            addTrace(rootNode, elements, st.getCount(), total);
        }
        rootNode.sortChildren((s1, s2) -> Long.signum(s2.getCount() - s1.getCount()), true);
        return rootNode;
    }

    private static void addTrace(StackTraceElementNode parent, List<String> remainingTrace, long count, long total) {
        String currentLocation = remainingTrace.get(0);
        StackTraceElementNode currentNode = null;
        for (StackTraceElementNode child : parent.getChildren()) {
            if (child.getLocation().equals(currentLocation)) {
                currentNode = child;
                break;
            }
        }
        if (currentNode == null) {
            currentNode = new StackTraceElementNode();
            currentNode.setLocation(currentLocation);
            currentNode.setTotal(total);
            parent.add(currentNode);
        }
        currentNode.setCount(currentNode.getCount() + count);
        if (remainingTrace.size() > 1) {
            addTrace(currentNode, remainingTrace.subList(1, remainingTrace.size()), count, total);
        }
    }
}
