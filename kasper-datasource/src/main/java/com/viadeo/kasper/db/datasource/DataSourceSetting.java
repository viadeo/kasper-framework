/*
 * Copyright 2010 Viadeo.com
 */
package com.viadeo.kasper.db.datasource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class DataSourceSetting {
	// Mandatory
	private String driverClassName;
	private String password;
	private String validationQuery;
	private String url;
	private String userName;

	// Optional, with default values
	private Integer maxActive=75;
	private Integer maxIdle=50;
	private Integer maxWait=10000;
	private Long minEvictableIdleTimeMillis=600000L;
	private Boolean testWhileIdle=true;
	private Boolean testOnBorrow=true;
	private Boolean testOnReturn=false;
	private Long timeBetweenEvictionRunsMillis=300000L;
	// Optional without default value
	private String option;

	/**
	 * Sets the driver class name.
	 * 
	 * @param driverClassName the new driver class name
	 */
	public void setDriverClassName(final String driverClassName) {
		this.driverClassName = driverClassName;
	}

	/**
	 * Sets the max active.
	 * 
	 * @param maxActive the new max active
	 */
	public void setMaxActive(final int maxActive) {
		this.maxActive = maxActive;
	}

	/**
	 * Sets the max idle.
	 * 
	 * @param maxIdle the new max idle
	 */
	public void setMaxIdle(final int maxIdle) {
		this.maxIdle = maxIdle;
	}

	/**
	 * Sets the max wait.
	 * 
	 * @param maxWait the new max wait
	 */
	public void setMaxWait(final Integer maxWait) {
		this.maxWait = maxWait;
	}

	public final void setTimeBetweenEvictionRunsMillis(final Long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public final void setMinEvictableIdleTimeMillis(final Long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	/**
	 * Sets the option.
	 * 
	 * @param option the new option
	 */
	public void setOption(final String option) {
		this.option = option;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password the new password
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	public final void setTestWhileIdle(final Boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	/**
	 * Sets the url.
	 * 
	 * @param url the new url
	 */
	public void setUrl(final String url) {
		this.url = getFullUrl((replaceELVariables(url)));
	}

	/**
	 * Sets the user name.
	 * 
	 * @param userName the new user name
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * @param testOnBorrow the testOnBorrow to set
	 */
	public void setTestOnBorrow(Boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	/**
	 * @param testOnReturn the testOnReturn to set
	 */
	public void setTestOnReturn(Boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	/**
	 * @param validationQuery the validationQuery to set
	 */
	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	private String getFullUrl(String jdbcUrl) {
		if (jdbcUrl.indexOf('?') > 0 || option == null || option.length() == 0) {
			return jdbcUrl;
		}
		// incompatible
		final StringBuilder sb = new StringBuilder(jdbcUrl).append('?');
		if (option.indexOf("utf8") >= 0 || option.indexOf("utf-8") >= 0) {
			sb.append("useUnicode=true&characterEncoding=UTF-8&");
		}
		if (option.indexOf("auto") >= 0) {
			sb.append("autoReconnect=true&");
		}
		if (option.indexOf("zlib") >= 0 || option.indexOf("zip") >= 0) {
			sb.append("useCompression=true&");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private static final Pattern EL_VAR_PATTERN = Pattern.compile("\\$\\{([a-zA-Z.\\-_0-9]+)\\}");

	public final static String replaceELVariables(final String str) {
		final StringBuffer sbuff = new StringBuffer();
		final Matcher matcher = EL_VAR_PATTERN.matcher(str);
		while (matcher.find()) {
			final String prop = System.getProperty(matcher.group(1));
			if (prop != null) {
				matcher.appendReplacement(sbuff, Matcher.quoteReplacement(prop));
			}
		}
		matcher.appendTail(sbuff);
		return sbuff.toString();
	}

	
	public Integer getMaxActive() {
		return maxActive;
	}


	public Integer getMaxIdle() {
		return maxIdle;
	}


	public String getDriverClassName() {
		return driverClassName;
	}

	public String getPassword() {
		return password;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public String getUrl() {
		return url;
	}

	public String getUserName() {
		return userName;
	}

	public Integer getMaxWait() {
		return maxWait;
	}

	public Long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public Boolean getTestWhileIdle() {
		return testWhileIdle;
	}

	public Boolean getTestOnBorrow() {
		return testOnBorrow;
	}

	public Boolean getTestOnReturn() {
		return testOnReturn;
	}

	public Long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public String getOption() {
		return option;
	}
	
	

}
