/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.setup;

import com.shiba.inu.base.BaseServiceImpl;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mpfleeger
 */
public class CategoryServiceImpl extends BaseServiceImpl implements CategoryService{

    @Override
    public List<Category> getCategoryList() {
        return this.fetchCategoryList();
    }
    
    private List<Category> fetchCategoryList() {
        List<Category> list = new ArrayList(0);
        Connection conn = super.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        final String sql = "select category_id, description from expense_category";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Category ti = new Category();
                ti.setCategoryId(rs.getString("category_id"));
                ti.setDescription(rs.getString("description"));

                list.add(ti);
            }

        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConn(conn, rs, stmt);
        }
        
        return list;

    }
    
}
