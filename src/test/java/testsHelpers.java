import base.Database;
import base.MessageStatus;
import base.Protocol;
import base.RegTypes;
import database.RegistrsHashMap;
import exeptions.NoSuchRegistrs;
import factory.FactorySetup;
import helpers.LogicHelper;
import message.Message;
import message.MessageParseExec;
import message.parsers.ModbusSlaveTcpParser;
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
        FactorySetup factorySetup = new FactorySetup();
        FactorySetup.addToFactory("Database",new RegistrsHashMap());
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
        byte [] res =    {1  ,58 ,0,0,0,5,1,1,2,85,1};
        byte [] res2 =   {111,111,0,0,0,5,1,1,2,85,1};

        FactorySetup.readXml();
        Database db = new RegistrsHashMap();
        db.create("test",1);
        FactorySetup.addToFactory("Database",db);
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
        reg.create("test",1);
        reg.read(0,10, RegTypes.DINPUT,1);
    }
    @Test
    public void testDbBitsGet() throws Exception{
        byte [] res =   {85,1};
        byte [] res2 =  {85,1};
        byte [] res3 =  {0,0,0,1,0,2,0,3,0,4,0,5,0,6,0,7,0,8,0,9};
        byte [] test;
        RegistrsHashMap reg = new RegistrsHashMap();
        int id =1;
        reg.create("test",id);
        test = reg.read(100,10, RegTypes.COILS,id);
        assertArrayEquals(res,test);
        test = reg.read(200,10, RegTypes.DINPUT,id);
        assertArrayEquals(res2,test);
        test = reg.read(300,10, RegTypes.HOLDING,id);
        assertArrayEquals(res3,test);
    }
    @Test
    public void testCrc() {
        byte[] in = {1,3,0,0,0,10};
        byte[] in1 = {1,1,2,LogicHelper.int2ByteLo(0xAA),2};
        int res = LogicHelper.crc16(in);
        byte hi = LogicHelper.int2ByteHi(res);
        byte lo = LogicHelper.int2ByteLo(res);
        assertTrue(Byte.toUnsignedInt(hi)==205);
        assertTrue(Byte.toUnsignedInt(lo)==197);
        res = LogicHelper.crc16(in1);
        assertTrue(res==40262);
    }
    @Test
    public void testIec104s() {
        byte[] in = {0x68,0xE,0,0,0,0,0x64,1,6,1,1,0,0,0,0,0x14};
        byte[] out = {0x68,0xE,0,0,2,0,0x64,1,7,1,1,0,0,0,0,0x14,0x68,0xE,2,0,2,0,0x64,1,10,1,1,0,0,0,0,0x14};
        FactorySetup.readXml();
        Database db = new RegistrsHashMap();
        db.clearDb();
        db.create("none",2);
        FactorySetup.addToFactory("Database",db);
        Message message = new Message(in);
        MessageParseExec.execute(Protocol.IEC104Client,message);
        assertArrayEquals(out,message.getTx());
    }

    @Test
    public void testIec104() {
        byte[] in = {0x68,0xE,0,0,0,0,0x64,1,6,1,1,0,0,0,0,0x14};
        //byte[] out = {0x68,0xE,0,0,2,0,0x64,1,7,1,1,0,0,0,0,0x14,0x68,0xE,2,0,2,0,0x64,1,10,1,1,0,0,0,0,0x14};
        FactorySetup.readXml();
        Database db = new RegistrsHashMap();
        db.create("test",1);
        FactorySetup.addToFactory("Database",db);
        Message message = new Message(in);
        MessageParseExec.execute(Protocol.IEC104Client,message);
    }
    @Test
    public void testIec104Event() {
        byte[] in = {7,LogicHelper.int2ByteHi(200),LogicHelper.int2ByteLo(200),1};
        byte[] out ={0x68,21,0,0,0,0,30,1,3,1,1,0,LogicHelper.int2ByteLo(200),0,0,1};
        FactorySetup.readXml();
        Database db = new RegistrsHashMap();
        db.create("test",1);
        FactorySetup.addToFactory("Database",db);
        Message message = new Message(in);
        message.setStatus(MessageStatus.SEND);
        MessageParseExec.execute(Protocol.IEC104Client,message);
        byte[] arr = new byte[16];
        System.arraycopy(message.getTx(),0,arr,0,16);
        assertArrayEquals(out,arr);
    }

}
