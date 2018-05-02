package com.wym.starter.datasource.config;

/**
 * 
 * @author tangay
 *	机构数据源配置信息
 */
public class OrgDSConfig {
	//机构码
	private String orgCode;
	//数据源url
	private String url;
	//数据源连接用户名
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
	
	private Integer maxActive;
	public OrgDSConfig() {
		
	}
	public OrgDSConfig(String orgCode, String url, String userName, String password) {
		this.orgCode = orgCode;
		this.url = url;
		this.userName = userName;
		this.password = password;
	}
	
	public Integer getMaxActive() {
		return maxActive;
	}
	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgDSConfig that = (OrgDSConfig) o;

        if (orgCode != null ? !orgCode.equals(that.orgCode) : that.orgCode != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (maxIdle != null ? !maxIdle.equals(that.maxIdle) : that.maxIdle != null) return false;
        if (maxWait != null ? !maxWait.equals(that.maxWait) : that.maxWait != null) return false;
        if (minIdle != null ? !minIdle.equals(that.minIdle) : that.minIdle != null) return false;
        if (initialSize != null ? !initialSize.equals(that.initialSize) : that.initialSize != null) return false;
        if (validationQuery != null ? !validationQuery.equals(that.validationQuery) : that.validationQuery != null)
            return false;
        if (testOnBorrow != null ? !testOnBorrow.equals(that.testOnBorrow) : that.testOnBorrow != null) return false;
        if (testWhileIdle != null ? !testWhileIdle.equals(that.testWhileIdle) : that.testWhileIdle != null)
            return false;
        if (timeBetweenEvictionRunsMills != null ? !timeBetweenEvictionRunsMills.equals(that.timeBetweenEvictionRunsMills) : that.timeBetweenEvictionRunsMills != null)
            return false;
        return maxActive != null ? maxActive.equals(that.maxActive) : that.maxActive == null;
    }

    @Override
    public int hashCode() {
        int result = orgCode != null ? orgCode.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (maxIdle != null ? maxIdle.hashCode() : 0);
        result = 31 * result + (maxWait != null ? maxWait.hashCode() : 0);
        result = 31 * result + (minIdle != null ? minIdle.hashCode() : 0);
        result = 31 * result + (initialSize != null ? initialSize.hashCode() : 0);
        result = 31 * result + (validationQuery != null ? validationQuery.hashCode() : 0);
        result = 31 * result + (testOnBorrow != null ? testOnBorrow.hashCode() : 0);
        result = 31 * result + (testWhileIdle != null ? testWhileIdle.hashCode() : 0);
        result = 31 * result + (timeBetweenEvictionRunsMills != null ? timeBetweenEvictionRunsMills.hashCode() : 0);
        result = 31 * result + (maxActive != null ? maxActive.hashCode() : 0);
        return result;
    }
}
