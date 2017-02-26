package com.zhanggb.fictionread.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.zhanggb.fictionread.util.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-27
 * Time: 下午9:22
 * To change this template use File | Settings | File Templates.
 */
public class PreferencesManager {
    private Context context;
    private SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Resource.READ, Context.MODE_WORLD_WRITEABLE);
    }

    public void setTextSize(int i) {
        sharedPreferences.edit().putInt(Resource.TEXT_SIZE, i).commit();
    }

    public int getTextSize() {
        return sharedPreferences.getInt(Resource.TEXT_SIZE, 20);
    }

    public void setTextBack(int i) {
        sharedPreferences.edit().putInt(Resource.TEXT_BACK, i).commit();
    }

    public int getTextBack() {
        return sharedPreferences.getInt(Resource.TEXT_BACK, 0);
    }
}
