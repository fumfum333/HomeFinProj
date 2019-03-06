/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.imp;

import com.shiba.inu.base.BaseServiceImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OraclePreparedStatement;

/**
 *
 * @author fmatsuura
 */
public class ImportTransServiceImpl extends BaseServiceImpl implements ImportTransService {

    @Override
    public int importFileRecords(String filePath) {
        int num = 0;

        List<ImportTranItem> itemList = new ArrayList<>();

        Path pathToFile = Paths.get(filePath);
        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
            String line = br.readLine();

            while (line != null) {
                String[] attributes = line.split(",");
                if (attributes != null && attributes.length > 0) {
                    if ("Transaction date".equalsIgnoreCase(attributes[0])) {

                        line = br.readLine();
                        continue;
                    }
                }

                ImportTranItem tranItm = createImportRecord(attributes);
                itemList.add(tranItm);

                line = br.readLine();
            }

        } catch (IOException ioe) {
            Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ioe);
        }

        if (itemList.size() > 0) {
            num = saveToDb(itemList);
        }

        return num;

    }

    private int saveToDb(List<ImportTranItem> list) {
        final String sqlInsert = "insert into import_transaction_items(transaction_date,posted_date, description,category,debit,credit,import_amount,memo) values(?,?,?,?,?,?,?,?)";
        Connection conn = super.getConnection();
        OraclePreparedStatement stmt = null;
        int count = 0;

        try {
            stmt = (OraclePreparedStatement) conn.prepareStatement(sqlInsert);

            for (ImportTranItem item : list) {

                stmt.setTimestamp(1, new Timestamp(item.getTransactionDate().getTime()));
                stmt.setTimestamp(2, new Timestamp(item.getPostedDate().getTime()));
                stmt.setString(3, item.getDescription());
                stmt.setString(4, item.getCategory());
                stmt.setBigDecimal(5, item.getDebit());
                stmt.setBigDecimal(6, item.getCredit());
                stmt.setBigDecimal(7, item.getImportAmount());
                stmt.setString(8, item.getMemo());
                int cnt = stmt.executeUpdate();
                if(cnt > 0)
                    count++;
            }
            conn.commit();
        } catch (SQLException ex) {
            try {
                if(conn != null)
                conn.rollback();
                Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex1) {
                Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            closeConn(conn, null, stmt);
        }

        return count;
    }

    private ImportTranItem createImportRecord(String[] attributes) {
        ImportTranItem item = new ImportTranItem();
        if (attributes.length == 5) {
            try {
                Date transactionDate = new SimpleDateFormat("MM/dd/yyyy").parse(attributes[0]);
                item.setTransactionDate(transactionDate);
                item.setPostedDate(transactionDate);
                item.setDescription(attributes[2]);
                item.setMemo(attributes[3]);
                item.setImportAmount(new BigDecimal(attributes[4]));

            } catch (ParseException ex) {
                Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date transactionDate = dFormat.parse(attributes[0]);
                java.util.Date postedDate = dFormat.parse(attributes[1]);      
                //Date transactionDate = new SimpleDateFormat("YYYY-MM-dd").parse(attributes[0]);
                //Date postedDate = new SimpleDateFormat("YYYY-MM-dd").parse(attributes[1]);
                item.setTransactionDate(transactionDate);
                item.setPostedDate(postedDate);
                item.setDescription(attributes[3]);
                item.setCategory(attributes[4]);
                if (attributes[5] != null && !"".equals(attributes[5])) {
                    item.setDebit(new BigDecimal(attributes[5]));
                }

                if (attributes.length > 6) {
                    item.setCredit(new BigDecimal(attributes[6]));
                }

            } catch (ParseException ex) {
                Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return item;

    }

    public List<ImportTranItem> getPendingImportItems(String importDate) {
        final String sqlTotal = "select import_seq_id, transaction_date, posted_date, description, category, debit,credit, import_date, mark_ind, import_amount, memo from import_transaction_items where import_status is null and trunc(import_date)=TO_DATE(?,'MM/DD/YYYY') order by import_date, transaction_date";
        Connection conn = super.getConnection();
        PreparedStatement stmt = null;
        List<ImportTranItem> ret = new ArrayList<>(0);
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sqlTotal);
            stmt.setString(1, importDate);
            rs = stmt.executeQuery();
            while (rs.next()) {
                ImportTranItem item = new ImportTranItem();
                item.setImportSeqId(rs.getLong("import_seq_id"));
                item.setTransactionDate(rs.getDate("transaction_date"));
                item.setPostedDate(rs.getDate("posted_date"));
                item.setDescription(rs.getString("description"));
                item.setCategory(rs.getString("category"));
                item.setDebit(rs.getBigDecimal("debit"));
                item.setCredit(rs.getBigDecimal("credit"));
                item.setImportDate(rs.getDate("posted_date"));
                item.setMarkInd(rs.getString("mark_ind"));
                item.setImportAmount(rs.getBigDecimal("import_amount"));
                item.setMemo(rs.getString("memo"));
                ret.add(item);

            }

        } catch (SQLException ex) {
            Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            closeConn(conn, rs, stmt);
        }
        return ret;
    }

    @Override
    public List<ImportTranItem> getAllPendingTransactions(String importDate) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return this.getPendingImportItems(importDate);
    }

    @Override
    public List<String> getListOfImportedDates() {
        final String sql = "select distinct to_char(trunc(import_date),'MM-dd-YYYY') import_date from import_transaction_items where import_status is null";
        Connection conn = super.getConnection();
        PreparedStatement stmt = null;
        List<String> list = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();
            list = new ArrayList<>();
            while (rs.next()) {
                String ret = rs.getString("import_date");
                list.add(ret);

            }

        } catch (SQLException ex) {
            Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            closeConn(conn, rs, stmt);
        }

        return list;
    }

    @Override
    public String updateImportTransaction(String markInd, Long importSeqId) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        final String sql = "update import_transaction_items iti set mark_ind = ? where import_seq_id = ?";
        Connection conn = super.getConnection();
        PreparedStatement stmt = null;
        String ret = "SUCCESS";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, markInd);
            stmt.setLong(2, importSeqId);

            stmt.executeUpdate();

            conn.commit();

        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            ret = "ERROR";
        } finally {

            closeConn(conn, null, stmt);
        }

        return ret;

    }
    
    public List<ImportTranItem> getMarkIndImportItems(String importDate) {
        final String sqlTotal = "select transaction_date, posted_date, description, category, debit,credit, import_date, import_amount, memo from import_transaction_items where mark_ind ='Y' and trunc(import_date)=TO_DATE(?,'MM/DD/YYYY') order by import_seq_id";
        Connection conn = super.getConnection();
        PreparedStatement stmt = null;
        List<ImportTranItem> ret = new ArrayList<>(0);
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sqlTotal);
            stmt.setString(1, importDate);
            rs = stmt.executeQuery();
            while (rs.next()) {
                ImportTranItem item = new ImportTranItem();
                item.setTransactionDate(rs.getDate("transaction_date"));
                item.setPostedDate(rs.getDate("posted_date"));
                item.setDescription(rs.getString("description"));
                item.setCategory(rs.getString("category"));
                item.setDebit(rs.getBigDecimal("debit"));
                item.setCredit(rs.getBigDecimal("credit"));
                item.setImportDate(rs.getDate("posted_date"));
                item.setImportAmount(rs.getBigDecimal("import_amount"));
                item.setMemo(rs.getString("memo"));
                ret.add(item);

            }

        } catch (SQLException ex) {
            Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            closeConn(conn, rs, stmt);
        }
        return ret;
    }
    
    @Override
    public void updateTransactions(String importDate) {
        Date statementDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try {
            statementDate = sdf.parse(importDate);
        } catch (ParseException ex) {
            Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        final String sqlInsert = "{call IMPORT_TRANSACTION_PK.import(?)} ";
        Connection conn = super.getConnection();
        OracleCallableStatement stmt = null;

        try {
            stmt = (OracleCallableStatement) conn.prepareCall(sqlInsert);
            stmt.setTimestamp(1, new Timestamp(statementDate.getTime()));
            stmt.executeUpdate();
            
            conn.commit();

        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(ImportTransServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConn(conn, null, stmt);
        }

    }

}


