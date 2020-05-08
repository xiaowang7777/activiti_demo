package com.wjf.github.activitidemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
public class FileConfigProperties {

	private String templatePath;

	private String handlerPath;

	private String staticAccessPath;

	private String excludeStaticAccessPath;

	private String localPath;

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public String getHandlerPath() {
		return handlerPath;
	}

	public void setHandlerPath(String handlerPath) {
		this.handlerPath = handlerPath;
	}

	public String getStaticAccessPath() {
		return staticAccessPath;
	}

	public void setStaticAccessPath(String staticAccessPath) {
		this.staticAccessPath = staticAccessPath;
	}

	public String getExcludeStaticAccessPath() {
		return excludeStaticAccessPath;
	}

	public void setExcludeStaticAccessPath(String excludeStaticAccessPath) {
		this.excludeStaticAccessPath = excludeStaticAccessPath;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
}
