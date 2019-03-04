/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.imp;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author fmatsuura
 */
public class ImportTranItem {

    private Date transactionDate = null;
    private Date postedDate = null;
    private String description = null;
    private String category = null;
    private BigDecimal credit = null;
    private BigDecimal debit = null;
    private BigDecimal importAmount = null;
    private String memo = null;
    private Long importSeqId = null;
    private Date importDate = null;
    private String markInd = null;

    public String getMarkInd() {
        return markInd;
    }

    public void setMarkInd(String markInd) {
        this.markInd = markInd;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public Long getImportSeqId() {
        return importSeqId;
    }

    public void setImportSeqId(Long importSeqId) {
        this.importSeqId = importSeqId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public void setDebit(BigDecimal debit) {
        this.debit = debit;
    }

    public BigDecimal getImportAmount() {
        return importAmount;
    }

    public void setImportAmount(BigDecimal importAmount) {
        this.importAmount = importAmount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
