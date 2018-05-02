package com.wym.starter.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.wym.starter.datasource.config.AcmDBConfigMgr;
import com.wym.starter.datasource.config.OrgDSConfig;
import com.wym.starter.datasource.plugin.CloudPageInterceptor;
import com.wym.starter.datasource.spi.DynamicIdSelector;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import net.sf.log4jdbc.Log4jdbcProxyDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.locks.ReentrantLock;

public class DataSourceManager {
    private static Logger logger = LoggerFactory.getLogger(DataSourceManager.class);



    private DynamicIdSelector dynamicIdSelector;


    private String mapperPath;

    public void setMapperPath(String mapperPath) {
        this.mapperPath = mapperPath;
    }

    public void setDynamicIdSelector(DynamicIdSelector dynamicIdSelector) {
        this.dynamicIdSelector = dynamicIdSelector;
    }

    /**
     * 维护机构码和数据源
     * 系统启动之后初始化
     *
     */
    public static Map<String, SqlSessionFactory> ORG_SQLSESSION_MAP = new HashMap<>();

    final static ReentrantLock lock = new ReentrantLock();

    private final static String MAPPER_PATH = "classpath:/mapper/cloud/*.xml";

    /**
     * 根据机构码获取数据源连接
     */
    public  SqlSessionFactory getSqlSessionFactory() {
        String orgCode = dynamicIdSelector.getOrgCode();
        return Optional.ofNullable(ORG_SQLSESSION_MAP.get(orgCode))
                .orElseGet(() -> {
                    SqlSessionFactory sqlSessionFactory = null;
                    try {
                        lock.lock();
                        OrgDSConfig dsConfig = AcmDBConfigMgr.getDSConfig(orgCode);
                        if(dsConfig != null && ORG_SQLSESSION_MAP.get(orgCode) ==null) {
                            String url = dsConfig.getUrl();
                            String password = dsConfig.getPassword();
                            String username = dsConfig.getUserName();
                            if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password) && StringUtils.isNotBlank(url)) {
                                DataSource createDataSourceDynamic = createDataSourceDynamic(dsConfig);
                                checkDBStatus(createDataSourceDynamic, dsConfig);
                                SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
                                sqlSessionFactoryBean.setDataSource(createDataSourceDynamic);
                                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                                sqlSessionFactoryBean.setMapperLocations(resolver.getResources(MAPPER_PATH));
                                Interceptor[] plugins = new Interceptor[] { new CloudPageInterceptor() };
                                sqlSessionFactoryBean.setPlugins(plugins);
                                sqlSessionFactory =sqlSessionFactoryBean.getObject();
                                ORG_SQLSESSION_MAP.put(orgCode, sqlSessionFactory);

                            } else {
                                logger.error("DataSourceManager.getSqlSessionFactory 根据机构码获取数据源异常,配置中心数据源信息配置错误 机构码={}",orgCode);
                            }
                        } else {
                            logger.error("DataSourceManager.getSqlSessionFactory 根据机构码获取数据源异常,配置中心无此机构对应的数据源 机构码={}",orgCode);
                        }

                    } catch(Exception e) {
                        logger.error("DataSourceManager.getSqlSessionFactory 根据机构码获取数据源异常 机构码="+orgCode+" 异常信息="+e.getMessage());
                    }finally {
                        lock.unlock();
                    }
                    return sqlSessionFactory;
                });
    }

    private static void checkDBStatus(DataSource dataSource, OrgDSConfig dsConfig) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = dataSource.getConnection();
            prepareStatement = connection.prepareStatement("select 1");
            if(prepareStatement != null) {
                ResultSet rs = prepareStatement.executeQuery();
                if(rs != null) {
                    if(rs.next()) {
                        int value = rs.getInt(1);
                        if(value != 1) {
                            logger.error("the org db could not connection, code is {}",dsConfig.getOrgCode());
                            logger.error("dsConfig json is {}", JSON.toJSONString(dsConfig));
                        } else {
                            logger.info("the org db is ok, code is {}", dsConfig.getOrgCode());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            try {
                if(prepareStatement != null) {
                    prepareStatement.close();
                }
                if(connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    private void createDynamicDataSource(OrgDSConfig dsConfig) {
        Optional.ofNullable(dsConfig)
                .ifPresent(orgDSConfig -> {
                    try {
                        String url = orgDSConfig.getUrl();
                        String password = orgDSConfig.getPassword();
                        String username = orgDSConfig.getUserName();

                        if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password) && StringUtils.isNotBlank(url)) {
                            DataSource createDataSourceDynamic = createDataSourceDynamic(orgDSConfig);
                            checkDBStatus(createDataSourceDynamic, orgDSConfig);
                            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
                            sqlSessionFactoryBean.setDataSource(createDataSourceDynamic);

                            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

                            sqlSessionFactoryBean.setMapperLocations(resolver.getResources(MAPPER_PATH));

                            Interceptor[] plugins = new Interceptor[] { new CloudPageInterceptor() };
                            sqlSessionFactoryBean.setPlugins(plugins);

                            ORG_SQLSESSION_MAP.put(dsConfig.getOrgCode(), sqlSessionFactoryBean.getObject());
                        }
                    } catch(Exception e) {
                        logger.error("create dynamic datasource error dsconfig:{}", JSON.toJSONString(dsConfig));
                    }
                });
    }

    /**
     * 初始化已经配置好的数据源
     */
    public void initOrgDataSourceMap(ConcurrentHashMap<String, OrgDSConfig> orgDSConfigMap) {
        if(MapUtils.isNotEmpty(orgDSConfigMap)) {
            KeySetView<String, OrgDSConfig> keySet = orgDSConfigMap.keySet();
            //获取每家机构的数据源配置
            try{
                lock.lock();
                for(String key : keySet) {
                    this.createDynamicDataSource(orgDSConfigMap.get(key));
                }
            }catch(Exception e) {
                logger.error("DataSourceManager.initOrgDataSourceMap 初始化机构码数据源时发生异常,机构码配置={} 异常= ",keySet,e.getMessage());
            }finally {
                lock.unlock();
            }
        }
    }

    /**
     * 动态创建数据源
     *
     * @param url
     * @param password
     * @param username
     * @return
     */
    private DataSource createDataSourceDynamic(String url, String password,String username) {
        return DataSourceBuilder.create().url(url)
                .username(username).password(password)
                .driverClassName("com.mysql.jdbc.Driver").build();
    }

    private static DataSource createDataSourceDynamic(OrgDSConfig config) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(config.getUrl());
        druidDataSource.setUsername(config.getUserName());
        druidDataSource.setPassword(config.getPassword());
        druidDataSource.setTestOnBorrow(config.getTestOnBorrow());
        druidDataSource.setTestWhileIdle(config.getTestWhileIdle());
        druidDataSource.setMinIdle(config.getMinIdle());
        druidDataSource.setMaxWait(config.getMaxWait());
        druidDataSource.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMills());
        druidDataSource.setInitialSize(config.getInitialSize());
        druidDataSource.setValidationQuery(config.getValidationQuery());

        Log4jdbcProxyDataSource dataSource = new Log4jdbcProxyDataSource();
        dataSource.setDataSource(druidDataSource);
        dataSource.setDumpSqlMaxLineLength(0);
        return dataSource;
    }

}
