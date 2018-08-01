package com.szzt.smart.framework.sample.job;


import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.szzt.smart.framework.elasticjob.SmartElasticJob;

@SmartElasticJob(cron = "0/5 * * * * ? ")
public class TestEkasticJob implements SimpleJob
{
    @Override
    public void execute(ShardingContext shardingContext)
    {
//        System.out.println("TestEkasticJob !!!");
    }
}
