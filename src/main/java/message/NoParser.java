package message;

import base.MessageStatus;
import base.ParseMessage;

public class NoParser implements ParseMessage {
    public NoParser() {
    }

    @Override
    public void execute(Message message) {
        message.setRxDecode(message.getRxString());
        message.setStatus(MessageStatus.NOANSWER);
    }

}
