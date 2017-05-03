package com.yhy.fridcir.helper;

import android.app.Application;

/**
 * Created by HongYi Yan on 2017/4/28 11:17.
 */
public class FcHelper {
    private static volatile FcHelper instance;

    private Application application;

    private FcHelper() {
        if (null != instance) {
            throw new RuntimeException("Can not create multiple instance for singleton class FcHelper.");
        }
    }

    public static FcHelper getInstance() {
        if (null == instance) {
            synchronized (FcHelper.class) {
                if (null == instance) {
                    instance = new FcHelper();
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
