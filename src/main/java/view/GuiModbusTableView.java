package view;

import base.Database;
import base.RegTypes;
import controller.GuiController;
import database.Db;
import database.Entity.*;

import javax.swing.table.AbstractTableModel;

public class GuiModbusTableView extends AbstractTableModel  {
    private final static int ROWNAME =0;
    private final static int ROWREG =1;
    private final static int ROWVAL =2;
    private TableRegs tableRegs;
    private Database db = Db.getInstance();
    private GuiController controller;
    private final Class[] columnClass;

    GuiModbusTableView(GuiController controller, RegTypes type,int id) {
        tableRegs = new TableRegs(id,type);
        this.controller = controller;
        columnClass = new Class[] {
                String.class,Integer.class,Integer.class
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
        return db.sizeTable(tableRegs);
    }
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Registr reg = (Registr) db.readAll(tableRegs).toArray()[rowIndex];
        if (ROWREG == columnIndex) return reg.getReg();
        if (ROWVAL == columnIndex) return reg.getValue();
        if (ROWNAME == columnIndex) return reg.getName();
        return null;
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (ROWNAME == columnIndex || ROWVAL == columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Registr key = (Registr) db.readAll(tableRegs).toArray()[rowIndex];
        if (ROWVAL == columnIndex){

            key.setValue((int) aValue);
            db.update(key);
            controller.clearCach();
            if (tableRegs.getType().ordinal()>6 & tableRegs.getType().ordinal()<10 ) controller.event(new Registrs(key,1));
        }
        if (ROWNAME == columnIndex) {
            key.setName((String) aValue);
            db.update(key);
        }
    }
}
