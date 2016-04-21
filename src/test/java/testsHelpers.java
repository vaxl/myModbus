import base.Protocol;
import factory.FactorySetup;
import helpers.LogicHelper;
import message.Message;
import message.MessageParseExec;
import message.ModbusSlaveTcpParser;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class testsHelpers {
    @Test
    public void testbitInByte(){
        assertEquals(1,LogicHelper.bitInByte(7));
        assertEquals(2,LogicHelper.bitInByte(9));
        assertEquals(1,LogicHelper.bitInByte(8));
        assertEquals(7,LogicHelper.bitInByte(52));
    }

    @Test
    public void testModbusSlaveTcpParser(){
        byte [] before = {1,58,0,0,0,6,1,1,0,0,0,7};
        byte [] res =  {1,58,0,0,0,4,1,1,1,1};

        FactorySetup factorySetup = new FactorySetup();
        factorySetup.readXml();
        Message message = new Message(before);
        MessageParseExec.execute(Protocol.MODBUSLAVETCP,message);
        assertArrayEquals(res,message.getTx());


    }
}
