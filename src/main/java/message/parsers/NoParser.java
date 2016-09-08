package message.parsers;

import base.MessageStatus;
import base.ParseMessage;
import database.Entity.DiagRegistrs;
import message.Message;

import java.util.Arrays;


public class NoParser implements ParseMessage {
    public NoParser() {
    }

    @Override
    public void execute(Message message) {
        message.setRegs(new DiagRegistrs(Arrays.toString(message.getRx())));
        message.setStatus(MessageStatus.NOANSWER);
    }

}
