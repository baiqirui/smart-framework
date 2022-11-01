package com.szzt.smart.framework.util.encrypt;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * 字符串 DESede(3DES) 加密
 * 
 * @author baiqirui
 * @version [版本号, 2012-9-13]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Slf4j
public class DESEncrypt
{
    private static final String Algorithm = "DES"; // 定义 加密算法,可用 DES,DESede,Blowfish
    
    private static final String transformation = "DES/ECB/NoPadding";// NoPadding ，PKCS5Padding
    
    // keybyte为加密密钥，长度为24字节
    
    // src为被加密的数据缓冲区（源）
    
    public static byte[] encryptMode(byte[] keybyte, byte[] src)
    {
        try
        {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            
            // 加密
            Cipher c1 = Cipher.getInstance(transformation);
            
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            
            return c1.doFinal(src);
            
            // 加密成功后返回
            
        }
        catch (java.security.NoSuchAlgorithmException e1)
        {
            e1.printStackTrace();
        }
        catch (javax.crypto.NoSuchPaddingException e2)
        {
            e2.printStackTrace();
        }
        catch (Exception e3)
        {
            e3.printStackTrace();
        }
        
        return null;
        // 失败返回null
    }
    
    // keybyte为加密密钥，长度为24字节
    // src为加密后的缓冲区
    
    public static byte[] decryptMode(byte[] keybyte, byte[] src)
    {
        try
        {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            // 解密
            Cipher c1 = Cipher.getInstance(transformation);// ECB/PKCS5Padding
            
            c1.init(Cipher.DECRYPT_MODE, deskey);
            
            return c1.doFinal(src);
            
        }
        catch (java.security.NoSuchAlgorithmException e1)
        {
            e1.printStackTrace();
        }
        catch (javax.crypto.NoSuchPaddingException e2)
        {
            e2.printStackTrace();
        }
        catch (Exception e3)
        {
            e3.printStackTrace();
        }
        
        return null;
        
    }
    
    // 转换成十六进制字符串
    public static String byte2hex(byte[] b)
    {
        String hs = "";
        String stmp = "";
        
        for (int n = 0; n < b.length; n++)
        {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            
            else
                hs = hs + stmp;
            
            if (n < b.length - 1)
                hs = hs + "";
        }
        return hs.toUpperCase();
    }
    
    public static void crypt(InputStream in, OutputStream out, Cipher cipher)
        throws Exception
    {
        int blockSize = cipher.getBlockSize();// blockSize = 8;
        int outputSize = cipher.getOutputSize(blockSize);// outputSize = 16。这个地方貌似是导致长度不一致的原因。
        byte[] inBytes = new byte[blockSize];
        byte[] outBytes = new byte[outputSize];
        
        int inLength = 0;
        boolean more = true;
        while (more)
        {
            inLength = in.read(inBytes);
            if (inLength == blockSize)
            {
                int outLength = cipher.update(inBytes, 0, blockSize, outBytes);// 加密后的outBytes长度为16,后面8位是0
                out.write(outBytes, 0, outLength);// 写入8字节
            }
            else
            {
                more = false;
            }
        }
        if (inLength > 0)
        {
            outBytes = cipher.doFinal(inBytes, 0, inLength);
        }
        else
        {
            outBytes = cipher.doFinal();// 这个地方又得到8个字节
        }
        out.write(outBytes);// 再写人8字节
    }
    
    public static void encryptMode(InputStream bis, OutputStream bos, byte[] keyBytes)
        throws Exception
    {
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);
        
        // 加密
        Cipher c1 = Cipher.getInstance(transformation);
        
        c1.init(Cipher.ENCRYPT_MODE, deskey);
        crypt(bis, bos, c1);
        
    }
    
    public static byte[] getSourceBytes(byte[] bytes)
    {
        if (null == bytes || bytes.length < 1)
        {
            return bytes;
        }
        int length = bytes.length;
        int appendLength = 8 - length % 8;
        if (length % 8 != 0)
        {
            byte[] newBytes = new byte[length + appendLength];
            for (int i = 0; i < newBytes.length; i++)
            {
                if (i < length)
                {
                    newBytes[i] = bytes[i];
                }
                else
                {
                    newBytes[i] = 0x00;
                }
            }
            return newBytes;
        }
        else
        {
            return bytes;
        }
    }
    
    public static byte[] getDecodeBytes(String string)
    {
        return HexStringUtil.hexString2Bytes(string);
    }
    
    public static byte[] removeAppendedByte(byte[] decodedBytes)
    {
        if (null == decodedBytes || decodedBytes.length < 1)
        {
            return decodedBytes;
        }
        for (int i = 0; i < decodedBytes.length; i++)
        {
            if (decodedBytes[i] == 0x00)
            {
                return Arrays.copyOf(decodedBytes, i);
            }
        }
        return decodedBytes;
    }
    
    /**
     * 解密方法,密钥必须为8位;
     * 
     * @param key
     * @param content
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String decrypt(String key, String content)
    {
        if (StringUtils.isEmpty(key))
        {
            log.error("decrypt key is " + key);
        }
        return new String(
            DESEncrypt.removeAppendedByte(DESEncrypt.decryptMode(key.getBytes(), DESEncrypt.getDecodeBytes(content))));
    }
    
    /**
     * 加密方法,密钥必须为8位;
     * 
     * @param key
     * @param content
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String encrypt(String key, String content)
    {
        if (StringUtils.isEmpty(key))
        {
            log.error("encrypt key is " + key);
        }
        return HexStringUtil.bytes2HexString(DESEncrypt.encryptMode(DESEncrypt.getSourceBytes(key.getBytes()),
            DESEncrypt.getSourceBytes(content.getBytes())));
    }
    
    public static void main(String[] args)
    {
        String mi = encrypt("12345678", "baiqirui");
        System.out.println(mi);
        System.out.println(decrypt("12345678", mi));
    }
}