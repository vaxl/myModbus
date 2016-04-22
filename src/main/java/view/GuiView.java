package view;


import base.View;
import controller.GuiController;
import message.Message;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiView implements View {
    private GuiController controller;
    private JFrame frame = new JFrame("MyModbus");
    private JTextField textField = new JTextField(50);
    private JTextArea messages = new JTextArea(10, 50);

    public GuiView(GuiController controller) {
        this.controller = controller;
        init();
    }

    public void init(){
        textField.setEditable(true);
        messages.setEditable(false);

        frame.getContentPane().add(textField, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(messages), BorderLayout.WEST);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.cmd(textField.getText());
                textField.setText("");
            }
        });
    }

    @Override
    public void print(String text) {
        messages.append(text + "\n");
    }

    @Override
    public void print(Message text) {
        messages.append(text.getTextRx());
        messages.append(text.getTextTx());
    }

    @Override
    public String readText() {
        return null;
    }

    @Override
    public void setLogView(logView logView) {

    }
}
