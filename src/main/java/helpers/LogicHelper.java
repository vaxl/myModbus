package helpers;



public final class LogicHelper {
    private final static int maskLo    = 0xff;
    private final static int maskHi    = 0xff00;
    public final static int mask3Byte = 0xff0000;
    public final static int mask4Byte = 0xff000000;
    public final static int maskLastBit = 0x000001;

    public static int twoByte2Int(byte a, byte b) {
        return (((a & maskLo)<<8) | (b & maskLo)) ;
    }
    public static byte int2ByteHi(int a) {
        return (byte)( (a & maskHi)>>>8);
    }
    public static byte int2ByteLo(int a) {
        return (byte)(a & maskLo);
    }
    public static byte getByte(long data,int N) {
        return (byte)(data & (maskLo<<(N*8)));
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
    public static int crc16(byte[] arr){
        int sum=0xffff;
        for (byte anArr : arr) {
            sum = (sum ^ (anArr & 0xff));
            for (int j = 0; j < 8; j++) {
                if ((sum & 0x1) == 1) {
                    sum >>>= 1;
                    sum = (sum ^ 0xA001);
                } else sum >>>= 1;
            }
        }
        return sum;
    }
}
