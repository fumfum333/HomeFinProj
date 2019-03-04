/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.imp;

import com.shiba.inu.base.BaseService;
import java.util.List;

/**
 *
 * @author mpfleeger
 */
public interface ImportTransService extends BaseService{
    
    int importFileRecords(String filePath);
    List<ImportTranItem> getAllPendingTransactions(String importDate);
    List<ImportTranItem> getMarkIndImportItems(String importDate);
    List<String> getListOfImportedDates();
    
    String updateImportTransaction(String markInd, Long importSeqId);
    
}
