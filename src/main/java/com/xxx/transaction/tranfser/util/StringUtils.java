package com.xxx.transaction.tranfser.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by ricdong on 15-8-31.
 */
public class StringUtils {

    /**
     * Make a string representation of the exception
     * @param e
     * @return
     */
    public static String stringifyException(Throwable e) {
        StringWriter stm = new StringWriter();
        PrintWriter wrt = new PrintWriter(stm);
        e.printStackTrace(wrt);
        wrt.close();
        return stm.toString();
    }
}
