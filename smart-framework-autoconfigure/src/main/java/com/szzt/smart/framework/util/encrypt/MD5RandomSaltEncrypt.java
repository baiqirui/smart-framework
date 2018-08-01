package com.szzt.smart.framework.util.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * 对数据进行MD5加密;
 * 
 * @author baiqirui
 * @version [版本号, 2012-9-13]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class MD5RandomSaltEncrypt
{
    private static final String HEX_NUMS_STR = "0123456789ABCDEF";
    
    private static final Integer SALT_LENGTH = 12;
    
    /**
     * 加密过程(接受用户输入的密码信息,进行加密并返回);
     * 
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String getEncryptedPwd(String info)
    {
        String pwd = null;
        // 1.获得随机盐数据;
        Random rand = new Random();
        byte[] salt = new byte[SALT_LENGTH];
        rand.nextBytes(salt);
        
        // 2.更新摘要信息,并加密后返回byte[]数组;
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(salt);
            messageDigest.update(info.getBytes("UTF-8"));
            byte[] dig = messageDigest.digest();
            // 创建一个新的数组将盐数据和最终摘要信息进行保存(前12位保存盐,后16位保存最终摘要信息);
            byte[] pwdArray = new byte[salt.length + dig.length];
            System.arraycopy(salt, 0, pwdArray, 0, salt.length);
            System.arraycopy(dig, 0, pwdArray, salt.length, dig.length);
            // 将此最终数组转换成String字符类型;
            pwd = byteToHexString(pwdArray);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return pwd;
    }
    
    /**
     * 解密;
     * 
     * @throws NoSuchAlgorithmException
     */
    public static boolean validPassword(String data, String pwd)
        throws Exception
    {
        boolean flag = false;
        // 将从数据库中获得数据转换成16位byte[]数组;
        byte[] pwdArray = hexStringToByte(data);
        // 创建盐数组,同时为盐数组copy数据;
        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(pwdArray, 0, salt, 0, salt.length);
        // 将用户输入的信息与获取的盐数据再次进行加密;
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(salt);
        m.update(pwd.getBytes("UTF-8"));
        byte[] newArray = m.digest();
        // 再次copy数组;
        byte[] infoArray = new byte[salt.length + newArray.length];
        System.arraycopy(salt, 0, infoArray, 0, salt.length);
        System.arraycopy(newArray, 0, infoArray, salt.length, newArray.length);
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
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }
}
