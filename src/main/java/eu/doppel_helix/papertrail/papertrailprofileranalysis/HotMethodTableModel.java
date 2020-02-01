
package eu.doppel_helix.papertrail.papertrailprofileranalysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class HotMethodTableModel extends AbstractTableModel {

    private List<HotMethodElement> elements = Collections.EMPTY_LIST;

    public List<HotMethodElement> getElements() {
        return elements;
    }

    public void setElements(Collection<? extends HotMethodElement> elements) {
        if(elements == null) {
            elements = Collections.EMPTY_LIST;
        }
        this.elements = new ArrayList<>(elements);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return elements.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int row, int column) {
        HotMethodElement hme = elements.get(row);
        switch(column) {
            case 0:
                return hme.getLocation();
            case 1:
                return hme.getTotalTime();
            case 2:
                return hme.getSelfTime();
            default:
                return null;
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
            case 1:
                return Long.class;
            case 2:
                return Long.class;
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch(column) {
            case 0:
                return "Location";
            case 1:
                return "Total";
            case 2:
                return "Self";
            default:
                return null;
        }
    }
}
