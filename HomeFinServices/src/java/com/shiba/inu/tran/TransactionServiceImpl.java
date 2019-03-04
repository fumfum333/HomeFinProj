/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.tran;

import com.shiba.inu.base.BaseServiceImpl;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author mpfleeger
 */
public class TransactionServiceImpl extends BaseServiceImpl implements TransactionService {

    private final String sql = "select transaction_id, source_id, transaction_date, description, trans_amount, category_id from transaction_item";
    private List<TransactionItem> transactionList;

    @Override
    public List<TransactionItem> getTransactions() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        if (transactionList == null) {
            return fetchDbTransactionItems();
        }
        return transactionList;
    }

    private List<TransactionItem> fetchDbTransactionItems() {
        List<TransactionItem> list = new ArrayList(0);
        Connection conn = super.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                TransactionItem ti = new TransactionItem();
                ti.setTransactionId(rs.getLong("transaction_id"));
                ti.setSourceId(rs.getString("source_id"));
                ti.setTransactionDate(rs.getTimestamp("transaction_date"));
                ti.setDescription(rs.getString("description"));
                ti.setTransAmount(rs.getBigDecimal("trans_amount"));
                ti.setCategoryId(rs.getString("category_id"));

                list.add(ti);
            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConn(conn, rs, stmt);
        }
        this.setTransactionList(list);
        return list;

    }

    private BigDecimal fetchTotalTransAmountByYear(Integer year) {
        final String sqlTotal = "select NVL(SUM(ABS(TRANS_AMOUNT)),0) total from TRANSACTION_ITEM WHERE YEAR = ? and expense_ind = 'Y' ";
        Connection conn = super.getConnection();
        PreparedStatement stmt = null;
        BigDecimal ret = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sqlTotal);
            stmt.setInt(1, year);

            rs = stmt.executeQuery();

            if (rs.next()) {
                ret = rs.getBigDecimal("total");

            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            closeConn(conn, rs, stmt);
        }

        return ret;

    }

    private List<TransactionSum> fetchTotalTransAmountByYearCat(Integer year, String category) {
        final String sqlTotal = "select NVL(SUM(TRANS_AMOUNT),0) TRANS_AMOUNT, CATEGORY_ID from TRANSACTION_ITEM WHERE YEAR = ? and CATEGORY_ID = ? and expense_ind = 'Y' GROUP BY CATEGORY_ID order by category_id";
        Connection conn = super.getConnection();
        PreparedStatement stmt = null;
        List<TransactionSum> ret = new ArrayList<>(0);
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sqlTotal);
            stmt.setInt(1, year);
            stmt.setString(2, category);

            rs = stmt.executeQuery();
            int seq = 0;
            while (rs.next()) {
                TransactionSum sum = new TransactionSum();
                sum.setSummaryType("YEAR_CAT");
                sum.setCategoryId(rs.getString("CATEGORY_ID"));
                sum.setSeqId(++seq);
                sum.setAmount(rs.getBigDecimal("TRANS_AMOUNT"));
                ret.add(sum);

            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            closeConn(conn, rs, stmt);
        }
        return ret;
    }

    private List<TransactionSum> fetchTranYearSummary(Integer year) {
        final String sqlTotal = "select NVL(SUM(ti.TRANS_AMOUNT),0) TRANS_AMOUNT, ti.CATEGORY_ID, ec.DESCRIPTION from TRANSACTION_ITEM ti, EXPENSE_CATEGORY ec WHERE ti.YEAR = ? and ti.expense_ind = 'Y' and ti.category_id = ec.category_id GROUP BY ti.CATEGORY_ID, ec.DESCRIPTION order by ti.category_id";
        Connection conn = super.getConnection();
        PreparedStatement stmt = null;
        List<TransactionSum> ret = new ArrayList<>(0);
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sqlTotal);
            stmt.setInt(1, year);

            rs = stmt.executeQuery();
            int seq = 0;
            while (rs.next()) {
                TransactionSum sum = new TransactionSum();
                sum.setSummaryType("YEAR_CAT");
                sum.setCategoryId(rs.getString("CATEGORY_ID"));
                sum.setCategoryDescription(rs.getString("DESCRIPTION"));
                sum.setSeqId(++seq);
                sum.setAmount(rs.getBigDecimal("TRANS_AMOUNT"));

                ret.add(sum);

            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            closeConn(conn, rs, stmt);
        }
        return ret;
    }

    private List<TransactionItem> fetchTransByMonth(Integer year, Integer month) {
        final String sqlTotal = "select transaction_id, source_id, transaction_date, description, category_id, trans_amount from transaction_item where expense_ind = 'Y' and year = ? and month = ?";
        Connection conn = super.getConnection();
        PreparedStatement stmt = null;
        List<TransactionItem> ret = new ArrayList<>(0);
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sqlTotal);
            stmt.setInt(1, year);
            stmt.setInt(2, month);

            rs = stmt.executeQuery();
            int seq = 0;
            while (rs.next()) {
                TransactionItem item = new TransactionItem();
                item.setTransactionId(rs.getLong("transaction_id"));
                item.setCategoryId(rs.getString("CATEGORY_ID"));
                item.setSourceId(rs.getString("source_id"));
                item.setTransAmount(rs.getBigDecimal("TRANS_AMOUNT"));
                item.setDescription(rs.getString("description"));
                item.setTransactionDate(rs.getDate("transaction_date"));
                ret.add(item);

            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConn(conn, rs, stmt);
        }

        return ret;
    }

    private TransactionItem insertTranItem(TransactionItem tranItem) {
        System.out.println("TEST GIT GIT GIT");
        
        final String sqlInsert = "{call transaction_pk.insert_transaction(?,?,?,?,?,?)} ";
        Connection conn = super.getConnection();
        OracleCallableStatement stmt = null;

        try {
            stmt = (OracleCallableStatement) conn.prepareCall(sqlInsert);
            stmt.setString(1, tranItem.getSourceId());
            stmt.setTimestamp(2, new Timestamp(tranItem.getTransactionDate().getTime()));
            stmt.setString(3, tranItem.getDescription());
            stmt.setString(4, tranItem.getCategoryId());
            stmt.setBigDecimal(5, tranItem.getTransAmount());
            stmt.registerOutParameter(6, OracleTypes.NUMBER);

            stmt.executeUpdate();
            if(stmt.getBigDecimal(6) != null) {
                BigDecimal retNum = (BigDecimal)stmt.getBigDecimal(6);
                tranItem.setTransactionId(retNum.longValue());
            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConn(conn, null, stmt);
        }

        return tranItem;
    }

    public void setTransactionList(List<TransactionItem> transactionList) {
        this.transactionList = transactionList;
    }

    @Override
    public BigDecimal getTotalYear(Integer year) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return fetchTotalTransAmountByYear(year);
    }

    @Override
    public List<TransactionSum> getTransactionSumByYearCat(Integer year, String categoryId) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return fetchTotalTransAmountByYearCat(year, categoryId);
    }

    @Override
    public List<TransactionItem> getTransactionByMonth(Integer year, Integer month) {
        return fetchTransByMonth(year, month);
    }

    @Override
    public TransactionItem createTransactionItem(TransactionItem item) {
        return insertTranItem(item);
    }

    @Override
    public List<TransactionSum> fetchTranSummaryByYear(Integer year) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return this.fetchTranYearSummary(year);
    }

    private List<TransactionSum> fetchTransSumByCategory(String categoryId, Integer year) {
        final String sqlTotal = "select sum(trans_amount) trans_amount, month from transaction_item where expense_ind = 'Y' and year = ? and category_id= ? group by month order by month";
        Connection conn = super.getConnection();
        PreparedStatement stmt = null;
        List<TransactionSum> ret = new ArrayList<>(0);
        ResultSet rs = null;
        int monthCounter = 0;
        try {
            stmt = conn.prepareStatement(sqlTotal);
            stmt.setInt(1, year);
            stmt.setString(2, categoryId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                monthCounter++;
                TransactionSum item = new TransactionSum();
                item.setCategoryId(categoryId);
                item.setAmount(rs.getBigDecimal("trans_amount"));
                item.setYear(year);
                item.setMonth(rs.getInt("month"));

                if (monthCounter < item.getMonth()) {
                    TransactionSum item0 = new TransactionSum();
                    item0.setCategoryId(categoryId);
                    item0.setAmount(BigDecimal.ZERO);
                    item0.setYear(year);
                    item0.setMonth(monthCounter++);
                    ret.add(item0);
                }

                ret.add(item);
            }

            while (monthCounter < 12) {
                TransactionSum item0 = new TransactionSum();
                item0.setCategoryId(categoryId);
                item0.setAmount(BigDecimal.ZERO);
                item0.setYear(year);
                item0.setMonth(++monthCounter);
                ret.add(item0);
            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConn(conn, rs, stmt);
        }

        return ret;
    }

    @Override
    public List<TransactionSum> fetchSumByCategory(String categoryId, Integer year) {
        return fetchTransSumByCategory(categoryId, year);
    }
}
