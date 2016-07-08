package message.parsers;

import base.MessageStatus;
import base.ParseMessage;
import message.Message;
import static base.View.logView.ORIGINAL;

public class NoParser implements ParseMessage {
    public NoParser() {
    }

    @Override
    public void execute(Message message) {
        message.setRxDecode(message.getLogRx(ORIGINAL));
        message.setStatus(MessageStatus.NOANSWER);
    }

}
