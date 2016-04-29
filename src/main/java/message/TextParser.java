package message;


import base.MessageStatus;
import base.ParseMessage;

public class TextParser implements ParseMessage{
    @Override
    public void execute(Message message) {
        message.setRxDecode(message.getRxText());
        message.setStatus(MessageStatus.NOANSWER);
    }

}
