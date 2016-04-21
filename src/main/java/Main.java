import base.LogWork;
import base.MessageStatus;
import base.PortWork;
import base.Protocol;
import factory.FactorySetup;
import helpers.ReflectionHelper;
import message.Message;
import message.MessageParseExec;
import settings.Setup;
import settings.Text;

public class Main {

    public static void main(String[] args) {

        FactorySetup factorySetup = new FactorySetup();
        factorySetup.readXml();
        Text text = (Text) FactorySetup.factory.get("text.xml");
        Setup setup = (Setup) FactorySetup.factory.get("setup.xml");
        LogWork messageWork = (LogWork) ReflectionHelper.createInstance(setup.messageWork);
        FactorySetup.factory.put("messageWork",messageWork);
        PortWork portWork = (PortWork) ReflectionHelper.createInstance(setup.portWork);

        if (portWork.start()) {
            while (true) {
                if (!portWork.isAlive()) break;
                Message message = portWork.read();
                if(message.getStatus() == MessageStatus.NOCONNECT) break;
                MessageParseExec.execute(Protocol.valueOf(setup.protocol),message);
                if (message.getStatus()!= MessageStatus.NOANSWER){
                    portWork.write(message);
                }
                messageWork.print(message.getRxToHexString());
                //messageWork.print(message.getTextRx());
                messageWork.print(message.getTextTx());
                messageWork.print(message.getTxToHexString());
            }
        }
    }
}
