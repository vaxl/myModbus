package view;

import base.*;
import controller.GuiController;
import database.Registr;
import factory.FactorySetup;
import message.Message;
import settings.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GuiView implements View {
    private static final String RESOURCES = "xls/";
    private Database db;
    private String dbType="none";
    private Setup setup = (Setup) FactorySetup.getClazz("setup.xml"); ;
    private Text text = (Text) FactorySetup.getClazz("text.xml");
    private logView logView= View.logView.valueOf(setup.logView);
    private GuiController controller;
    private Map<Integer,Map<RegTypes,GuiModbusTableView>> tabModels = new HashMap<>();
    private Map<Integer, JTabbedPane> tabsInMain = new HashMap<>();
    private JFrame frame = new JFrame(text.GUINAME);
    private JTextField cmdField = new JTextField(60);
    private JTextArea messages = new JTextArea(10,60);
    private JTabbedPane mainTab = new JTabbedPane();

    public GuiView(GuiController controller) {
        this.controller = controller;
    }

    public void init(){
        db = (Database) FactorySetup.getClazz("Database");
        JPanel panel = new JPanel();
        JToolBar toolBarLog = new JToolBar();
        toolBarLog.add(new JScrollPane(messages));
        JToolBar toolBarData = new JToolBar();
        toolBarData.add(mainTab);
        JMenuBar menuBar = new JMenuBar();
        cmdField.setEditable(true);
        messages.setEditable(false);
        menuBar.add(createLogMenu());
        menuBar.add(createProtocolMenu());
        menuBar.add(createConnectionMenu());
        menuBar.add(createDbMenu());
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(createToolMenu());
        panel.add((toolBarLog));
        panel.add(toolBarData);
        panel.add(cmdField);
        frame.getContentPane().add(menuBar, BorderLayout.NORTH);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        cmdField.addActionListener(e -> controller.cmdConsole(readText()));
    }

    @Override
    public void print(String text) {
        messages.insert(text + "\n",0);
    }

    @Override
    public void print(Message message) {
        if (message!=null) {
            switch (logView) {
                case ONLYERRORS:
                    if (message.getStatus() == MessageStatus.ERR) {
                        messages.insert(text.TX +  message.getLogTx(logView)+"\n",0);
                        messages.insert(text.RX +message.getLogRx(logView)+"\n",0);
                    }
                    break;
                case OFF:  break;
                default:
                    if (message.getStatus() != MessageStatus.NOANSWER) messages.insert(text.TX + message.getLogTx(logView)+"\n",0);
                    if (message.getStatus() != MessageStatus.SEND) messages.insert(text.RX + message.getLogRx(logView)+"\n",0);
            }
        }
    }

    @Override
    public String readText() {
        String text = cmdField.getText();
        cmdField.setText("");
        return text;
    }

    @Override
    public void createIdTab(){
        JFrame frame = new JFrame(text.ADDRES);
        frame.setLocation(300,300);
        JTextField id = new JTextField("1",10);
        JButton ok1 = new JButton(text.OK);
        frame.getContentPane().add(id, BorderLayout.WEST);
        frame.getContentPane().add(new JLabel(text.ADDRES), BorderLayout.NORTH);
        frame.getContentPane().add(ok1, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.pack();
        ok1.addActionListener(e -> {
            try {
                int curId = Integer.valueOf(id.getText());
                db.create(dbType, curId);
                startPaintTable(curId);
                frame.setVisible(false);
            }catch (NumberFormatException ignored) {}
        });
    }

    @Override
    public void dbChanged(int id,RegTypes regTypes) {
        tabModels.get(id).get(regTypes).fireTableDataChanged();
    }

    private JToolBar createToolMenu(){
        JToolBar toolBar = new JToolBar();
        JButton buttonStart = new JButton(text.GUISTART);
        JButton buttonClear = new JButton(text.GUICLEAR);
        JButton buttonSend = new JButton(text.GUISEND);
        JButton buttonStop = new JButton(text.GUISTOP);
        JButton buttonAdd = new JButton(text.ADDREGS);
        JButton buttonDb = new JButton(text.ADDDB);
        toolBar.add(buttonStart);
        toolBar.add(buttonStop);
        toolBar.add(buttonClear);
        toolBar.add(buttonSend);
        toolBar.add(buttonDb);
        toolBar.add(buttonAdd);
        buttonStart.addActionListener(e -> controller.start());
        buttonStop.addActionListener(e -> controller.stop());
        buttonDb.addActionListener(e -> controller.addDb());
        buttonClear.addActionListener(e -> messages.setText(""));
        buttonSend.addActionListener(e ->  controller.writeToPort(readText()));
        buttonAdd.addActionListener(e -> frameAddRegs());
        return toolBar;
    }

    private JMenu createLogMenu(){
        JMenu menuLog = new JMenu(text.GUILOGGER);
        ButtonGroup group = new ButtonGroup();
        for(View.logView v: View.logView.values()) {
            JRadioButtonMenuItem rad = new JRadioButtonMenuItem(v.name());
            group.add(rad);
            if(setup.logView.equals(v.name())) rad.setSelected(true);
            rad.addActionListener(e -> logView = View.logView.valueOf(e.getActionCommand()));
            menuLog.add(rad);
        }
        return menuLog;
    }
    
    private JMenu createDbMenu(){
        JMenu menuDb = new JMenu(text.DB);
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem none = new JRadioButtonMenuItem("none");
        JRadioButtonMenuItem test = new JRadioButtonMenuItem("test");
        group.add(none);
        group.add(test);
        none.setSelected(true);
        menuDb.add(none);
        menuDb.add(test);
        none.addActionListener(e -> dbType="none");
        test.addActionListener(e -> dbType="test");

        File file = new File(RESOURCES);
        File [] files = file.listFiles();
        if (files==null) return menuDb;
        for(File xls : files) {
            JRadioButtonMenuItem rad = new JRadioButtonMenuItem(xls.getName());
            group.add(rad);
            rad.addActionListener(e -> dbType=e.getActionCommand());
            menuDb.add(rad);
        }
        return menuDb;
    }

    private JMenu createProtocolMenu(){
        JMenu menuProtocol = new JMenu(text.GUIPROTOCOL);
        ButtonGroup group = new ButtonGroup();
        for(Protocol v: Protocol.values()) {
            JRadioButtonMenuItem rad = new JRadioButtonMenuItem(v.name());
            group.add(rad);
            if(setup.protocol.equals(v.name())) rad.setSelected(true);
            rad.addActionListener(e -> controller.setProtocol(e.getActionCommand()));
            menuProtocol.add(rad);
        }
        return menuProtocol;
    }

    private JMenu createConnectionMenu(){
        JMenu menuConnection = new JMenu(text.GUICONNECTION);
        ButtonGroup group = new ButtonGroup();
        for(EConnection v: EConnection.values()) {
            JRadioButtonMenuItem rad = new JRadioButtonMenuItem(v.name());
            group.add(rad);
            if(setup.connection.equals(v.name())) rad.setSelected(true);
            rad.addActionListener(e -> controller.setConnection(e.getActionCommand()));
            menuConnection.add(rad);
        }
        return menuConnection;
    }

    private void frameAddRegs(){
        JFrame frame = new JFrame(text.ENTERREGS);
        frame.setLocation(300,300);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        JTextField reg = new JTextField("0",5);
        JTextField num = new JTextField("1",5);
        JComboBox<RegTypes> types = new JComboBox<>(RegTypes.values());
        JTextField id = new JTextField("1",5);
        JButton ok = new JButton(text.OK);
        panel.add(new JLabel(text.ADDRES));
        panel.add(id);
        panel.add(new JLabel(text.FUNCTION));
        panel.add(types);
        panel.add(new JLabel(text.REGISTR));
        panel.add(reg);
        panel.add(new JLabel(text.NUMBER));
        panel.add(num);
        JLabel error = new JLabel("      ");
        panel.add(error);
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(ok, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.pack();

        ok.addActionListener(e -> {
            try {
                controller.addRegs(new Registr(Integer.valueOf(id.getText()), Integer.valueOf(reg.getText()),RegTypes.values()[types.getSelectedIndex()]), Integer.valueOf(num.getText()));
                startPaintTable(Integer.valueOf(id.getText()));
                frame.setVisible(false);
            }catch (Exception ex) {error.setText(text.ERRREGS);}
        });
    }

    private void startPaintTable(int id){
        if(db!=null) {
            tabsInMain.putIfAbsent(id,new JTabbedPane());
            JTabbedPane tabsReg = tabsInMain.get(id);
            boolean newTab = !tabModels.containsKey(id);
            tabModels.putIfAbsent(id, new HashMap<>());
            for (RegTypes r : RegTypes.values()) {
                if (db.sizeTable(r, id) == 0) continue;
                if (tabModels.get(id).get(r)==null) {
                    GuiModbusTableView model = new GuiModbusTableView(controller, r, id);
                    tabModels.get(id).put(r, model);
                    JTable table = new JTable(model);
                    JScrollPane paneReg = new JScrollPane(table);
                    tabsReg.addTab(r.name(), paneReg);
                }
                else   tabModels.get(id).get(r).fireTableStructureChanged();
                }
            if (newTab) mainTab.addTab(id+"",tabsReg);
            frame.pack();
        } else print(text.ERRDB);
    }
}
