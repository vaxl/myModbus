package view;

import controller.GuiController;

import javax.swing.table.AbstractTableModel;
import java.util.Map;


public class GuiModbusTableView<T> extends AbstractTableModel {
    private final static int ROWREG =0;
    private final static int ROWVAL =1;
    private Map<Integer,T> map;
    private GuiController controller;
    private final Class[] columnClass;


    public GuiModbusTableView(Map<Integer,T> map,GuiController controller) {
        this.map = map;
        this.controller = controller;
        T ob = (T) map.values().toArray()[0];
        columnClass = new Class[] {
                Integer.class, ob.getClass()
        };
    }

    private final String[] columnNames = new String[] {
            "Reg", "Value"
    };



    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return columnClass[columnIndex];
    }
    @Override
    public int getRowCount() {
        return map.size();
    }
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] keySet = map.keySet().toArray();
        if (ROWREG == columnIndex) return keySet[rowIndex];
        if (ROWVAL == columnIndex) return map.get(keySet[rowIndex]);
        return null;
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (ROWREG == columnIndex) return false;
        if (ROWVAL == columnIndex) return true;
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Object[] keySet = map.keySet().toArray();
        map.put((int)keySet[rowIndex],(T) aValue);
        controller.cmd("clearCach");
    }
}
