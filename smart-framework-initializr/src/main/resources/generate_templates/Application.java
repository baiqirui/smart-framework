package {{packageName}};



import org.springframework.boot.SpringApplication;
{{#useCloudModel}}
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
{{/useCloudModel}}
{{#useFeignModel}}
import org.springframework.cloud.netflix.feign.EnableFeignClients;
{{/useFeignModel}}
{{#useMybatisModel}}
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
{{/useMybatisModel}}
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;





{{#useCloudModel}}
@EnableDiscoveryClient
@EnableCircuitBreaker
{{/useCloudModel}}
{{#useFeignModel}}
@EnableFeignClients
{{/useFeignModel}}
{{#useMybatisModel}}
@EnableTransactionManagement
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class})
{{/useMybatisModel}}
{{^useMybatisModel}}
@SpringBootApplication
{{/useMybatisModel}}
@ComponentScan({"com.szzt.smart", "{{packageName}}"})
public class {{bootstrapApplicationName}}
{
	public static void main(String[] args)
	{
		SpringApplication.run({{bootstrapApplicationName}}.class, args);
	}
}
