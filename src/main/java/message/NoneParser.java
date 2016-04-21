package message;

import base.MessageStatus;
import base.ParseMessage;

class NoneParser implements ParseMessage {

    @Override
    public void execute(Message message) {
        message.setStatus(MessageStatus.NOANSWER);
    }
}
