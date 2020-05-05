package com.example.starter.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.starter.tasks.MyTaskOne;
import com.example.starter.tasks.MyTaskTwo;


@Configuration
@EnableBatchProcessing
public class BatchConfig {
     
    @Autowired
    private JobBuilderFactory jobs;
 
    @Autowired
    private StepBuilderFactory steps;
    
    @Autowired
    JobLauncher jobLauncher;
    
//    @Autowired
//    private Job job1;
//
//    @Autowired
//    private Job job2;
     
    @Autowired
    private MyTaskOne m;
    
    @Autowired
    private MyTaskTwo n;
    
    @Bean
    public Step stepOne(){
        return steps.get("stepOne")
                .tasklet(m)
                .build();
    }
     
    @Bean
    public Step stepTwo(){
        return steps.get("stepTwo")
                .tasklet(n)
                .build();
    }   
     
    @Bean
    public Job job1(){
        return jobs.get("job1")
                .incrementer(new RunIdIncrementer())
                .start(stepOne())
                .next(stepTwo())
                .build();
    }
    
    @Bean
    public Job job2(){
        return jobs.get("job2")
                .incrementer(new RunIdIncrementer())
                .start(stepTwo())
                .build();
    }
    
    @Bean
    public DataSource getDataSource(){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/temp?characterEncoding=UTF-8");
        dataSource.setUsername("root");
        dataSource.setPassword("Shubham@123");
        return dataSource;
    }
    
    @Bean
    public BatchConfigurer batchConfigurer() {
    	return new DefaultBatchConfigurer() {
    		@Override
    		protected JobRepository createJobRepository() throws Exception {
    		    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    		    factory.setDataSource(getDataSource());
    		    factory.setTransactionManager(getTransactionManager());
    		    factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
    		    factory.setTablePrefix("BATCH_");
    		    factory.setMaxVarCharLength(1000);
    		    return factory.getObject();
    		}
    	};
    } 
    
    @Scheduled(cron = "0 */1 * * * ?")
    public void run1(){
    	JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
    	try {
            jobLauncher.run(job1(), params);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

    }
    
    @Scheduled(cron = "0 */2 * * * ?")
    public void run2() {
    	JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
    	try {
            jobLauncher.run(job2(), params);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    	
    }
}
