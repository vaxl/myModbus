package view;


import base.*;
import controller.GuiController;
import factory.FactorySetup;
import message.Message;
import settings.Setup;
import settings.Text;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class GuiView implements View {
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
    private JButton buttonDb = new JButton("DB");
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu = new JMenu(text.GUICONNECT);
    private JMenu menuLog = new JMenu(text.GUILOGGER);
    private JMenuItem menuStart = new JMenuItem(text.GUISTART);
    private JMenuItem menuStop = new JMenuItem(text.GUISTOP);
    private JMenu menuProtocol = new JMenu(text.GUIPROTOCOL);
    private JMenu menuConnection = new JMenu(text.GUICONNECTION);
    private JTabbedPane tabs = new JTabbedPane();
    private JPanel panel = new JPanel();
    private Map<RegistrsTypes,GuiModbusTableView> tabModels;

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

        menuBar.add(menu);
        menuBar.add(menuLog);
        menuBar.add(menuProtocol);
        menuBar.add(menuConnection);

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
            controller.cmd(cmdField.getText());
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
                case ORIGINAL:
                    if (message.getStatus() != MessageStatus.NOANSWER) messages.insert(message.getTxString()+"\n",0);
                    messages.insert(message.getRxString()+"\n",0);
                    break;
                case HEX:
                    if (message.getStatus() != MessageStatus.NOANSWER) messages.insert(message.getTxHexString()+"\n",0);
                    messages.insert(message.getRxHexString()+"\n",0);
                    break;
                case TEXT:
                    if (message.getStatus() != MessageStatus.NOANSWER) messages.insert(message.getTxText()+"\n",0);
                    messages.insert(message.getRxText()+"\n",0);
                    break;
                case DECODE:
                    if (message.getStatus() != MessageStatus.NOANSWER) messages.insert(message.getTxDecode()+"\n",0);
                    messages.insert(message.getRxDecode()+"\n",0);
                    break;
                case ONLYERRORS:
                    if (message.getStatus() == MessageStatus.ERR) {
                        messages.insert(message.getTxDecode() + "\n", 0);
                        messages.insert(message.getRxDecode() + "\n", 0);
                    }
                    break;
                case OFF:
                    break;
                default:
                    if (message.getStatus() != MessageStatus.NOANSWER) messages.insert(message.getTxDecode()+"\n",0);
                    messages.insert(message.getRxDecode()+"\n",0);
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

        buttonStart.addActionListener(e -> controller.cmd("run"));
        buttonDb.addActionListener(e -> controller.cmd("addDb"));
        buttonStop.addActionListener(e -> controller.cmd("stop"));
        buttonClear.addActionListener(e -> messages.setText(""));
        buttonSend.addActionListener(e -> {
            controller.cmd("tx " + cmdField.getText());
            cmdField.setText("");
        });
    }

    private void createLogMenu(){
        ButtonGroup group = new ButtonGroup();
        for(View.logView v: View.logView.values()) {
            JRadioButtonMenuItem rad = new JRadioButtonMenuItem(v.name());
            group.add(rad);
            rad.addActionListener(e -> controller.cmd("setlog " + e.getActionCommand()));
            menuLog.add(rad);
        }
    }



    private void createProtocolMenu(){
        ButtonGroup group = new ButtonGroup();
        for(Protocol v: Protocol.values()) {
            JRadioButtonMenuItem rad = new JRadioButtonMenuItem(v.name());
            group.add(rad);
            if(setup.protocol.equals(v.name())) rad.setSelected(true);
            rad.addActionListener(e -> controller.cmd("setProtocol " + e.getActionCommand()));
            menuProtocol.add(rad);
        }
    }

    private void createConnectionMenu(){
        ButtonGroup group = new ButtonGroup();
        for(EConnection v: EConnection.values()) {
            JRadioButtonMenuItem rad = new JRadioButtonMenuItem(v.name());
            group.add(rad);
            if(setup.connection.equals(v.name())) rad.setSelected(true);
            rad.addActionListener(e -> controller.cmd("setProtocol " + e.getActionCommand()));
            menuConnection.add(rad);
        }


    }

    public void createTable(){
        Database db = (Database) FactorySetup.getClazz("Database");
        if(db!=null  & db!=null) {
            tabs.removeAll();
            for(RegistrsTypes r : RegistrsTypes.values()) {
                Map map = db.getMap(r);
                if (map==null) continue;
                GuiModbusTableView model = new GuiModbusTableView(map, controller);
                tabModels= new HashMap<>();
                tabModels.put(r,model);
                JTable table = new JTable(model);
                JScrollPane pane = new JScrollPane(table);
                tabs.addTab(r.name(), pane);
                toolBarData.add(tabs);
                frame.pack();
            }
        } else print(text.ERRDB);
    }

    @Override
    public void dbChanged() {
        for (RegistrsTypes r: RegistrsTypes.values()) {
            GuiModbusTableView tabModel = tabModels.get(r);
            if (tabModel!=null)
                tabModel.fireTableDataChanged();
        }
    }
}