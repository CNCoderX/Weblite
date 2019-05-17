package com.topevery.hybird.plugin.net;

/**
 * @author wujie
 */
public class Method {
    public static final String GET = "GET";
    public static final String HEAD = "HEAD";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String PATCH = "PATCH";

    public static boolean isGet(String method) {
        return method != null && method.equals(GET);
    }

    public static boolean isHead(String method) {
        return method != null && method.equals(HEAD);
    }

    public static boolean isPost(String method) {
        return method != null && method.equals(POST);
    }

    public static boolean isPut(String method) {
        return method != null && method.equals(PUT);
    }

    public static boolean isDelete(String method) {
        return method != null && method.equals(DELETE);
    }

    public static boolean isPatch(String method) {
        return method != null && method.equals(PATCH);
    }
}
