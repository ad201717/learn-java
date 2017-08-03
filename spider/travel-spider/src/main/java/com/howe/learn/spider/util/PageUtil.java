package com.howe.learn.spider.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.howe.learn.spider.basic.domain.PageResult;

@Intercepts({
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }),
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }),
		@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class }) })
public class PageUtil implements Interceptor {

	private static ThreadLocal<PageResult> threadLocal = new ThreadLocal<PageResult>();

	/**
	 * 开始分页
	 * 
	 * @param pageNum
	 * @param pageSize
	 */
	public static PageResult startPage(int pageIndex) {
		PageResult page = new PageResult();
		page.setPageIndex(pageIndex);
		threadLocal.set(page);
		return page;
	}

	public static PageResult endPage() {
		PageResult page = threadLocal.get();
		threadLocal.remove();
		return page;
	}

	@SuppressWarnings("unchecked")
	public Object intercept(Invocation invocation) throws Throwable {
		try {
			if (invocation.getTarget() instanceof Executor) {
				Object[] args = invocation.getArgs();
				MappedStatement mStmt = (MappedStatement) args[0];
				if (mStmt.getId().endsWith("PageResult")) {
					Map<String, Object> hashMap = new HashMap<String, Object>();
					Map<String, Object> params = (Map<String, Object>) args[1];
	
					PageResult page = startPage((Integer) params.get("1"));
					try {
						Object arg = params.get("2");
						if (arg != null) page.setRecordsPerpage((Integer)arg);
					} catch (BindingException ex) {}
					hashMap.put("startIndex", page.getPageIndex() * page.getRecordsPerpage());
					hashMap.put("records", page.getRecordsPerpage());
	
					hashMap.putAll((Map<String, Object>) params.get("0"));
					args[1] = hashMap;
	
					List<PageResult> list = (List<PageResult>) invocation.proceed();
					if (list.get(0).getList().isEmpty() && list.get(0).getTotalRecords() > 0) {
						page.setPageIndex(page.getPageIndex() - 1);
						hashMap.put("startIndex", page.getPageIndex() * page.getRecordsPerpage());
						page.setList((List<Object>) invocation.proceed());
						return Arrays.asList(page);
					}
					return list;
				}
				return invocation.proceed();
			} else if (invocation.getTarget() instanceof StatementHandler) {
				PageResult page = threadLocal.get();
				if (page == null) {
					return invocation.proceed();
				}
	
				StatementHandler stmtHandler = (StatementHandler) invocation.getTarget();
				MetaObject metaObj = SystemMetaObject.forObject(stmtHandler);
	
				while (metaObj.hasGetter("h")) {
					Object obj = metaObj.getValue("h");
					metaObj = SystemMetaObject.forObject(obj);
				}
	
				while (metaObj.hasGetter("target")) {
					Object obj = metaObj.getValue("target");
					metaObj = SystemMetaObject.forObject(obj);
				}
	
				MappedStatement mStmt = (MappedStatement) metaObj.getValue("delegate.mappedStatement");
	
				if (!mStmt.getId().endsWith("PageResult")) {
					return invocation.proceed();
				}
	
				BoundSql boundSql = (BoundSql) metaObj.getValue("delegate.boundSql");
	
				Connection conn = (Connection) invocation.getArgs()[0];
				setPageParam(conn, mStmt, boundSql);
				return invocation.proceed();
			} else if (invocation.getTarget() instanceof ResultSetHandler) {
				MetaObject metaObj = SystemMetaObject.forObject(invocation.getTarget());
				
				while (metaObj.hasGetter("h")) {
					Object obj = metaObj.getValue("h");
					metaObj = SystemMetaObject.forObject(obj);
				}
	
				while (metaObj.hasGetter("target")) {
					Object obj = metaObj.getValue("target");
					metaObj = SystemMetaObject.forObject(obj);
				}
	
				MappedStatement mStmt = (MappedStatement) metaObj.getValue("mappedStatement");
	
				if (!mStmt.getId().endsWith("PageResult")) {
					return invocation.proceed();
				}
				
				PageResult page = threadLocal.get();
				if (page == null) {
					return invocation.proceed();
				}
				if (page.getTotalRecords() > 0) {
					List<Object> result = (List<Object>) invocation.proceed();
					page.setList(result);
				}
	
				return Arrays.asList(endPage());
			}
			return null;
		} catch(Throwable e) {
			if (threadLocal.get() instanceof PageResult)
				threadLocal.remove();
			throw e;
		}
		
	}

	private void setPageParam(Connection conn, MappedStatement mStmt, BoundSql boundSql) throws SQLException {
		String id = mStmt.getId().substring(0, mStmt.getId().length() - "PageResult".length()) + "Count";
		MappedStatement mStmt2 = mStmt.getConfiguration().getMappedStatement(id);

		String countSql = mStmt2.getBoundSql(boundSql.getParameterObject()).getSql();
		PreparedStatement pStmt = conn.prepareStatement(countSql);
		ResultSet rs = null;

		BoundSql countBs = new BoundSql(mStmt.getConfiguration(), countSql, boundSql.getParameterMappings(),
				boundSql.getParameterObject());
		setParameters(pStmt, mStmt, countBs, boundSql.getParameterObject());
		rs = pStmt.executeQuery();
		int totalCount = 0;
		if (rs.next()) {
			totalCount = rs.getInt(1);
		}
		threadLocal.get().setTotalRecords(totalCount);
	}

	private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
			Object parameterObject) throws SQLException {
		ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
		parameterHandler.setParameters(ps);
	}

	public Object plugin(Object target) {
		if (target instanceof StatementHandler || target instanceof ResultSetHandler || target instanceof Executor) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	public void setProperties(Properties properties) {
		// System.out.println(properties);

	}

}
