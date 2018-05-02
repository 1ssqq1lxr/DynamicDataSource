package com.wym.starter.datasource;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.*;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Optional.ofNullable;
import static org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable;
import static org.mybatis.spring.SqlSessionUtils.*;

/**
 * 根据上下文中的结构码动态切换数据源
 * @author maenliang
 *
 */
public class DynamicSqlSessionTemplate extends SqlSessionTemplate {  
   
    private final ExecutorType executorType;  
    private final SqlSession sqlSessionProxy;  
    private final PersistenceExceptionTranslator exceptionTranslator;  
    private SqlSessionFactory sqlSessionFactory;

    private  DataSourceManager dataSourceManager;

    public DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    public DynamicSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public DynamicSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        this(sqlSessionFactory, executorType, new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration()
                .getEnvironment().getDataSource(), true));
    }
   
    public DynamicSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType,
                                     PersistenceExceptionTranslator exceptionTranslator) {
   
        super(sqlSessionFactory, executorType, exceptionTranslator);  
   
        this.executorType = executorType;  
        this.exceptionTranslator = exceptionTranslator;

        this.sqlSessionProxy = (SqlSession) newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),  
                new Class[] { SqlSession.class },   
                new SqlSessionInterceptor());  
    }  
   
    /**
     * 根据上下文中的结构码动态切换数据源
     */
    @Override  
    public SqlSessionFactory getSqlSessionFactory() {
        return  ofNullable(dataSourceManager.getSqlSessionFactory()).orElse( this.sqlSessionFactory);
    }
   
    @Override  
    public Configuration getConfiguration() {  
        return this.getSqlSessionFactory().getConfiguration();  
    }  
   
    public ExecutorType getExecutorType() {  
        return this.executorType;  
    }  
   
    public PersistenceExceptionTranslator getPersistenceExceptionTranslator() {  
        return this.exceptionTranslator;  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public <T> T selectOne(String statement) {  
        return this.sqlSessionProxy.<T> selectOne(statement);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public <T> T selectOne(String statement, Object parameter) {  
        return this.sqlSessionProxy.<T> selectOne(statement, parameter);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {  
        return this.sqlSessionProxy.<K, V> selectMap(statement, mapKey);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {  
        return this.sqlSessionProxy.<K, V> selectMap(statement, parameter, mapKey);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {  
        return this.sqlSessionProxy.<K, V> selectMap(statement, parameter, mapKey, rowBounds);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public <E> List<E> selectList(String statement) {  
        return this.sqlSessionProxy.<E> selectList(statement);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public <E> List<E> selectList(String statement, Object parameter) {  
        return this.sqlSessionProxy.<E> selectList(statement, parameter);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {  
        return this.sqlSessionProxy.<E> selectList(statement, parameter, rowBounds);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public void select(String statement, ResultHandler handler) {  
        this.sqlSessionProxy.select(statement, handler);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public void select(String statement, Object parameter, ResultHandler handler) {  
        this.sqlSessionProxy.select(statement, parameter, handler);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {  
        this.sqlSessionProxy.select(statement, parameter, rowBounds, handler);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public int insert(String statement) {  
        return this.sqlSessionProxy.insert(statement);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public int insert(String statement, Object parameter) {  
        return this.sqlSessionProxy.insert(statement, parameter);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public int update(String statement) {  
        return this.sqlSessionProxy.update(statement);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public int update(String statement, Object parameter) {  
        return this.sqlSessionProxy.update(statement, parameter);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public int delete(String statement) {  
        return this.sqlSessionProxy.delete(statement);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public int delete(String statement, Object parameter) {  
        return this.sqlSessionProxy.delete(statement, parameter);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public <T> T getMapper(Class<T> type) {  
        return getConfiguration().getMapper(type, this);  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public void commit() {  
        throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public void commit(boolean force) {  
        throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public void rollback() {  
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public void rollback(boolean force) {  
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public void close() {  
        throw new UnsupportedOperationException("Manual close is not allowed over a Spring managed SqlSession");  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public void clearCache() {  
        this.sqlSessionProxy.clearCache();  
    }  
   
    /** 
     * {@inheritDoc} 
     */  
    public Connection getConnection() {  
        return this.sqlSessionProxy.getConnection();  
    }  
   
    /** 
     * {@inheritDoc} 
     * @since 1.0.2 
     */  
    public List<BatchResult> flushStatements() {  
        return this.sqlSessionProxy.flushStatements();  
    }  
   
    /** 
     * Proxy needed to route MyBatis method calls to the proper SqlSession got from Spring's Transaction Manager It also 
     * unwraps exceptions thrown by {@code Method#invoke(Object, Object...)} to pass a {@code PersistenceException} to 
     * the {@code PersistenceExceptionTranslator}. 
     */  
    private class SqlSessionInterceptor implements InvocationHandler {  
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {  
            final SqlSession sqlSession = getSqlSession(  
                    DynamicSqlSessionTemplate.this.getSqlSessionFactory(),  
                    DynamicSqlSessionTemplate.this.executorType,   
                    DynamicSqlSessionTemplate.this.exceptionTranslator);  
            try {  
                Object result = method.invoke(sqlSession, args);  
                if (!isSqlSessionTransactional(sqlSession, DynamicSqlSessionTemplate.this.getSqlSessionFactory())) {  
                    // force commit even on non-dirty sessions because some databases require  
                    // a commit/rollback before calling close()  
                    sqlSession.commit(true);  
                }  
                return result;  
            } catch (Throwable t) {  
                Throwable unwrapped = unwrapThrowable(t);  
                if (DynamicSqlSessionTemplate.this.exceptionTranslator != null && unwrapped instanceof PersistenceException) {  
                    Throwable translated = DynamicSqlSessionTemplate.this.exceptionTranslator  
                        .translateExceptionIfPossible((PersistenceException) unwrapped);  
                    if (translated != null) {  
                        unwrapped = translated;  
                    }  
                }  
                throw unwrapped;  
            } finally {  
                closeSqlSession(sqlSession, DynamicSqlSessionTemplate.this.getSqlSessionFactory());  
            }  
        }  
    }
}
