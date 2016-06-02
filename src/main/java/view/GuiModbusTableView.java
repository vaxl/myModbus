package view;

import base.Database;
import base.RegTypes;
import controller.GuiController;
import exeptions.NoSuchRegistrs;
import factory.FactorySetup;
import javax.swing.table.AbstractTableModel;

public class GuiModbusTableView extends AbstractTableModel  {
    private final static int ROWNAME =0;
    private final static int ROWREG =1;
    private final static int ROWVAL =2;
    private RegTypes type;
    private int id;
    private Database db = (Database) FactorySetup.getClazz("Database");
    private GuiController controller;
    private final Class[] columnClass;

    GuiModbusTableView(GuiController controller, RegTypes type,int id) {
        this.type = type;
        this.id = id;
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
        return db.sizeTable(type,id);
    }
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (ROWREG == columnIndex) return db.readReg(type,rowIndex,id);
        if (ROWVAL == columnIndex) return db.readValue(type,rowIndex,id);
        if (ROWNAME == columnIndex) return db.readName(type,rowIndex,id);
        return null;
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (ROWNAME == columnIndex || ROWVAL == columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        int key = db.readReg(type,rowIndex,id);
        if (ROWVAL == columnIndex){
            try {
                db.setValue(key,type,(int) aValue,id);
            } catch (NoSuchRegistrs ignored) { }
            controller.clearCach();
            controller.event(type,key,id);
        }
        if (ROWNAME == columnIndex) {
            db.setName(key,type,(String) aValue,id);
        }
    }
}
