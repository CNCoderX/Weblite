package com.topevery.hybird;

import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.Memory;

import java.lang.ref.WeakReference;

/**
 * @author wujie
 */
public class JsFunctionFactory implements Memory.JsObjectFactory {
    private HybirdActivity mHybirdActivity;

    public JsFunctionFactory(HybirdActivity hybirdActivity) {
        mHybirdActivity = hybirdActivity;
    }

    @Override
    public JsFunction create() {
        return new JsFunctionImpl(mHybirdActivity);
    }

    static class JsFunctionImpl extends JsFunction {
        final WeakReference<HybirdActivity> mHybirdActivity;

        public JsFunctionImpl(HybirdActivity hybirdActivity) {
            mHybirdActivity = new WeakReference<>(hybirdActivity);
        }

        @Override
        public void invoke() {
            HybirdActivity activity = mHybirdActivity.get();
            if (activity != null) {
                activity.evaluateJavascript("invokeCallback(" + this.pointer + ")");
            }
        }

        @Override
        public void invoke(Object arg0) {
            HybirdActivity activity = mHybirdActivity.get();
            if (activity != null) {
                activity.evaluateJavascript("invokeCallback(" + this.pointer + "," + $(arg0) + ")");
            }
        }

        @Override
        public void invoke(Object arg0, Object arg1) {
            HybirdActivity activity = mHybirdActivity.get();
            if (activity != null) {
                activity.evaluateJavascript("invokeCallback(" + this.pointer + "," + $(arg0) + "," + $(arg1) + ")");
            }
        }

        @Override
        public void invoke(Object arg0, Object arg1, Object arg2) {
            HybirdActivity activity = mHybirdActivity.get();
            if (activity != null) {
                activity.evaluateJavascript("invokeCallback(" + this.pointer + "," + $(arg0) + "," + $(arg1) + "," + $(arg2) + ")");
            }
        }

        @Override
        public void invoke(Object arg0, Object arg1, Object arg2, Object arg3) {
            HybirdActivity activity = mHybirdActivity.get();
            if (activity != null) {
                activity.evaluateJavascript("invokeCallback(" + this.pointer + "," + $(arg0) + "," + $(arg1) + "," + $(arg2) + "," + $(arg3) + ")");
            }
        }

        @Override
        public void release() {
            HybirdActivity activity = mHybirdActivity.get();
            if (activity != null) {
                activity.evaluateJavascript("removeCallback(" + this.pointer + ")");
            }
        }

        private Object $(Object arg0) {
            return (arg0 instanceof CharSequence) ? "'" + arg0 + "'" : arg0;
        }
    }
}
