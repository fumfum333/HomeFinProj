/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.util;

import java.sql.Types;

/**
 *
 * @author mpfleeger
 */
public class ParamOut {
    private int sqlType = Types.VARCHAR;
    private Object paramValue = null;

    public ParamOut() {
    }
    
    public ParamOut(int sqlType, Object paramValue) {
        this.sqlType = sqlType;
        this.paramValue = paramValue;
    }

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object paramValue) {
        this.paramValue = paramValue;
    }
    
    
    
}
