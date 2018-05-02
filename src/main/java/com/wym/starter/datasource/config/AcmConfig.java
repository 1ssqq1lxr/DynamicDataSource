package com.wym.starter.datasource.config;

import java.util.List;

public class AcmConfig {

	private List<DataSourceConfig> dataSourceConfig;

	public List<DataSourceConfig> getDataSourceConfig() {
		return dataSourceConfig;
	}
	public void setDataSourceConfig(List<DataSourceConfig> dataSourceConfig) {
		this.dataSourceConfig = dataSourceConfig;
	}
}
