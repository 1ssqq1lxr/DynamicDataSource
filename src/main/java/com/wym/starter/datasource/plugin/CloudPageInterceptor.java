package com.wym.starter.datasource.plugin;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

@Intercepts({ @Signature(type = Executor.class, method = "query", args = {
		MappedStatement.class, Object.class, RowBounds.class,
		ResultHandler.class }) })
public class CloudPageInterceptor implements Interceptor {

	private static int MAPPEDSTATEMENT_INDEX = 0;

	private static int PARAMETER_INDEX = 1;

	private static int ROWBOUNDS_INDEX = 2;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		final Object[] queryArgs = invocation.getArgs();
		final MappedStatement mappedStatement = (MappedStatement) queryArgs[MAPPEDSTATEMENT_INDEX];
		final Object parameter = queryArgs[PARAMETER_INDEX];
		BoundSql boundSql = mappedStatement.getBoundSql(parameter);
		String sql = boundSql.getSql();

		int index = sql.toLowerCase().lastIndexOf("limit ");
		if (index > -1) {
			int c = sql.lastIndexOf(")");
			if (c < index) {
				return invocation.proceed();
			}
		}
		return appendLimit(invocation, queryArgs, boundSql, sql);
	}

	private Object appendLimit(Invocation invocation, Object[] queryArgs, BoundSql boundSql, String sql) throws Throwable {
		final MappedStatement mappedStatement = (MappedStatement) queryArgs[MAPPEDSTATEMENT_INDEX];
		final RowBounds rowBounds = (RowBounds) queryArgs[ROWBOUNDS_INDEX];
		int start = rowBounds.getOffset();
		int limit = rowBounds.getLimit();
		boolean hasForUpdate = false;

		if (sql.endsWith("for update")) {
			// 去掉末尾的for update
			sql = sql.substring(0, sql.length() - " for update".length());
			hasForUpdate = true;
		}
		StringBuffer sb = new StringBuffer(sql);
		sb.append(" limit ");
		if (start > 0) {
			sb.append(start).append(",").append(limit);
		} else {
			sb.append(limit);
		}

		if (hasForUpdate) {
			sb.append("for update");
		}
		queryArgs[ROWBOUNDS_INDEX] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
		BoundSql newBoundSql = createNewBoundSql(mappedStatement, boundSql, sb.toString());
		MappedStatement newMappedStatement = createNewMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
		queryArgs[MAPPEDSTATEMENT_INDEX] = newMappedStatement;
		return invocation.proceed();
	}

	private BoundSql createNewBoundSql(MappedStatement mappedStatement,
			BoundSql oldBoundSql, String sql) {
		BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(),
				sql, oldBoundSql.getParameterMappings(),
				oldBoundSql.getParameterObject());
		for (ParameterMapping mapping : oldBoundSql.getParameterMappings()) {
			String prop = mapping.getProperty();
			if (oldBoundSql.hasAdditionalParameter(prop)) {
				newBoundSql.setAdditionalParameter(prop,
						oldBoundSql.getAdditionalParameter(prop));
			}
		}

		return newBoundSql;
	}

	class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

	private MappedStatement createNewMappedStatement(MappedStatement ms,
			SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(
				ms.getConfiguration(), ms.getId(), newSqlSource,
				ms.getSqlCommandType());

		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		String[] pros = ms.getKeyProperties();
		StringBuffer sb = new StringBuffer();
		if (pros != null && pros.length > 0) {
			for (String p : pros) {
				sb.append(p).append(",");
			}
			if (sb.toString().contains(",")) {
				sb.substring(0, sb.toString().length() - 1);
			}
		}

		builder.keyProperty(sb.toString());
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	@Override
	public Object plugin(Object target) {

		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}

}