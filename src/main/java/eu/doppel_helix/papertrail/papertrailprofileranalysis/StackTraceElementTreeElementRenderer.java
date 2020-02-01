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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public final class StackTraceElementTreeElementRenderer implements TreeCellRenderer {
    private final DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
    private final JPanel resultComponent = new JPanel();
    private final JProgressBar progressBar = new JProgressBar();

    public StackTraceElementTreeElementRenderer() {
        defaultRenderer.setLeafIcon(null);
        defaultRenderer.setOpenIcon(null);
        defaultRenderer.setClosedIcon(null);
        progressBar.setMaximumSize(new Dimension(100, 200));
        progressBar.setStringPainted(true);
        resultComponent.setLayout(new BorderLayout(5, 0));
        resultComponent.add(progressBar, BorderLayout.WEST);
        resultComponent.add(defaultRenderer);
        resultComponent.setOpaque(false);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof StackTraceElementNode) {
            StackTraceElementNode ste = (StackTraceElementNode) value;
            progressBar.setMaximum((int) ste.getTotal());
            progressBar.setMinimum(0);
            progressBar.setValue((int) ste.getCount());
            progressBar.setString(String.format("[%d / %d] (%d)", ste.getCount(), ste.getTotal(), ste.getSelfCount()));
            defaultRenderer.getTreeCellRendererComponent(tree, ste.getLocation(), sel, expanded, leaf, row, hasFocus);
        }
        defaultRenderer.setSize(defaultRenderer.getPreferredSize());
        return resultComponent;
    }
}
