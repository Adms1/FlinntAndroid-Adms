package com.edu.flinnt.util;

import java.text.DecimalFormat;

/**
 * Created by Aspsine on 2015/7/29.
 */
public class DownloadUtils {

    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public static String getDownloadPerSize(long finished, long total) {
        return DF.format((float) finished / (1024 * 1024)) + "M/" + DF.format((float) total / (1024 * 1024)) + "M";
    }



}
