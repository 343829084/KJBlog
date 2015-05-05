package org.kymjs.blog;

import org.kymjs.kjframe.bitmap.BitmapConfig;
import org.kymjs.kjframe.http.HttpConfig;

import android.app.Application;

/**
 * 
 * @author kymjs (https://github.com/kymjs)
 * @since 2015-3
 */
public class AppContext extends Application {
    public static int screenW;
    public static int screenH;

    @Override
    public void onCreate() {
        super.onCreate();
        BitmapConfig.CACHEPATH = AppConfig.imgCachePath;
        HttpConfig.CACHEPATH = AppConfig.httpCachePath;
        CrashHandler.create(this);
    }
}
