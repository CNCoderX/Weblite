package com.topevery.hybird.reflect;

import com.topevery.hybird.utils.Releasable;

/**
 * @author wujie
 */
public class JsObject implements Releasable {
    public final int pointer = System.identityHashCode(this);

    @Override
    public void release() {
        // release
    }
}
