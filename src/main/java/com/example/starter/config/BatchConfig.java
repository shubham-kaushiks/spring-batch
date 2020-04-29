package com.example.starter.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.starter.tasks.MyTaskOne;
import com.example.starter.tasks.MyTaskTwo;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
     
    @Autowired
    private JobBuilderFactory jobs;
 
    @Autowired
    private StepBuilderFactory steps;
     
    @Bean
    public Step stepOne(){
        return steps.get("stepOne")
                .tasklet(new MyTaskOne())
                .build();
    }
     
    @Bean
    public Step stepTwo(){
        return steps.get("stepTwo")
                .tasklet(new MyTaskTwo())
                .build();
    }   
     
    @Bean
    public Job demoJob(){
        return jobs.get("demoJob")
                .incrementer(new RunIdIncrementer())
                .start(stepOne())
                .next(stepTwo())
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
}
