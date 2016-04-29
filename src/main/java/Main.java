import base.View;
import base.MessageStatus;
import base.Connection;
import base.Protocol;
import factory.FactorySetup;
import helpers.ReflectionHelper;
import message.Message;
import message.MessageParseExec;
import settings.Setup;
import settings.Text;

public class Main {

    public static void main(String[] args) {

        Text text = (Text) FactorySetup.getClazz("text.xml");
        Setup setup = (Setup) FactorySetup.getClazz("setup.xml");
        View messageWork = (View) ReflectionHelper.createInstance(setup.messageWork);
        Connection connection = (Connection) ReflectionHelper.createInstance(setup.connection);

        if (connection.init()) {
            while (true) {
                if (!connection.isAlive()) break;
                Message message = connection.read();
                if(message.getStatus() == MessageStatus.NOCONNECT) break;
                MessageParseExec.execute(Protocol.valueOf(setup.protocol),message);
                if (message.getStatus()!= MessageStatus.NOANSWER){
                    connection.write(message);
                }
                //messageWork.print(message.getRxHexString());
                messageWork.print(message.getRxDecode());
                messageWork.print(message.getTxDecode());
                //messageWork.print(message.getTxHexString());
            }
        }
    }
}
