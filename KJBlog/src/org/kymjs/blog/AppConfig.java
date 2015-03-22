package org.kymjs.blog;

/**
 * 配置文件常量
 * 
 * @author kymjs (https://github.com/kymjs)
 * @since 2015-3
 */
public class AppConfig {
    public static String saveFolder = "KJBlog";
    public static String httpCachePath = saveFolder + "/httpCache";
    public static String imgCachePath = saveFolder + "/imageCache";
    public static String audioPath = saveFolder + "/audio";

    public static String CACHE_TIME_KEY = "cache_time_key";

    public static String SPLASH_HEAD_IMG_KEY = "headimage_key";
    public static String SPLASH_BACKGROUND_KEY = "main_background_key";
    public static String SPLASH_BOX_KEY = "main_box_key";
    public static String SPLASH_CONTENT_KEY = "main_content_key";

    public static String PUSH_SWITCH_FILE = "push_switch_file";
    public static String PUSH_SWITCH_KEY = "push_switch_key";
}
