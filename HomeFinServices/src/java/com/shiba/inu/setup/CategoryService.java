/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.setup;

import com.shiba.inu.base.BaseService;
import java.util.List;

/**
 *
 * @author mpfleeger
 */
public interface CategoryService extends BaseService{
    
    List<Category> getCategoryList();
    
}
