package com.wym.starter.datasource.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * mybatis配置
 *
 * @author lxr
 * @create 2018-04-28 16:11
 **/
@ConfigurationProperties(prefix ="wym.mybatis")
public class MybatisProperties {

    private String mapperPath;

    private String typeAliasesPackage;


    public String getMapperPath() {
        return mapperPath;
    }

    public void setMapperPath(String mapperPath) {
        this.mapperPath = mapperPath;
    }

    public String getTypeAliasesPackage() {
        return typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }
}
