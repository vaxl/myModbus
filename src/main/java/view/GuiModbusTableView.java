package view;

import base.Database;
import base.RegistrsTypes;
import controller.GuiController;

import javax.swing.table.AbstractTableModel;
import java.util.Map;


public class GuiModbusTableView<T> extends AbstractTableModel {
    private final static int ROWNAME =0;
    private final static int ROWREG =1;
    private final static int ROWVAL =2;
    private Map<Integer,T> map;
    private RegistrsTypes type;
    private Database db;
    private GuiController controller;
    private final Class[] columnClass;


    GuiModbusTableView(Map<Integer,T> map, GuiController controller, Database db, RegistrsTypes type) {
        this.map = map;
        this.db = db;
        this.type = type;
        this.controller = controller;
        T ob = (T) map.values().toArray()[0];
        columnClass = new Class[] {
                String.class,Integer.class, ob.getClass()
        };
    }

    private final String[] columnNames = new String[] {
            "Name","Reg", "Value"
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
        if (ROWNAME == columnIndex) return db.getName((int)keySet[rowIndex],type);
        return null;
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (ROWNAME == columnIndex || ROWVAL == columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Object[] keySet = map.keySet().toArray();
        if (ROWVAL == columnIndex){
        map.put((int)keySet[rowIndex],(T) aValue);
        controller.cmd("clearCach");
        }
        if (ROWNAME == columnIndex) {
            db.setName((int)keySet[rowIndex],type,(String) aValue);
        }
    }
}
