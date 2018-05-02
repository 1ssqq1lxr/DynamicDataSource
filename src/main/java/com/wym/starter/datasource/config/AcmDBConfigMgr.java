package com.wym.starter.datasource.config;

import com.alibaba.edas.acm.ConfigService;
import com.alibaba.edas.acm.listener.ConfigChangeListener;
import com.alibaba.fastjson.JSONObject;
import com.wym.starter.datasource.DataSourceManager;
import com.wym.starter.datasource.properties.AcmProperties;
import com.wym.starter.datasource.spi.DynamicAcmNotice;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class AcmDBConfigMgr implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(AcmDBConfigMgr.class);

    private DataSourceManager dataSourceManager;

    private AcmProperties acmProperties;

    @Autowired
    private DynamicAcmNotice dynamicAcmNotice;

    static ConcurrentHashMap<String, OrgDSConfig> orgDSConfigMap = new ConcurrentHashMap<String, OrgDSConfig>();

    List<OrgDSConfig> orgDSConfigs = null;

    @PostConstruct
    public void init() {
        ConfigService.setDebug(false);
        // 初始化配置服务，控制台通过示例代码自动获取下面参数
        ConfigService.init(acmProperties.getEndpoint(), acmProperties.getNamespace(), acmProperties.getAccessKey(), acmProperties.getSecretKey());
    }

    public  List<String> getInstCodeList() {
        return  orgDSConfigMap.keySet().stream()
                .collect(Collectors.toList());
    }

    public static OrgDSConfig getDSConfig(String orgCode) {
        OrgDSConfig orgDSConfig = orgDSConfigMap.get(orgCode);
        return orgDSConfig;
    }

    private List<OrgDSConfig> convert(List<DataSourceConfig> dataSourceConfig) {
        return ofNullable(dataSourceConfig)
                .map(dataSourceConfigs -> dataSourceConfigs.stream().map(dataSourceConfig1 -> {
                    OrgDSConfig orgDSConfig ;
                    List<OrgCodeDBName> listOrgCodeDBName = dataSourceConfig1.getListOrgCodeDBName();
                    if(CollectionUtils.isNotEmpty(listOrgCodeDBName)) {
                        for(OrgCodeDBName orgCodeDBName : listOrgCodeDBName) {
                            orgDSConfig = new OrgDSConfig();
                            orgDSConfig.setMaxIdle(dataSourceConfig1.getMaxIdle());
                            orgDSConfig.setMaxWait(dataSourceConfig1.getMaxWait());
                            orgDSConfig.setMinIdle(dataSourceConfig1.getMinIdle());
                            orgDSConfig.setUserName(dataSourceConfig1.getUserName());
                            orgDSConfig.setTestOnBorrow(dataSourceConfig1.getTestOnBorrow());
                            orgDSConfig.setValidationQuery(dataSourceConfig1.getValidationQuery());
                            orgDSConfig.setInitialSize(dataSourceConfig1.getInitialSize());
                            orgDSConfig.setPassword(dataSourceConfig1.getPassword());
                            orgDSConfig.setTestWhileIdle(dataSourceConfig1.getTestWhileIdle());
                            orgDSConfig.setTimeBetweenEvictionRunsMills(dataSourceConfig1.getTimeBetweenEvictionRunsMills());
                            orgDSConfig.setMaxActive(dataSourceConfig1.getMaxActive());
                            orgDSConfig.setOrgCode(orgCodeDBName.getOrgCode());
                            orgDSConfig.setUrl(dataSourceConfig1.getUrlBefore()+orgCodeDBName.getDbName()+dataSourceConfig1.getUrlAfter());
                            return  orgDSConfig;
                        }
                    }
                    return  null;
                }).collect(Collectors.toList())).orElse(null);
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化配置服务，控制台通过示例代码自动获取下面参数
        String configStr = ConfigService.getConfig(acmProperties.getDataId(), acmProperties.getGroup(), acmProperties.getTimeout());
        AcmConfig acmConfig = JSONObject.parseObject(configStr, AcmConfig.class);
        initDataSources(acmConfig);
        ConfigService.addListener(acmProperties.getDataId(), acmProperties.getGroup(), new ConfigChangeListener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                // 当配置更新后，通过该回调函数将最新值返回给用户
                List<OrgDSConfig> convert = new ArrayList<>();
                List<String> initList = getInstCodeList();
                if (StringUtils.isNotBlank(configInfo)) {
                    AcmConfig acmConfig = JSONObject.parseObject(configInfo, AcmConfig.class);
                    List<DataSourceConfig> listDataSourceConfig = acmConfig.getDataSourceConfig();
                    //如果新增加机构，需要动态创建数据库表结构
                    convert = convert(listDataSourceConfig);
                    for(OrgDSConfig config:convert) {
                        if(!initList.contains(config.getOrgCode())) {
                            try{
                                ofNullable(dynamicAcmNotice)
                                        .ifPresent(service -> service.notify(config.getOrgCode()));
                            }catch(Exception e) {
                                logger.error("Acm 配置变更 通知异常：{}",config);
                            }
                        }
                    }
                }
                if(CollectionUtils.isNotEmpty(convert)) {
                    for (OrgDSConfig orgDSConfig : convert) {
                        orgDSConfigMap.put(orgDSConfig.getOrgCode(), orgDSConfig);
                    }
                }
                //刷新数据源
                dataSourceManager.initOrgDataSourceMap(orgDSConfigMap);
            }
        });
    }

    private void initDataSources(AcmConfig acmConfig)  throws Exception{
        List<DataSourceConfig> dataSourceConfig = acmConfig.getDataSourceConfig();
        List<OrgDSConfig> orgDSConfigs = convert(dataSourceConfig);
        this.orgDSConfigs = orgDSConfigs;
        ofNullable(orgDSConfigs).ifPresent(orgDSConfigs1 -> orgDSConfigs1.stream().forEach(orgDSConfig -> {
            orgDSConfigMap.put(orgDSConfig.getOrgCode(), orgDSConfig);
        }));
        dataSourceManager.initOrgDataSourceMap(orgDSConfigMap);
    }


    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    public void setAcmProperties(AcmProperties acmProperties) {
        this.acmProperties = acmProperties;
    }
}
