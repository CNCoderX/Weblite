package com.topevery.hybird.utils;

import android.annotation.SuppressLint;
import android.os.SystemClock;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author wujie
 */
public class Utils {

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatDateTime(Date date) {
        synchronized (dateFormat) {
            return dateFormat.format(date);
        }
    }

    public static Date parseDateTime(String dateTime) {
        synchronized (dateFormat) {
            try {
                return dateFormat.parse(dateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
        }
    }

    public static String encodeURI(String source) {
        String encoded;
        try {
            encoded = URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            encoded = URLEncoder.encode(source);
        }
        return encoded.replaceAll("\\+",  "%20");
    }

    public static String decodeURI(String encoded) {
        String source;
        try {
            source = URLDecoder.decode(encoded, "utf-8");
        } catch (UnsupportedEncodingException e) {
            source = URLDecoder.decode(encoded);
        }
        return source;
    }

    private static final String FILENAME_SEQUENCE_SEPARATOR = "-";
    private static Random sRandom = new Random(SystemClock.uptimeMillis());

    public static File uniqueFileName(File source) {
        if (!source.exists()) {
            return source;
        }
        String fullname = source.getName();
        String parent = source.getParent();
        int index = fullname.lastIndexOf(".");
        String filename = fullname;
        String extension = "";
        if (index != -1) {
            filename = fullname.substring(0, index);
            extension = fullname.substring(index);
        }
        filename = filename + FILENAME_SEQUENCE_SEPARATOR;

        int sequence = 1;
        for (int magnitude = 1; magnitude < 1000000000; magnitude *= 10) {
            for (int iteration = 0; iteration < 9; ++iteration) {
                fullname = filename + sequence + extension;
                File f = new File(parent, fullname);
                if (!f.exists()) {
                    return f;
                }
                sequence += sRandom.nextInt(magnitude) + 1;
            }
        }
        return source;
    }
}
