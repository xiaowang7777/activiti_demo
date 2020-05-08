package com.wjf.github.activitidemo.config;

import org.activiti.engine.ProcessEngine;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Component
public class ActivitiConfig {

	@Autowired
	private ActivitiConfigProperties activitiConfigProperties;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	@Bean
	public ProcessEngine processEngine(){
		SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
		configuration.setDataSource(dataSource);
		configuration.setDbHistoryUsed(activitiConfigProperties.getDbHistoryUsed());
		configuration.setDatabaseSchemaUpdate(activitiConfigProperties.getDatabaseSchemaUpdate());
		configuration.setHistoryLevel(activitiConfigProperties.getHistoryLevel());
		configuration.setAsyncExecutorActivate(activitiConfigProperties.getAsyncExecutorActivate());
		configuration.setTransactionManager(platformTransactionManager);
		return configuration.buildProcessEngine();
	}
}
