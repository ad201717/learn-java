package com.howe.learn.trace.mysql;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementInterceptorV2;

import java.sql.SQLException;
import java.util.Properties;

/**
 * @Author Karl
 * @Date 2017/3/16 14:30
 */
public class MysqlStatementInterceptor implements StatementInterceptorV2 {
    public ThreadLocal<Long> startTimeLocal = new ThreadLocal<Long>();
    @Override
    public void init(Connection connection, Properties properties) throws SQLException {

    }

    @Override
    public ResultSetInternalMethods preProcess(String s, Statement statement, Connection connection) throws SQLException {
        System.out.println("start to process sql:" + parseSql(s, statement));
        startTimeLocal.set(System.currentTimeMillis());
        return null;
    }

    @Override
    public boolean executeTopLevelOnly() {
        return true;
    }

    @Override
    public void destroy() {

    }

    @Override
    public ResultSetInternalMethods postProcess(String s, Statement statement, ResultSetInternalMethods resultSetInternalMethods, Connection connection, int i, boolean b, boolean b1, SQLException e) throws SQLException {
        System.out.println("end to process sql:" + parseSql(s, statement));
        System.out.println("sql cost:" + (System.currentTimeMillis() - startTimeLocal.get()));
        return null;
    }

    private String parseSql(String s, Statement statement) {
        String sql = null;
        if (statement instanceof PreparedStatement) {
            sql = ((PreparedStatement)statement).getPreparedSql();
        } else {
            sql = s;
        }
        return sql;
    }
}
