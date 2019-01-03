package com.eagletsoft.yotaassistant.assistant;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.eagletsoft.yotaassistant.ui.FullscreenActivity;
import com.eagletsoft.yotaassistant.utils.DualDisplayManager;
import com.eagletsoft.yotaassistant.yota.YotaBridgeManager;
import com.eagletsoft.yotaassistant.yota.YotaDisplayManager;
import com.yotadevices.sdk.Epd;

import java.util.List;

public class ShortcutHandler {

    public static void handle(String key, Context context) {
        try {
            switch (key) {
                case "mirror":
                    toggleMirror(context);
                    break;
                case "exit_mirror":
                    toggleMirror(context);
                    break;
                case "mozhi":
                    openMozhi(context);
                    break;
                case "refresh":
                    openTest(context);
                    break;
                case "update_params":
                    openParams(context);
                    break;
                case "remove":
                    stopHelper(context);
                    break;
                default:
            }
        }
        catch (Exception ex) {
            Log.e("markhe", "error", ex);
            Toast.makeText(context, "打开失败", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    public static void toggleMirror(final Context context) {
        Toast.makeText(context, "请翻转到背屏使用", Toast.LENGTH_LONG).show();
        Handler handler = new Handler();
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        YotaBridgeManager.getInstance().toggleMirroring(context);
                    }
                }, 1000
        );
    }

    public static void stopHelper(Context context) {
        Intent intent = new Intent(context, FloatService.class);
        context.stopService(intent);
        Toast.makeText(context, "镜像助手已关闭", Toast.LENGTH_LONG).show();
    }

    public static boolean startHelper(Context context) {
        try {
            //if (!YotaBridgeManager.getInstance().isEpdInteractive() || YotaBridgeManager.getInstance().isMirroringStarted()) {
            Intent intent = new Intent(context, FloatService.class);
            context.startService(intent);
            return true;
            //}
        }
        catch (Exception ex) {
            Log.e("markhe", "Error in calling Yota", ex);
        }
        return false;
    }

    public static void openParams(Context context) {
        YotaDisplayManager.getInstance().openParams(context);
    }

    public static void openTest(Context context) {
        /*
        Intent intent = new Intent();
        intent.setClass(context, FullscreenActivity.class);
        context.startActivity(intent);
        */
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(0x64);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            Log.e("markhe", task.topActivity.toString());

        }

    }

    public static void openMozhi(Context context) {
        Intent closeRecents = new Intent();
        ComponentName recents = new ComponentName("com.android.jv.ink.launcherink", "com.android.jv.ink.launcherink.ui.home.JvMainActivity");
        closeRecents.setComponent(recents);
        context.startActivity(closeRecents);
    }
}
