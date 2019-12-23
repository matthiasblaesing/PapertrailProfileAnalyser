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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author matthias
 */
public class PapertrailUI extends javax.swing.JFrame {

    private static final Logger LOG = Logger.getLogger(PapertrailUI.class.getName());
    private PapertrailParser parser;
    private DefaultComboBoxModel<StackTrace> stacktraceSelectionModel = new DefaultComboBoxModel<>();
    private DefaultTableModel stacktraceModel = new DefaultTableModel(new String[]{"Method"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private DefaultTreeModel calltreeModel = new DefaultTreeModel(new StackTraceElementNode());

    /**
     * Creates new form PaperTrailUI
     */
    public PapertrailUI() {
        initComponents();
        stacktraceTable.setModel(stacktraceModel);
        stacktraceSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof StackTrace) {
                    StackTrace st = (StackTrace) value;
                    value = st.getCount() + " - " + st.getTraceElements().get(0);
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        stacktraceSelector.setModel(stacktraceSelectionModel);
        stacktraceSelector.addActionListener(l -> {
            StackTrace selected = (StackTrace) stacktraceSelector.getSelectedItem();
            stacktraceModel.setRowCount(0);
            if (selected != null) {
                selected.getTraceElements().stream().forEachOrdered(s -> stacktraceModel.addRow(new Object[]{s}));
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });

        calltreeTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int[] selectedRow = calltreeTree.getSelectionRows();
                    if(selectedRow.length == 1) {
                        TreePath tp = calltreeTree.getPathForRow(selectedRow[0]);
                        toggleExpandRecursive(calltreeTree, tp);
                    }
                    e.consume();
                }
            }
        });
        calltreeTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    TreePath tp = calltreeTree.getPathForLocation(e.getX(), e.getY());
                    if(tp != null) {
                        toggleExpandRecursive(calltreeTree, tp);
                    }
                }
            }
        });
        calltreeTree.setToggleClickCount(Integer.MAX_VALUE);
        calltreeTree.setModel(calltreeModel);
        calltreeTree.setRootVisible(false);
        calltreeTree.setShowsRootHandles(true);
        calltreeTree.setCellRenderer(new StackTraceElementTreeElementRenderer());
    }

    private void toggleExpandRecursive(JTree tree, TreePath tp) {
        boolean collapse = tree.isExpanded(tp);
        if(collapse) {
            tree.collapsePath(tp);
        } else {
            expandRecursive(tree, tp);
        }
    }

    private void expandRecursive(JTree tree, TreePath tp) {
        tree.expandPath(tp);
        StackTraceElementNode sten = (StackTraceElementNode) tp.getLastPathComponent();
        if(sten.getChildCount() == 1) {
            for(StackTraceElementNode child: sten.getChildren()) {
                expandRecursive(tree, tp.pathByAddingChild(child));
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        analysisPanels = new javax.swing.JTabbedPane();
        stacktraceList = new javax.swing.JPanel();
        stacktraceTableWrapper = new javax.swing.JScrollPane();
        stacktraceTable = new javax.swing.JTable();
        stacktraceSelector = new javax.swing.JComboBox<>();
        calltreePanel = new javax.swing.JPanel();
        calltreeWrapper = new javax.swing.JScrollPane();
        calltreeTree = new javax.swing.JTree();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openfile = new javax.swing.JMenuItem();
        exit = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Papertrail Profile Analyser");
        setPreferredSize(new java.awt.Dimension(800, 600));

        stacktraceList.setLayout(new java.awt.BorderLayout());

        stacktraceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Method"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        stacktraceTableWrapper.setViewportView(stacktraceTable);

        stacktraceList.add(stacktraceTableWrapper, java.awt.BorderLayout.CENTER);
        stacktraceList.add(stacktraceSelector, java.awt.BorderLayout.PAGE_START);

        analysisPanels.addTab("Stacktrace List", stacktraceList);

        calltreePanel.setLayout(new java.awt.BorderLayout());

        calltreeWrapper.setViewportView(calltreeTree);

        calltreePanel.add(calltreeWrapper, java.awt.BorderLayout.CENTER);

        analysisPanels.addTab("Calltree", calltreePanel);

        getContentPane().add(analysisPanels, java.awt.BorderLayout.CENTER);

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");
        fileMenu.setToolTipText("");

        openfile.setMnemonic('o');
        openfile.setText("Open");
        openfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openfileActionPerformed(evt);
            }
        });
        fileMenu.add(openfile);

        exit.setMnemonic('q');
        exit.setText("Quit");
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        fileMenu.add(exit);

        jMenuBar1.add(fileMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitActionPerformed

    public void openFile(final File file, final Charset charset) {
        new SwingWorker<PapertrailParser, Object>() {
            @Override
            protected PapertrailParser doInBackground() throws Exception {
                PapertrailParser parser = new PapertrailParser(file.toPath(), charset);
                return parser;
            }

            @Override
            protected void done() {
                super.done();
                try {
                    PapertrailUI.this.parser = get();
                } catch (ExecutionException | InterruptedException ex) {
                    PapertrailUI.this.parser = null;
                    LOG.log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(PapertrailUI.this,
                        "Failed to read papertrail profile\n\n" + ex.getMessage(),
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
                }
                updateFromParser();
            }
        }.execute();
    }

    private void updateFromParser() {
        stacktraceSelectionModel.removeAllElements();
        if (parser != null) {
            ArrayList<StackTrace> traces = new ArrayList<>(parser.getStackTraces());
            Collections.sort(traces, (s1, s2) -> Long.signum(s2.getCount() - s1.getCount()));
            stacktraceSelectionModel.addAll(traces);
            stacktraceSelectionModel.setSelectedItem(traces.get(0));
        }
        new SwingWorker<StackTraceElementNode, Object>() {
            @Override
            protected StackTraceElementNode doInBackground() throws Exception {
                return StackTraceSummarizer.summarize(parser);
            }

            @Override
            protected void done() {
                try {
                    calltreeModel.setRoot(get());
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }

        }.execute();

    }

    private static File lastLocation = null;
    private static FileFilter selectedFilter = null;
    private static final FileFilter pprofFilter = new FileNameExtensionFilter("pprof File", "pprof");
    private static final Charset[] charsetSelection;
    static {
        ArrayList<Charset> charsets = new ArrayList<>(Charset.availableCharsets().values());
        Collections.sort(charsets, (c1, c2) -> c1.displayName().compareTo(c2.displayName()));
        charsetSelection = charsets.toArray(new Charset[0]);
    }
    private void openfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openfileActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(lastLocation);
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.addChoosableFileFilter(pprofFilter);
        JComboBox<Charset> charsetComboBox = new JComboBox<>(charsetSelection);
        charsetComboBox.setSelectedItem(StandardCharsets.UTF_8);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.BASELINE_LEADING;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Please choose the charset:"), gbc);
        gbc.gridy = 1;
        panel.add(charsetComboBox, gbc);
        fileChooser.setAccessory(panel);
        fileChooser.setFileFilter(selectedFilter == null ? pprofFilter : selectedFilter);
        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this)) {
            lastLocation = fileChooser.getCurrentDirectory();
            selectedFilter = fileChooser.getFileFilter();
            openFile(fileChooser.getSelectedFile(), (Charset) charsetComboBox.getSelectedItem());
        }
    }//GEN-LAST:event_openfileActionPerformed

    public static void start(final File filePath, final Charset charset) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PapertrailUI ptu = new PapertrailUI();
                ptu.setVisible(true);
                if(filePath != null) {
                    ptu.openFile(filePath, charset);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane analysisPanels;
    private javax.swing.JPanel calltreePanel;
    private javax.swing.JTree calltreeTree;
    private javax.swing.JScrollPane calltreeWrapper;
    private javax.swing.JMenuItem exit;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem openfile;
    private javax.swing.JPanel stacktraceList;
    private javax.swing.JComboBox<StackTrace> stacktraceSelector;
    private javax.swing.JTable stacktraceTable;
    private javax.swing.JScrollPane stacktraceTableWrapper;
    // End of variables declaration//GEN-END:variables
}
