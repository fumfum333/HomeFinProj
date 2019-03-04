/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.tran;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author mpfleeger
 */
public interface TransactionService {
    List<TransactionItem> getTransactions();
    List<TransactionItem> getTransactionByMonth(Integer year, Integer month);
    List<TransactionSum> getTransactionSumByYearCat(Integer year, String categoryId);
    TransactionItem createTransactionItem(TransactionItem item);
    List<TransactionSum> fetchTranSummaryByYear(Integer year);
    
    BigDecimal getTotalYear(Integer year);
    
    List<TransactionSum> fetchSumByCategory(String categoryId, Integer year);
}
