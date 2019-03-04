/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.base;

import com.shiba.inu.util.ParamOut;
import com.shiba.inu.tran.TransactionServiceImpl;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author mpfleeger
 */
public class BaseServiceImpl implements BaseService {

    protected Connection getConnection() {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/finConn");
            return ds.getConnection();
        } catch (NamingException ne) {
            System.out.println("Could not resolved data source name: " + ne.getMessage());
        } catch (SQLException ne) {
            System.out.println("Could not obtain db connection: " + ne.getMessage());
        }
        return null;
    }

    protected void closeConn(Connection conn, ResultSet rs, Statement stmt) {
        try {
            if (rs != null) {
                rs.close();
            }

            if (stmt != null) {
                stmt.close();
            }

            if (conn != null) {
                conn.close();
            }

        } catch (SQLException se) {
            System.out.println("Error on closeConn() " + se.getMessage());
        }
    }

    private String buildSqlStmt(String stmt, Object[] params) {
        StringBuilder sqlStmtBuilder = new StringBuilder();
        sqlStmtBuilder.append("{ call ");
        sqlStmtBuilder.append(stmt);
        if (params != null && params.length > 0) {
            sqlStmtBuilder.append("(");
            for (int i = 0; i < params.length; i++) {
                sqlStmtBuilder.append("?");
                if (i < params.length - 1) {
                    sqlStmtBuilder.append(",");
                }
            }
            sqlStmtBuilder.append(")");

        }
        sqlStmtBuilder.append("}");

        return sqlStmtBuilder.toString();
    }

    protected Object[] callStoredProcedure(Connection conn, String stmt, Object[] params) {
        OracleCallableStatement ocstmt = null;
        String sqlStmt = buildSqlStmt(stmt, params);

        try {
            ocstmt = (OracleCallableStatement) conn.prepareCall(sqlStmt);

            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof ParamOut) {
                    ocstmt.setObject(i + 1, ((ParamOut) params[i]).getParamValue());
                    ocstmt.registerOutParameter(i + 1, ((ParamOut) params[i]).getSqlType());
                } else {
                    ocstmt.setObject(i + 1, params[i]);
                }
            }

            ocstmt.executeUpdate();

            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof ParamOut) {
                    if (ocstmt.getObject(i + 1) != null) {
                        ParamOut outValue = (ParamOut) params[i];
                        outValue.setParamValue(ocstmt.getObject(i + 1));
                    }

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConn(conn, null, ocstmt);
        }

        return params;

    }
}
