package com.topevery.hybird;

import org.xwalk.core.XWalkSettings;

/**
 * @author wujie
 */
public class WebSettings {
    public static final int LOAD_DEFAULT = XWalkSettings.LOAD_DEFAULT;
    public static final int LOAD_CACHE_ELSE_NETWORK = XWalkSettings.LOAD_CACHE_ELSE_NETWORK;
    public static final int LOAD_NO_CACHE = XWalkSettings.LOAD_NO_CACHE;
    public static final int LOAD_CACHE_ONLY = XWalkSettings.LOAD_CACHE_ONLY;

    public static final int LAYOUT_ALGORITHM_NORMAL = 1;
    public static final int LAYOUT_ALGORITHM_SINGLE_COLUMN = 2;
    public static final int LAYOUT_ALGORITHM_NARROW_COLUMNS = 3;
    public static final int LAYOUT_ALGORITHM_TEXT_AUTOSIZING = 4;

    private final XWalkSettings xWalkSettings;

    WebSettings(XWalkSettings xWalkSettings) {
        this.xWalkSettings = xWalkSettings;
    }

    public void setCacheMode(int mode) {
        xWalkSettings.setCacheMode(mode);
    }

    public int getCacheMode() {
        return xWalkSettings.getCacheMode();
    }

    public void setBlockNetworkLoads(boolean flag) {
        xWalkSettings.setBlockNetworkLoads(flag);
    }

    public boolean getBlockNetworkLoads() {
        return xWalkSettings.getBlockNetworkLoads();
    }

    public void setAllowFileAccess(boolean allow) {
        xWalkSettings.setAllowFileAccess(allow);
    }

    public boolean getAllowFileAccess() {
        return xWalkSettings.getAllowFileAccess();
    }

    public void setAllowContentAccess(boolean allow) {
        xWalkSettings.setAllowContentAccess(allow);
    }

    public boolean getAllowContentAccess() {
        return xWalkSettings.getAllowContentAccess();
    }

    public void setJavaScriptEnabled(boolean flag) {
        xWalkSettings.setJavaScriptEnabled(flag);
    }

    public void setAllowUniversalAccessFromFileURLs(boolean flag) {
        xWalkSettings.setAllowUniversalAccessFromFileURLs(flag);
    }

    public void setAllowFileAccessFromFileURLs(boolean flag) {
        xWalkSettings.setAllowFileAccessFromFileURLs(flag);
    }

    public void setLoadsImagesAutomatically(boolean flag) {
        xWalkSettings.setLoadsImagesAutomatically(flag);
    }

    public boolean getLoadsImagesAutomatically() {
        return xWalkSettings.getLoadsImagesAutomatically();
    }

    public void setBlockNetworkImage(boolean flag) {
        xWalkSettings.setBlockNetworkImage(flag);
    }

    public boolean getBlockNetworkImage() {
        return xWalkSettings.getBlockNetworkImage();
    }

    public boolean getJavaScriptEnabled() {
        return xWalkSettings.getJavaScriptEnabled();
    }

    public boolean getAllowUniversalAccessFromFileURLs() {
        return xWalkSettings.getAllowUniversalAccessFromFileURLs();
    }

    public boolean getAllowFileAccessFromFileURLs() {
        return xWalkSettings.getAllowFileAccessFromFileURLs();
    }

    public void setJavaScriptCanOpenWindowsAutomatically(boolean flag) {
        xWalkSettings.setJavaScriptCanOpenWindowsAutomatically(flag);
    }

    public boolean getJavaScriptCanOpenWindowsAutomatically() {
        return xWalkSettings.getJavaScriptCanOpenWindowsAutomatically();
    }

    public void setSupportMultipleWindows(boolean support) {
        xWalkSettings.setSupportMultipleWindows(support);
    }

    public boolean supportMultipleWindows() {
        return xWalkSettings.supportMultipleWindows();
    }

    public void setUseWideViewPort(boolean use) {
        xWalkSettings.setUseWideViewPort(use);
    }

    public boolean getUseWideViewPort() {
        return xWalkSettings.getUseWideViewPort();
    }

    public void setDomStorageEnabled(boolean flag) {
        xWalkSettings.setDomStorageEnabled(flag);
    }

    public boolean getDomStorageEnabled() {
        return xWalkSettings.getDomStorageEnabled();
    }

    public void setDatabaseEnabled(boolean flag) {
        xWalkSettings.setDatabaseEnabled(flag);
    }

    public boolean getDatabaseEnabled() {
        return xWalkSettings.getDatabaseEnabled();
    }

    public void setMediaPlaybackRequiresUserGesture(boolean require) {
        xWalkSettings.setMediaPlaybackRequiresUserGesture(require);
    }

    public boolean getMediaPlaybackRequiresUserGesture() {
        return xWalkSettings.getMediaPlaybackRequiresUserGesture();
    }

    public void setUserAgentString(String userAgent) {
        xWalkSettings.setUserAgentString(userAgent);
    }

    public String getUserAgentString() {
        return xWalkSettings.getUserAgentString();
    }

    public void setAcceptLanguages(String acceptLanguages) {
        xWalkSettings.setAcceptLanguages(acceptLanguages);
    }

    public String getAcceptLanguages() {
        return xWalkSettings.getAcceptLanguages();
    }

    public void setSaveFormData(boolean enable) {
        xWalkSettings.setSaveFormData(enable);
    }

    public boolean getSaveFormData() {
        return xWalkSettings.getSaveFormData();
    }

    public void setInitialPageScale(float scaleInPercent) {
        xWalkSettings.setInitialPageScale(scaleInPercent);
    }

    public void setTextZoom(int textZoom) {
        xWalkSettings.setTextZoom(textZoom);
    }

    public int getTextZoom() {
        return xWalkSettings.getTextZoom();
    }

    public void setDefaultFontSize(int size) {
        xWalkSettings.setDefaultFontSize(size);
    }

    public int getDefaultFontSize() {
        return xWalkSettings.getDefaultFontSize();
    }

    public void setDefaultFixedFontSize(int size) {
        xWalkSettings.setDefaultFixedFontSize(size);
    }

    public int getDefaultFixedFontSize() {
        return xWalkSettings.getDefaultFixedFontSize();
    }

    public void setSupportZoom(boolean support) {
        xWalkSettings.setSupportZoom(support);
    }

    public boolean supportZoom() {
        return xWalkSettings.supportZoom();
    }

    public void setBuiltInZoomControls(boolean enabled) {
        xWalkSettings.setBuiltInZoomControls(enabled);
    }

    public boolean getBuiltInZoomControls() {
        return xWalkSettings.getBuiltInZoomControls();
    }

    public boolean supportsMultiTouchZoomForTest() {
        return xWalkSettings.supportsMultiTouchZoomForTest();
    }

    public void setSupportSpatialNavigation(boolean enable) {
        xWalkSettings.setSupportSpatialNavigation(enable);
    }

    public boolean getSupportSpatialNavigation() {
        return xWalkSettings.getSupportSpatialNavigation();
    }

    public void setSupportQuirksMode(boolean enable) {
        xWalkSettings.setSupportQuirksMode(enable);
    }

    public boolean getSupportQuirksMode() {
        return xWalkSettings.getSupportQuirksMode();
    }

    public void setLayoutAlgorithm(int la) {
        switch (la) {
            case LAYOUT_ALGORITHM_NORMAL:
                xWalkSettings.setLayoutAlgorithm(XWalkSettings.LayoutAlgorithm.NORMAL);
                break;
            case LAYOUT_ALGORITHM_SINGLE_COLUMN:
                xWalkSettings.setLayoutAlgorithm(XWalkSettings.LayoutAlgorithm.SINGLE_COLUMN);
                break;
            case LAYOUT_ALGORITHM_NARROW_COLUMNS:
                xWalkSettings.setLayoutAlgorithm(XWalkSettings.LayoutAlgorithm.NARROW_COLUMNS);
                break;
            case LAYOUT_ALGORITHM_TEXT_AUTOSIZING:
                xWalkSettings.setLayoutAlgorithm(XWalkSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                break;
        }
    }

    public int getLayoutAlgorithm() {
        XWalkSettings.LayoutAlgorithm la = xWalkSettings.getLayoutAlgorithm();
        int result = 0;
        switch (la) {
            case NORMAL:
                result = LAYOUT_ALGORITHM_NORMAL;
                break;
            case SINGLE_COLUMN:
                result = LAYOUT_ALGORITHM_SINGLE_COLUMN;
                break;
            case NARROW_COLUMNS:
                result = LAYOUT_ALGORITHM_NARROW_COLUMNS;
                break;
            case TEXT_AUTOSIZING:
                result = LAYOUT_ALGORITHM_TEXT_AUTOSIZING;
                break;
        }
        return result;
    }

    public void setLoadWithOverviewMode(boolean overview) {
        xWalkSettings.setLoadWithOverviewMode(overview);
    }

    public boolean getLoadWithOverviewMode() {
        return xWalkSettings.getLoadWithOverviewMode();
    }
}
