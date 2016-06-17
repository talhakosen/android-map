package tkosen.com.gcm;

import android.app.Application;

/**
 * Created by tctkosen on 17/06/16.
 */
public class MapApplication extends Application {
    private boolean isActivityVisible;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public boolean isActivityVisible() {
        return isActivityVisible;
    }

    public void setActivityVisible(boolean activityVisible) {
        isActivityVisible = activityVisible;
    }
}
