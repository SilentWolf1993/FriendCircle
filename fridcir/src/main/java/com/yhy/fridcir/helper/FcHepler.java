package com.yhy.fridcir.helper;

import android.app.Application;

/**
 * Created by HongYi Yan on 2017/4/28 11:17.
 */
public class FcHepler {
    private static volatile FcHepler instance;

    private Application application;

    private FcHepler() {
        if (null != instance) {
            throw new RuntimeException("Can not create multiple instance for singleton class FcHelper.");
        }
    }

    public static FcHepler getInstance() {
        if (null == instance) {
            synchronized (FcHepler.class) {
                if (null == instance) {
                    instance = new FcHepler();
                }
            }
        }
        return instance;
    }

    public void init(Application application) {
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }
}
