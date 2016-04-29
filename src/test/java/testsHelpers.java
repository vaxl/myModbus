import base.Protocol;
import base.RegistrsTypes;
import database.RegistrsHashMap;
import exeptions.NoSuchRegistrs;
import factory.FactorySetup;
import helpers.LogicHelper;
import message.Message;
import message.MessageParseExec;
import message.ModbusSlaveTcpParser;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class testsHelpers {
    @Test
    public void testbitInByte(){
        assertEquals(1,LogicHelper.bitInByte(7));
        assertEquals(2,LogicHelper.bitInByte(9));
        assertEquals(1,LogicHelper.bitInByte(8));
        assertEquals(7,LogicHelper.bitInByte(52));
    }

    @Test
    public void testEatString(){
        ModbusSlaveTcpParser mod = new ModbusSlaveTcpParser();
        String in = "1 2 3 4 5 6 7 8";
        String out = "4 5 6 7 8";
        String res = mod.eatStringSpace(in,3);
        assertTrue(res.equals(out));
    }

    @Test
    public void testModbusSlaveTcpParser(){
        byte [] before = {1,58,0,0,0,6,1,1,0,100,0,10};
        byte [] before2 = {111,111,0,0,0,6,1,1,0,100,0,10};
        byte [] res =    {1,58,0,0,0,5,1,1,2,-86,2};
        byte [] res2 =    {111,111,0,0,0,5,1,1,2,-86,2};

        FactorySetup factorySetup = new FactorySetup();
        factorySetup.readXml();
        Message message = new Message(before);
        MessageParseExec.execute(Protocol.ModbusSlaveTcp,message);
        assertArrayEquals(res,message.getTx());
        message = new Message(before2);
        MessageParseExec.execute(Protocol.ModbusSlaveTcp,message);
        assertArrayEquals(res2,message.getTx());

    }

    @Test(expected = NoSuchRegistrs.class)
    public void erorDBTest() throws Exception{
        RegistrsHashMap reg = new RegistrsHashMap();
        reg.create();
        reg.read(0,10, RegistrsTypes.DINPUT);
    }

    @Test
    public void testDbBitsGet() throws Exception{
        byte [] res =  {-86,2};
        byte [] res2 =  {85,1};
        byte [] res3 =  {0,0,0,1,0,2,0,3,0,4,0,5,0,6,0,7,0,8,0,9};
        byte [] test;
        RegistrsHashMap reg = new RegistrsHashMap();
        reg.create();
        test = reg.read(100,10, RegistrsTypes.COILS);
        assertArrayEquals(res,test);
        test = reg.read(200,10, RegistrsTypes.DINPUT);
        assertArrayEquals(res2,test);
        test = reg.read(300,10, RegistrsTypes.HOLDING);
        assertArrayEquals(res3,test);
    }
}
