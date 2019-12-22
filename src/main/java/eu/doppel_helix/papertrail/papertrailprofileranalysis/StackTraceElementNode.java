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
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;

public class StackTraceElementNode implements TreeNode {
    private final List<StackTraceElementNode> children = new ArrayList<>();
    private StackTraceElementNode parent = null;
    private String location = "";
    private long count = 0;
    private long total = 0;

    @Override
    public Enumeration<? extends TreeNode> children() {
        return (Enumeration<? extends TreeNode>) Collections.unmodifiableList(children);
    }

    public List<StackTraceElementNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public StackTraceElementNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public StackTraceElementNode getParent() {
        return parent;
    }

    public void setParent(StackTraceElementNode parent) {
        this.parent = parent;
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void add(StackTraceElementNode ste) {
        ste.setParent(this);
        this.children.add(ste);
    }

    public void sortChildren(Comparator<StackTraceElementNode> comparator, boolean recursive) {
        Collections.sort(children, comparator);
        if(recursive) {
            for(StackTraceElementNode sten: children) {
                sten.sortChildren(comparator, recursive);
            }
        }
    }
}
