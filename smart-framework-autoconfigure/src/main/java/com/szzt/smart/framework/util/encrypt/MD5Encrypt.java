package com.szzt.smart.framework.util.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 对数据进行MD5加密(该MD5加密没有加随机盐);
 * 
 * @author baiqirui
 * @version [版本号, 2012-9-13]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class MD5Encrypt
{
    private static final String HEX_NUMS_STR = "0123456789ABCDEF";
    
    /**
     * 加密过程(接受用户输入的密码信息,进行加密并返回);
     * 
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String getEncryptedPwd(String info)
    {
        String pwd = null;
        
        // 2.更新摘要信息,并加密后返回byte[]数组;
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(info.getBytes("UTF-8"));
            byte[] dig = messageDigest.digest();
            // 将此最终数组转换成String字符类型;
            pwd = byteToHexString(dig);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return pwd;
    }
    
    /**
     * 解密过程
     * 
     * @param data [加密的数据]
     * @param pwd [源数据]
     * @return
     * @throws Exception [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean validPassword(String data, String pwd)
        throws Exception
    {
        boolean flag = false;
        // 将用户输入的信息与获取的盐数据再次进行加密;
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(pwd.getBytes("UTF-8"));
        byte[] newArray = m.digest();
        // 再次copy数组;
        byte[] infoArray = new byte[newArray.length];
        System.arraycopy(newArray, 0, infoArray, 0, newArray.length);
        // 将最终byte[]数组转换为String字符类型;
        String info = byteToHexString(infoArray);
        if (info.equals(data))
        {
            flag = true;
        }
        
        return flag;
    }
    
    /**
     * 将16进制字符串转换成字节数组
     * 
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex)
    {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] hexChars = hex.toCharArray();
        for (int i = 0; i < len; i++)
        {
            int pos = i * 2;
            result[i] = (byte)(HEX_NUMS_STR.indexOf(hexChars[pos]) << 4 | HEX_NUMS_STR.indexOf(hexChars[pos + 1]));
        }
        return result;
    }
    
    /**
     * 将指定byte数组转换成16进制字符串
     * 
     * @param b
     * @return
     */
    public static String byteToHexString(byte[] b)
    {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++)
        {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    public static void main(String[] args)
        throws Exception
    {
        System.out.println(getEncryptedPwd("baiqirui"));
        System.out.println(validPassword("66e792ac19995c7e6f462d60b03ea6cd", "baiqirui"));
    }
}