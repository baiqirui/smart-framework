package com.szzt.smart.framework.web.util;

import org.apache.commons.lang.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 为语言拼音提供常数和静态方法。
 * 
 * @author yidi
 */
public class PinyinUtil
{
    /**
     * 获取汉字串拼音首字母，英文字符不变。
     * 
     * @param chinese
     *            汉字串。
     * @return 汉语拼音首字母。
     */
    public static String toFirstSpell(String chinese)
    {
        StringBuffer buffer = new StringBuffer();
        char[] array = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] > 128)
            {
                try
                {
                    String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(
                            array[i], defaultFormat);
                    if (null != pinyins)
                    {
                        buffer.append(pinyins[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e)
                {
                    e.printStackTrace();
                }
            } else
            {
                buffer.append(array[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * 获取汉字串拼音，英文字符不变。
     * 
     * @param chinese
     *            汉字串。
     * @return 汉语拼音。
     */
    public static String toSpell(String chinese)
    {
        StringBuffer buffer = new StringBuffer();
        char[] array = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] > 128)
            {
                try
                {
                    buffer.append(PinyinHelper.toHanyuPinyinStringArray(
                            array[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e)
                {
                    e.printStackTrace();
                }
            } else
            {
                buffer.append(array[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * 获取汉字串拼音，英文字符不变。(获取大写字母)
     * 
     * @param chinese
     *            汉字串。
     * @return 汉语拼音。
     */
    public static String toUpperSpell(String chinese)
    {
        StringBuffer buffer = new StringBuffer();
        char[] array = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] > 128)
            {
                try
                {
                    buffer.append(PinyinHelper.toHanyuPinyinStringArray(
                            array[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e)
                {
                    e.printStackTrace();
                }
            } else
            {
                buffer.append(array[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * 获取汉字串拼音首写大写字母
     *
     * @param chinese
     * @return[参数、异常说明]
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String toFirstUpperSpell(String chinese)
    {
        return StringUtils.upperCase(toFirstSpell(chinese));
    }
}