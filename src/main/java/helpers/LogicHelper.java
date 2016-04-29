package helpers;

import settings.Const;

public final class LogicHelper {
    public static int twoByte2Int(byte a, byte b) {
        return (((a & Const.maskLo)<<8) | (b & Const.maskLo)) ;
    }
    public static byte int2ByteHi(int a) {
        return (byte)( (a & Const.maskHi)>>>8);
    }
    public static byte int2ByteLo(int a) {
        return (byte)(a & Const.maskLo);
    }
    public static int bitInByte(int a) {
        if (a % 8 == 0) return a/8;
        return a/8+1;
    }
    public static byte[] strByteToByte(String text){
        String [] data = text.split(" ");
        byte[] res = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] =  Byte.valueOf(data[i]);
        }
        return  res;
    }
    public static byte[] textToByte(String text){
        char[] temp = text.toCharArray();
        byte[] res = new byte[temp.length];
        for (int i = 0; i < temp.length; i++) {
            res[i] = int2ByteLo(temp[i]);
        }
        return  res;
    }
}
