/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 *
 * @author mpfleeger
 */
public class CsvWriter {

    private static final char DEF_SEPARATOR = ',';


    public static void writeLine(Writer w, List<String> values) throws IOException {

        boolean first = true;

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(DEF_SEPARATOR);
            }
            sb.append(value);

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());

    }

}
