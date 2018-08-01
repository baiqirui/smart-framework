package com.szzt.smart.framework.initializr.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * zip文件相关工具类;
 * 
 * @author baiqirui
 * @version [版本号, Jun 6, 2014]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public final class ZipUtils
{
    private static final Logger log = Logger.getLogger(ZipUtils.class);
    
    private ZipUtils()
    {
        
    }
    
    /**
     * 解压文件
     * 
     * @param unZipFile [压缩的源文件]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void unzip(File unZipFile)
    {
        unzip(unZipFile, unZipFile.getParent(), "GBK");
    }
    
    /**
     * 解压文件
     * 
     * @param unZipFile [压缩的源文件]
     * @param outDirectory [解压的目标路径]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void unzip(File unZipFile, String outDirectory)
    {
        unzip(unZipFile, outDirectory, "GBK");
    }
    
    /**
     * 解压文件
     * 
     * @param unZipFile [压缩的源文件]
     * @param outDirectory [解压的目标路径]
     * @param encoding [编码格式]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public static void unzip(File unZipFile, String outDirectory, String encoding)
    {
        BufferedInputStream inBuffered = null;
        BufferedOutputStream outBuffered = null;
        try
        {
            ZipFile zipFile = new ZipFile(unZipFile, encoding);
            Enumeration<? extends ZipEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory())
                {
                    File directory = new File(outDirectory + File.separator + entry.getName());
                    if (!directory.exists())
                    {
                        directory.mkdirs();
                    }
                }
                else
                {
                    File entryFile = new File(outDirectory + File.separator + entry.getName());
                    if (!entryFile.getParentFile().exists())
                    {
                        entryFile.getParentFile().mkdirs();
                    }
                    inBuffered = new BufferedInputStream(zipFile.getInputStream(entry));
                    outBuffered = new BufferedOutputStream((new FileOutputStream(entryFile)));
                    byte[] buffer = new byte[1024 * 8];
                    int b = -1;
                    while ((b = inBuffered.read(buffer)) != -1)
                    {
                        outBuffered.write(buffer, 0, b);
                    }
                    outBuffered.flush();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                inBuffered.close();
                outBuffered.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 压缩文件;
     * 
     * @param sourceFile [源文件]
     * @param targetFiles [目标文件]
     * @param isRequireRoot [是否需要将更目录也压缩]
     * @param encoding [字符编码]
     * 
     * @return void [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static void compress(File sourceFile, File targetFiles, boolean isRequireRoot, String encoding)
    {
        // 如果要压缩的源文件不存在则,记录错误日志,然后直接终止压缩;
        if (null == sourceFile || !sourceFile.exists())
        {
            log.error(sourceFile + "不存在！");
            return;
        }
        // 如果目标父目录不存在则创建;
        if (!targetFiles.getParentFile().exists())
        {
            targetFiles.getParentFile().mkdirs();
        }
        ZipOutputStream outZipFile = null;
        FileOutputStream fileOutputStream = null;
        CheckedOutputStream cos = null;
        try
        {
            fileOutputStream = new FileOutputStream(targetFiles);
            // 使用CRC32先进行校验;
            cos = new CheckedOutputStream(fileOutputStream, new CRC32());
            outZipFile = new ZipOutputStream(cos);
            outZipFile.setEncoding(encoding);
            for (File file : sourceFile.listFiles())
            {
                String basePath = isRequireRoot ? sourceFile.getName() + File.separator + file.getName()
                    : file.getName();
                if (file.isDirectory())
                {
                    compressDirectory(file, outZipFile, basePath + "/");
                }
                else
                {
                    compressFile(file, outZipFile, basePath);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                outZipFile.close();
                cos.close();
                fileOutputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 压缩文件;
     * 
     * @param sourceFile [源文件]
     * @param targetFiles [目标文件]
     * @param isRequireRoot [是否需要将根目录也压缩]
     * 
     * @return void [返回类型说明]
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static void compress(File sourceFile, File targetFiles, boolean isRequireRoot)
    {
        compress(sourceFile, targetFiles, isRequireRoot, "UTF-8");
    }
    
    /**
     * 压缩文件;
     * 
     * @param sourceFile [源文件]
     * @param targetFiles [目标文件]
     * @return void [返回类型说明]
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static void compress(File sourceFile, File targetFiles)
        throws Exception
    {
        compress(sourceFile, targetFiles, false);
    }
    
    /**
     * 压缩文件;
     * 
     * @param file
     * @param outZipFile
     * @param basePath [压缩路径]
     * 
     * @return void [返回类型说明]
     * @throws Exception
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private static void compressFile(File file, ZipOutputStream outZipFile, String basePath)
        throws Exception
    {
        ZipEntry zipEntryFile = new ZipEntry(basePath);
        outZipFile.putNextEntry(zipEntryFile);
        BufferedInputStream inBuffered = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[1024 * 8];
        int b;
        while ((b = inBuffered.read(buffer)) != -1)
        {
            outZipFile.write(buffer, 0, b);
        }
        outZipFile.closeEntry();
        inBuffered.close();
    }
    
    /**
     * 递归压缩目录及其目录下的子文件;
     * 
     * @param directory
     * @param outZipFile
     * @param basePath [压缩路径]
     * @throws Exception [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private static void compressDirectory(File directory, ZipOutputStream outZipFile, String basePath)
        throws Exception
    {
        // 首先压缩目录本身;
        ZipEntry zipEntryDir = new ZipEntry(basePath);
        outZipFile.putNextEntry(zipEntryDir);
        outZipFile.closeEntry();
        // 然后递归目录下的所有文集及其子目录,继续压缩;
        for (File file : directory.listFiles())
        {
            if (file.isDirectory())
            {
                compressDirectory(file, outZipFile, basePath + file.getName() + "/");
            }
            else
            {
                compressFile(file, outZipFile, basePath + file.getName());
            }
        }
    }
    
    public static void main(String[] args)
    {
        File unZipFile = new File("D://test//zip//test.epub");
        unzip(unZipFile);
        // File sourceFile = new File("D://test//zip//myutils");
        // compress(sourceFile, new File("D://test//zip//test.epub"), true);
    }
    
}
