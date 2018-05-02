package auto;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.miemiedev.mybatis.paginator.OffsetLimitInterceptor;
import com.wym.starter.datasource.DafultDynamicIdSelector;
import com.wym.starter.datasource.DataSourceManager;
import com.wym.starter.datasource.DynamicSqlSessionTemplate;
import com.wym.starter.datasource.config.AcmDBConfigMgr;
import com.wym.starter.datasource.plugin.CloudPageInterceptor;
import com.wym.starter.datasource.properties.AcmProperties;
import com.wym.starter.datasource.properties.MybatisProperties;
import com.wym.starter.datasource.spi.DynamicIdSelector;
import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 描述: TODO:
 *
 * 版本: 1.0 JDK: since 1.8
 */
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties({MybatisProperties.class,AcmProperties.class})
public class MyBatisConfig {

	private static final Logger logger = LoggerFactory
			.getLogger(MyBatisConfig.class);

	@Bean("transactionManager")
	@Primary
	public PlatformTransactionManager txManager(@Qualifier("dataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}


	/**
	 * 动态切换数据源
	 * @return
	 */
	@Bean
	@Primary
    @ConditionalOnClass({SqlSessionFactory.class,DataSourceManager.class})
	public DynamicSqlSessionTemplate createCustomSqlSessionTemplate(@Autowired SqlSessionFactory sqlSessionFactory,
                                                                    @Autowired DataSourceManager dataSourceManager) {
			DynamicSqlSessionTemplate customSqlSessionTemplate = new DynamicSqlSessionTemplate(sqlSessionFactory);
            customSqlSessionTemplate.setDataSourceManager(dataSourceManager);
			return customSqlSessionTemplate;
	}



	@Bean("sqlSessionFactory")
	@Primary
	public SqlSessionFactory sqlSessionFactoryBean(@Autowired MybatisProperties mybatisProperties) throws Exception {

		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		sqlSessionFactoryBean.setMapperLocations(resolver.getResources(mybatisProperties.getMapperPath()));

		sqlSessionFactoryBean.setTypeAliasesPackage(mybatisProperties.getTypeAliasesPackage());

		Interceptor[] plugins = new Interceptor[] { new CloudPageInterceptor() ,getInterceptor()};
		sqlSessionFactoryBean.setPlugins(plugins);

		return sqlSessionFactoryBean.getObject();
	}

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		Log4jdbcProxyDataSource dataSource = new Log4jdbcProxyDataSource();
		DruidDataSource druidDataSource = new DruidDataSource();
		dataSource.setDataSource(druidDataSource);
		dataSource.setDumpSqlMaxLineLength(0);
		return dataSource;
	}


	/**
	 *
	 * @return
	 */
	public OffsetLimitInterceptor getInterceptor() {
		OffsetLimitInterceptor offsetLimitInterceptor = new OffsetLimitInterceptor();
		offsetLimitInterceptor
				.setDialectClass("com.github.miemiedev.mybatis.paginator.dialect.MySQLDialect");
		return offsetLimitInterceptor;

	}


	@Bean
    @ConditionalOnClass({DataSourceManager.class, AcmProperties.class})
	public AcmDBConfigMgr initAcmDBConfigMgr(@Autowired DataSourceManager dataSourceManager,
                                             @Autowired AcmProperties acmProperties ){
        AcmDBConfigMgr acmDBConfigMgr = new AcmDBConfigMgr();
        acmDBConfigMgr.setAcmProperties(acmProperties);
        acmDBConfigMgr.setDataSourceManager(dataSourceManager);
        return  acmDBConfigMgr;
    }



	@Bean
    @ConditionalOnClass({DynamicIdSelector.class})
    public DataSourceManager initDataSourceManager(@Autowired DynamicIdSelector dynamicIdSelector,@Autowired MybatisProperties mybatisProperties ){
	    DataSourceManager dataSourceManager = new DataSourceManager();
        dataSourceManager.setDynamicIdSelector(dynamicIdSelector);
        dataSourceManager.setMapperPath(mybatisProperties.getMapperPath());
        return  dataSourceManager;
    }

	@Bean
    @ConditionalOnMissingBean(value = {DynamicIdSelector.class})
	public DynamicIdSelector initDynamicIdSelector(){
	    return  new DafultDynamicIdSelector();
    }
}
