package com.szzt.smart.framework.web.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;


/**
 * Double类型帮助类，主要提供double的计算方法。
 * @author luozhen
 * @data 2016年6月24日
 */
public class DoubleUtil
{
	/**
	 * 默认精度，2位小数
	 */
	public static  int DefaultScale = 2;
	
	/**
	 * 默认比率精度，前台需要显示如“91.21%”的百分数，需要将精度设置为4位。
	 */
	public static int DefaultRateScale = 4;
	
	/**
	 * 默认取舍规则
	 */
	public static int DefaultRoundingMode = BigDecimal.ROUND_HALF_UP;
	
	public static DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
	
	/**
	 * 加法
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static BigDecimal add(Double d1, Double d2)
	{
		BigDecimal rs = new BigDecimal(Double.toString(d1)).add(new BigDecimal(Double.toString(d2)));
		return rs;
	}
	
    /**
     * 加法 -进行四舍五入，取2位小数
     * @param d1
     * @param d2
     * @return
     */
    public static Double addToDouble(Double d1, Double d2)
    {
        return scale(add(d1, d2).doubleValue());
    }
    
    /**
     * 加法 -进行四舍五入，取2位小数
     * @param d1
     * @param d2
     * @return
     */
    public static Double addToDouble(Double d1, Double d2, int scale)
    {
        return scale(add(d1, d2).doubleValue(), scale);
    }

	/**
	 * 减法
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static BigDecimal subtract(Double d1, Double d2)
	{
		BigDecimal rs = new BigDecimal(Double.toString(d1)).subtract(new BigDecimal(Double.toString(d2)));
		return rs;
	}
	
    /**
     * 减法 -进行四舍五入，取4位小数
     * @param d1
     * @param d2
     * @return
     */
    public static Double subtractToDouble(Double d1, Double d2)
    {
        return scale(subtract(d1, d2).doubleValue());
    }	
	/**
	 * 乘法
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static BigDecimal multiply(Double d1, Double d2)
	{
	    d1 = (null == d1) ? 0.0D : d1;
	    d2 = (null == d2) ? 0.0D : d2;
		return new BigDecimal(Double.toString(d1)).multiply(new BigDecimal(Double.toString(d2)));
	}
	
	
	/**
     * 乘法
     * @param d1
     * @param d2
     * @return
     */
    public static Double multiplyToDouble(Double d1, Double d2, int scale)
    {
        return scale(multiply(d1, d2).doubleValue(), scale);
    }
    
    /**
     * 乘法
     * @param d1
     * @param d2
     * @return
     */
    public static Double multiplyToDouble(Double d1, Double d2)
    {
          return scale(multiply(d1, d2).doubleValue());
    }
    
    
    /**
     * 对double进行四舍五入，取2位小数
     */
    public static Double scale(Double d, int scale)
    {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        BigDecimal result = bd.setScale(scale, DoubleUtil.DefaultRoundingMode); 
        return result.doubleValue();
    }
    
    
    /**
     * 对double进行四舍五入，取2位小数
     */
    public static Double scale(Double d)
    {
        return scale(d, DoubleUtil.DefaultScale);
    }
	
	
	/**
	 * 乘法
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static BigDecimal multiply(Double d1, BigDecimal d2)
	{
		return new BigDecimal(Double.toString(d1)).multiply(d2);
	}
	
	/**
	 * 除法
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static BigDecimal divide(Double d1, Double d2)
	{
		return DoubleUtil.divide(d1, d2, DefaultScale, DefaultRoundingMode);
	}
	
	public static BigDecimal divide(Double d1, Double d2, int scale)
	{
		return DoubleUtil.divide(d1, d2, scale, DefaultRoundingMode);
	}
	
	public static BigDecimal divide(Double d1, Double d2, int scale, int roundingMode)
	{
		return new BigDecimal(Double.toString(d1)).divide(new BigDecimal(Double.toString(d2)), scale, roundingMode);
	}
	
	/**
	 * d1是否小于等于零
	 * @param d1
	 * @return
	 */
	public static boolean lessOrEqualZero(Double d1)
	{
		BigDecimal b = new BigDecimal(Double.toString(d1));
		int rs = b.compareTo(new BigDecimal(0.0));
		if (rs > 0)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * d1是否大于0
	 * @param d1
	 * @return
	 */
	public static boolean greaterZero(Double d1)
	{
		int rs = compare(d1, 0.0D);
		if (rs > 0)
		{
			return true;
		}
		return false;
	}
	
	public static int compare(Double d1, Double d2)
	{
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		int rs = b1.compareTo(b2);
		return rs;
	}

	/**
	 * d1是否小于0
	 * @param d1
	 * @return
	 */
	public static boolean lessZero(Double d1)
	{
		BigDecimal b = new BigDecimal(Double.toString(d1));
		int rs = b.compareTo(new BigDecimal(0.0));
		if (rs >= 0)
		{
			return false;
		}
		return true;
	}
	
	public static BigDecimal divide(Double d1, Integer i)
	{
		return new BigDecimal(Double.toString(d1)).divide(new BigDecimal(i), DefaultScale, DefaultRoundingMode);
	}
	
	public static String scaleForString(Double d)
	{
		return decimalFormat.format(d);
	}
	
	/**
	 * 返回百分数，保留2位小数
	 * @param invoiceRate
	 * @return
	 */
	public static String percentValue(Double invoiceRate)
	{
		if (null == invoiceRate)
		{
			invoiceRate = 0.0;
		}
		invoiceRate = DoubleUtil.multiplyToDouble(invoiceRate, 100D).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00"); 
		String num=df.format(invoiceRate);
		return  num + "%";
	}
	
	/**
	 * 返回百分数，保留0位小数
	 * @param invoiceRate
	 * @return
	 */
	public static String percentValueNoPoint(Double invoiceRate)
	{
		if (null == invoiceRate)
		{
			invoiceRate = 0.0;
		}
		invoiceRate = DoubleUtil.multiplyToDouble(invoiceRate, 100D).doubleValue();
		DecimalFormat df = new DecimalFormat("0"); 
		String num=df.format(invoiceRate);
		return  num + "%";
	}
}
