package com.szzt.smart.framework.util.encrypt;

/**
 * 16进制转换工具;
 * 
 * @author baiqirui
 * @version [版本号, 2012-9-13]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class HexStringUtil
{
    // 整数到字节数组转换
    public static byte[] int2bytes(int n)
    {
        byte[] ab = new byte[4];
        ab[0] = (byte)(0xff & n);
        ab[1] = (byte)((0xff00 & n) >> 8);
        ab[2] = (byte)((0xff0000 & n) >> 16);
        ab[3] = (byte)((0xff000000 & n) >> 24);
        return ab;
    }
    
    // 字节数组到整数的转换
    public static int bytes2int(byte b[])
    {
        int s = 0;
        s = ((((b[0] & 0xff) << 8 | (b[1] & 0xff)) << 8) | (b[2] & 0xff)) << 8 | (b[3] & 0xff);
        return s;
    }
    
    // 字节转换到字符
    public static char byte2char(byte b)
    {
        return (char)b;
    }
    
    private final static byte[] hex = "0123456789ABCDEF".getBytes();
    
    private static int parse(char c)
    {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }
    
    // 从字节数组到十六进制字符串转换
    public static String bytes2HexString(byte[] b)
    {
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++)
        {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }
    
    // 从十六进制字符串到字节数组转换
    public static byte[] hexString2Bytes(String hexstr)
    {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++)
        {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte)((parse(c0) << 4) | parse(c1));
        }
        return b;
    }
}