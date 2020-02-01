
package eu.doppel_helix.papertrail.papertrailprofileranalysis;

import javax.swing.table.AbstractTableModel;

public class StackTraceTableModel extends AbstractTableModel {

    private StackTrace stackTrace = null;

    public StackTrace getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(StackTrace stackTrace) {
        this.stackTrace = stackTrace;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        if(stackTrace == null) {
            return 0;
        } else {
            return stackTrace.getTraceElements().size();
        }
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (stackTrace == null) {
            return 0;
        } else {
            switch (column) {
                case 0:
                    return stackTrace.getTraceElements().get(row);
                default:
                    return null;
            }
        }
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch(column) {
            case 0:
                return String.class;
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch(column) {
            case 0:
                return "Location";
            default:
                return null;
        }
    }
}
