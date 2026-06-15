package com.quectel.app.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

public class DisplayUtil {
    private static final String TAG = "DisplayUtil";

    /**
     * Keep font size unchanged regardless of system settings (call before layout)
     * Override Activity's attachBaseContext()
     */
    public static Context attachBaseContext(Context context, float fontScale) {
        Configuration config = context.getResources().getConfiguration();
        //QLog.i(TAG, "changeActivityFontScaleA " + config.fontScale + ", " + fontScale);
        // Correct approach
        config.fontScale = fontScale;
        return context.createConfigurationContext(config);
    }

    /**
     * Keep font size unchanged regardless of system settings (call before layout)
     * Override Activity's getResources()
     */
    public static Resources getResources(Context context, Resources resources, float fontScale) {
        Configuration config = resources.getConfiguration();
        //QLog.i(TAG, "changeActivityFontScaleR " + config.fontScale + ", " + fontScale);
        if(config.fontScale != fontScale) {
            config.fontScale = fontScale;
            return context.createConfigurationContext(config).getResources();
        } else {
            return resources;
        }
    }
    /**
     * Save font size then notify UI to rebuild; triggers attachBaseContext to change font size
     */
    public static void recreate(Activity activity) {
        // Only this line takes effect; the other two do not
        activity.recreate();
    }

}
