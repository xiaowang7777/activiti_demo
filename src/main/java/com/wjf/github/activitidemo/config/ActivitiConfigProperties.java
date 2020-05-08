package com.wjf.github.activitidemo.config;

import org.activiti.engine.impl.history.HistoryLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.activiti")
public class ActivitiConfigProperties {

	private String databaseSchemaUpdate;

	private HistoryLevel historyLevel;

	private Boolean dbHistoryUsed;

	private Boolean asyncExecutorActivate;

	public String getDatabaseSchemaUpdate() {
		return databaseSchemaUpdate;
	}

	public void setDatabaseSchemaUpdate(String databaseSchemaUpdate) {
		this.databaseSchemaUpdate = databaseSchemaUpdate;
	}

	public HistoryLevel getHistoryLevel() {
		return historyLevel;
	}

	public void setHistoryLevel(HistoryLevel historyLevel) {
		this.historyLevel = historyLevel;
	}

	public Boolean getDbHistoryUsed() {
		return dbHistoryUsed;
	}

	public void setDbHistoryUsed(Boolean dbHistoryUsed) {
		this.dbHistoryUsed = dbHistoryUsed;
	}

	public Boolean getAsyncExecutorActivate() {
		return asyncExecutorActivate;
	}

	public void setAsyncExecutorActivate(Boolean asyncExecutorActivate) {
		this.asyncExecutorActivate = asyncExecutorActivate;
	}
}
