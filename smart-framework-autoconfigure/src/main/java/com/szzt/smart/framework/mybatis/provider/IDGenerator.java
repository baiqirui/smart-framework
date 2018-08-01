package com.szzt.smart.framework.mybatis.provider;

import java.util.HashSet;
import java.util.Set;

public class IDGenerator
{
    
    // ==============================Fields===========================================
    /** 开始时间截 (2015-01-01) */
    private final long twepoch = 1483200000000L;
    
    /** 机器id所占的位数 */
    private final long workerIdBits = 4L;
    
    /** 数据标识id所占的位数 */
    private final long datacenterIdBits = 4L;
    
    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxNodeId = -1L ^ (-1L << workerIdBits);
    
    /** 支持的最大数据标识id，结果是31 */
    private final long maxClusterCenterId = -1L ^ (-1L << datacenterIdBits);
    
    /** 序列在id中占的位数 */
    private final long sequenceBits = 8L;
    
    /** 机器ID向左移12位 */
    private final long nodeIdShift = sequenceBits;
    
    /** 数据标识id向左移17位(12+5) */
    private final long clusterCenterIdShift = sequenceBits + workerIdBits;
    
    /** 时间截向左移22位(5+5+12) */
//    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    
    /** 时间截向左移14位(4+4+8) */
    private final long timestampLeftShift = 13L;
    
    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    
    /** 工作节点ID(0~31) */
    private long nodeId;
    
    /**集群中心ID(0~31) */
    private long clusterCenterId;
    
    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;
    
    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;
    
    // ==============================Constructors=====================================
    /**
     * 构造函数
     * 
     * @param workerId 工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public IDGenerator(long nodeId, long clusterCenterId)
    {
        if (nodeId > maxNodeId || nodeId < 0)
        {
            throw new IllegalArgumentException(
                String.format("nodeId Id can't be greater than %d or less than 0", maxNodeId));
        }
        if (clusterCenterId > maxClusterCenterId || clusterCenterId < 0)
        {
            throw new IllegalArgumentException(
                String.format("clusterCenter Id can't be greater than %d or less than 0", maxClusterCenterId));
        }
        this.nodeId = nodeId;
        this.clusterCenterId = clusterCenterId;
    }
    
    // ==============================Methods==========================================
    /**
     * 获得下一个ID (该方法是线程安全的)
     * 
     * @return SnowflakeId
     */
    public synchronized long nextId()
    {
        long timestamp = timeGen();
        
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp)
        {
            throw new RuntimeException(String.format(
                "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp)
        {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0)
            {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else
        {
            sequence = 0L;
        }
        
        // 上次生成ID的时间截
        lastTimestamp = timestamp;
        
        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) //
            | (clusterCenterId << clusterCenterIdShift) //
            | (nodeId << nodeIdShift) //
            | sequence;
    }
    
    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * 
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp)
    {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp)
        {
            timestamp = timeGen();
        }
        return timestamp;
    }
    
    /**
     * 返回以毫秒为单位的当前时间
     * 
     * @return 当前时间(毫秒)
     */
    protected long timeGen()
    {
        return System.currentTimeMillis();
    }
    
    // ==============================Test=============================================
    /** 测试 
     * @throws Exception */
    public static void main(String[] args) throws Exception
    {
        IDGenerator idWorker = new IDGenerator(0, 0);
        long startTime = System.currentTimeMillis();
        Set<Long> ids = new HashSet<Long>();
        for (int i = 0; i < 30000; i++)
        {
            long id = idWorker.nextId();
            System.out.println(id + " ------ " + String.valueOf(id).length() + " ------ " + Long.toBinaryString(id));
//            if (ids.contains(id))
//            {
//                System.out.println("在执行第" + i + "次的时候发现重复ID：" + id);
//            }
//            else
//            {
//                System.out.println(id + " ------ " + String.valueOf(id).length() + " ------ " + Long.toBinaryString(id));
//            }
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - startTime) + "ms");
////        DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
//        String str = "2017-01-01";
//        String str1 = "2015-1-1";
//        System.out.println(f.parse(str).getTime());
//        System.out.println(f.parse(str1).getTime());
//        System.out.println(Integer.toBinaryString(-5));
        System.out.println("39bit的时间戳可以支持该算法使用年限：" + (1L << 39) / (3600L * 24 * 365 * 1000.0));
        System.out.println("8bit的工作机器id数量：" + ((1L << 8) - 1));
        System.out.println("8bit序列id数量：" + ((1L << 8) - 1));
        System.out.println(-1L ^ (-1L << 8));
    }
}