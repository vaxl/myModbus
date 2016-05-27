package view;

import base.*;
import controller.GuiController;
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
    private String dbType="none";
    private logView logView;
    private Setup setup ;
    private Text text = (Text) FactorySetup.getClazz("text.xml");
    private GuiController controller;
    private JFrame frame = new JFrame(text.GUINAME);
    private JTextField cmdField = new JTextField(60);
    private JTextArea messages = new JTextArea(10,60);
    private JToolBar toolBar = new JToolBar();
    private JToolBar toolBarLog = new JToolBar();
    private JToolBar toolBarData = new JToolBar();
    private JButton buttonStart = new JButton(text.GUISTART);
    private JButton buttonClear = new JButton(text.GUICLEAR);
    private JButton buttonSend = new JButton(text.GUISEND);
    private JButton buttonStop = new JButton(text.GUISTOP);
    private JButton buttonAdd = new JButton("AddRegs");
    private JButton buttonDb = new JButton("AddDB");
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu = new JMenu(text.GUICONNECT);
    private JMenu menuLog = new JMenu(text.GUILOGGER);
    private JMenuItem menuStart = new JMenuItem(text.GUISTART);
    private JMenuItem menuStop = new JMenuItem(text.GUISTOP);
    private JMenu menuProtocol = new JMenu(text.GUIPROTOCOL);
    private JMenu menuConnection = new JMenu(text.GUICONNECTION);
    private JMenu menuDb = new JMenu("Database");
    private JTabbedPane tabs = new JTabbedPane();
    private JTabbedPane mainTab = new JTabbedPane();
    private JPanel panel = new JPanel();
    private Map<RegTypes,GuiModbusTableView> tabModels;
    private Map<Integer, Map<RegTypes,GuiModbusTableView>> mainTabs = new HashMap<>();
    int id;

    public GuiView(GuiController controller) {
        this.controller = controller;
    }

    public void init(){
        setup = (Setup) FactorySetup.getClazz("setup.xml");
        cmdField.setEditable(true);
        messages.setEditable(false);

        createConnectMenu();
        createToolMenu();
        createLogMenu();
        createProtocolMenu();
        createConnectionMenu();
        createDbMenu();

        menuBar.add(menu);
        menuBar.add(menuLog);
        menuBar.add(menuProtocol);
        menuBar.add(menuConnection);
        menuBar.add(menuDb);

        toolBarLog.add(new JScrollPane(messages));

        frame.getContentPane().add(menuBar, BorderLayout.NORTH);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(toolBar);
        panel.add((toolBarLog));
        panel.add(toolBarData);
        panel.add(cmdField);
        frame.setVisible(true);
        frame.pack();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        cmdField.addActionListener(e -> {
            controller.cmdConsole(cmdField.getText());
            cmdField.setText("");
        });
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
                case OFF:
                    break;
                default:
                    if (message.getStatus() != MessageStatus.NOANSWER) messages.insert(text.TX + message.getLogTx(logView)+"\n",0);
                    if (message.getStatus() != MessageStatus.SEND) messages.insert(text.RX + message.getLogRx(logView)+"\n",0);
                    break;
            }
        }
    }

    @Override
    public String readText() {
        return null;
    }

    @Override
    public void setLogView(View.logView logView) {
        this.logView = logView;
    }

    private void createConnectMenu(){
        menu.add(menuStart);
        menu.add(menuStop);

        menuStart.addActionListener(e -> buttonStart.doClick());
        menuStop.addActionListener(e -> buttonStop.doClick());
    }

    private void createToolMenu(){
        toolBar.add(buttonStart);
        toolBar.add(buttonStop);
        toolBar.add(buttonClear);
        toolBar.add(buttonSend);
        toolBar.add(buttonDb);
        toolBar.add(buttonAdd);

        buttonStart.addActionListener(e -> controller.start());
        buttonDb.addActionListener(e -> controller.addDb());
        buttonStop.addActionListener(e -> controller.stop());
        buttonClear.addActionListener(e -> messages.setText(""));
        buttonSend.addActionListener(e -> {
            controller.writeToPort(cmdField.getText());
            cmdField.setText("");
        });
        buttonAdd.addActionListener(e -> contexDb());
    }

    private void createLogMenu(){
        ButtonGroup group = new ButtonGroup();
        for(View.logView v: View.logView.values()) {
            JRadioButtonMenuItem rad = new JRadioButtonMenuItem(v.name());
            group.add(rad);
            if(setup.logView.equals(v.name())) rad.setSelected(true);
            rad.addActionListener(e -> controller.setLog(e.getActionCommand()));
            menuLog.add(rad);
        }
    }
    
    private void createDbMenu(){
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
        if (files==null) return;
        for(File xls : files) {
            JRadioButtonMenuItem rad = new JRadioButtonMenuItem(xls.getName());
            group.add(rad);
            rad.addActionListener(e -> dbType=e.getActionCommand());
            menuDb.add(rad);
        }
    }
    @Override
    public String getDbType(){
        return dbType;
    }
    private void createProtocolMenu(){
        ButtonGroup group = new ButtonGroup();
        for(Protocol v: Protocol.values()) {
            JRadioButtonMenuItem rad = new JRadioButtonMenuItem(v.name());
            group.add(rad);
            if(setup.protocol.equals(v.name())) rad.setSelected(true);
            rad.addActionListener(e -> controller.setProtocol(e.getActionCommand()));
            menuProtocol.add(rad);
        }
    }

    private void createConnectionMenu(){
        ButtonGroup group = new ButtonGroup();
        for(EConnection v: EConnection.values()) {
            JRadioButtonMenuItem rad = new JRadioButtonMenuItem(v.name());
            group.add(rad);
            if(setup.connection.equals(v.name())) rad.setSelected(true);
            rad.addActionListener(e -> controller.setConnection(e.getActionCommand()));
            menuConnection.add(rad);
        }
    }

    @Override
    public void createTable(){
        JFrame frame = new JFrame("ID");
        JTextField id = new JTextField(10);
        JButton ok1 = new JButton("OK");
        frame.getContentPane().add(id, BorderLayout.WEST);
        frame.getContentPane().add(new JLabel("ID"), BorderLayout.NORTH);
        frame.getContentPane().add(ok1, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.pack();
        ok1.addActionListener(e -> {
            setId(Integer.valueOf(id.getText()));
            startCreateTable();
            frame.setVisible(false);
        });
    }

    @Override
    public void dbChanged() {
        for (RegTypes r: RegTypes.values()) {
            GuiModbusTableView tabModel = tabModels.get(r);
            if (tabModel!=null)
                tabModel.fireTableDataChanged();
        }
        tabs.repaint();
    }

    private void contexDb(){
        JFrame frame = new JFrame("DB");
        JPanel panel = new JPanel();
        JTextField reg = new JTextField(10);
        JTextField num = new JTextField(10);
        JTextField type = new JTextField(10);
        JButton ok = new JButton("OK");
        panel.add(new JLabel("Registr"));
        panel.add(reg);
        panel.add(new JLabel("Number"));
        panel.add(num);
        panel.add(new JLabel("Type"));
        panel.add(type);

        ok.addActionListener(e -> {
            controller.addRegs(reg.getText(),num.getText(),type.getText());
            frame.setVisible(false);
        });

        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(ok, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.pack();
    }

    private void startCreateTable(){
        Database db = (Database) FactorySetup.getClazz("Database");
        if(db!=null) {
            for(RegTypes r : RegTypes.values()) {
                if (db.sizeTable(r)==0) continue;
                GuiModbusTableView model = new GuiModbusTableView(controller,r);
                tabModels= new HashMap<>();
                tabModels.put(r,model);
                JTable table = new JTable(model);
                JScrollPane pane = new JScrollPane(table);
                tabs.addTab(r.name(), pane);
            }
            mainTab.addTab(id+"",tabs);
            toolBarData.add(mainTab);
            frame.pack();
        } else print(text.ERRDB);
    }

    public void setId(int id) {
        this.id = id;
    }
}
