package com.wym.starter.datasource.config;

import java.util.List;

/**
 * 
 * @author tangay
 *	机构数据源配置信息
 */
public class DataSourceConfig {
	private String userName;
	//数据源连接用户密码
	private String password;
	
	private Integer maxIdle;
	private Long maxWait;
	private Integer minIdle;
	private Integer initialSize;
	private String validationQuery;
	
	private Boolean testOnBorrow;
	private Boolean testWhileIdle;
	private Long timeBetweenEvictionRunsMills;
	
	private String urlBefore;
	private String urlAfter;
	
	private List<OrgCodeDBName> listOrgCodeDBName;

	private Integer maxActive;
	
	public Integer getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}

	public List<OrgCodeDBName> getListOrgCodeDBName() {
		return listOrgCodeDBName;
	}

	public void setListOrgCodeDBName(List<OrgCodeDBName> listOrgCodeDBName) {
		this.listOrgCodeDBName = listOrgCodeDBName;
	}
 

	public String getUrlBefore() {
		return urlBefore;
	}
	public void setUrlBefore(String urlBefore) {
		this.urlBefore = urlBefore;
	}
	public String getUrlAfter() {
		return urlAfter;
	}
	public void setUrlAfter(String urlAfter) {
		this.urlAfter = urlAfter;
	}
	public DataSourceConfig() {
		
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Integer getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
	}
	 
	public Long getMaxWait() {
		return maxWait;
	}
	public void setMaxWait(Long maxWait) {
		this.maxWait = maxWait;
	}
	public Integer getMinIdle() {
		return minIdle;
	}
	public void setMinIdle(Integer minIdle) {
		this.minIdle = minIdle;
	}
	public Integer getInitialSize() {
		return initialSize;
	}
	public void setInitialSize(Integer initialSize) {
		this.initialSize = initialSize;
	}
	public String getValidationQuery() {
		return validationQuery;
	}
	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}
	public Boolean getTestOnBorrow() {
		return testOnBorrow;
	}
	public void setTestOnBorrow(Boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}
	public Boolean getTestWhileIdle() {
		return testWhileIdle;
	}
	public void setTestWhileIdle(Boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}
	public Long getTimeBetweenEvictionRunsMills() {
		return timeBetweenEvictionRunsMills;
	}
	public void setTimeBetweenEvictionRunsMills(Long timeBetweenEvictionRunsMills) {
		this.timeBetweenEvictionRunsMills = timeBetweenEvictionRunsMills;
	}
	
	
}
