package com.eagletsoft.yotaassistant.assistant;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.eagletsoft.circlemenu.CircleMenu;
import com.eagletsoft.circlemenu.OnMenuSelectedListener;
import com.eagletsoft.circlemenu.OnMenuStatusChangeListener;
import com.eagletsoft.yotaassistant.utils.DualDisplayManager;
import com.eagletsoft.yotaassistant.yota.YotaBridgeManager;
import com.eagletsoft.yotaassistant.R;

public class AssistantMenuLauncher {
    private RelativeLayout layer;
    private Context context;
    private WindowManager mWindowManager;
    private CircleMenu circleMenu;

    private final static AssistantMenuLauncher INSTANCE = new AssistantMenuLauncher();

    private AssistantMenuLauncher() {

    }
    public static AssistantMenuLauncher getInstance() {
        return INSTANCE;
    }

    public void show(Context context, int x, int y) {
        this.context = context;
        createLayerIfNot();
        showLayer();
        initMenu();
        circleMenu.openMenu();
    }

    private void initMenu() {
        circleMenu.clearSubMenus();

        if (this.isEpdInteractive()) {
            circleMenu.setSkipAnimation(true);
            layer.setBackgroundColor(Color.parseColor("#000000"));
            if (this.isInMirrorMode()) {
                //退出镜像
                circleMenu.addSubMenu("exit_mirror", Color.parseColor("#FFFFFF"), R.mipmap.icon_mirror);
            }
            else {
                circleMenu.addSubMenu("mozhi", Color.parseColor("#FFFFFF"), R.mipmap.icon_mozhi);
                //多任务
                //circleMenu.addSubMenu("multi", Color.parseColor("#258CFF"), R.mipmap.icon_search);

            }
            //全局刷新
            //circleMenu.addSubMenu("refresh", Color.parseColor("#FF4B32"), R.mipmap.icon_notify);

            //自动刷新
            //circleMenu.addSubMenu("update_params", Color.parseColor("#8A39FF"), R.mipmap.icon_setting);

            //关闭悬浮
            circleMenu.addSubMenu("remove", Color.parseColor("#FFFFFF"), R.mipmap.icon_ball);
        }
        else {
            circleMenu.setSkipAnimation(false);
            layer.setBackgroundColor(Color.parseColor("#CC000000"));
            //镜像
            circleMenu.addSubMenu("mirror", Color.parseColor("#30A400"), R.mipmap.icon_mirror);
            //关闭悬浮
            circleMenu.addSubMenu("remove", Color.parseColor("#FF6A00"), R.mipmap.icon_ball);
        }
    }

    private void showLayer() {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT);

        mWindowManager = DualDisplayManager.getInstance().getActiveWindowManager(context);

        wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        mWindowManager.addView(layer, wmParams);
    }

    private RelativeLayout createLayerIfNot() {
        if (this.layer == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            layer = (RelativeLayout) inflater.inflate(R.layout.popup, null);
            layer.setFocusableInTouchMode(true);
/*

                    .addSubMenu("A",  Color.parseColor("#258CFF"), R.mipmap.icon_home)
                    .addSubMenu("A", Color.parseColor("#30A400"), R.mipmap.icon_search)
                    .addSubMenu("A", Color.parseColor("#FF4B32"), R.mipmap.icon_notify)
                    .addSubMenu("A", Color.parseColor("#8A39FF"), R.mipmap.icon_setting)
                    .addSubMenu("A", Color.parseColor("#FF6A00"), R.mipmap.icon_gps)
                    */
            circleMenu = (CircleMenu) layer.findViewById(R.id.circle_menu);

            circleMenu.setMainMenu(Color.parseColor("#CDCDCD"), R.mipmap.icon_menu, R.mipmap.icon_cancel)
                    .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                        @Override
                        public void onMenuSelected(int index) {
                            CircleMenu.SubMenu sub = circleMenu.indexAt(index);
                            ShortcutHandler.handle(sub.getKey(), context);
                        }
                    }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

                @Override
                public void onMenuOpened() {}

                @Override
                public void onMenuClosed() {
                    mWindowManager.removeView(layer);
                }

            });
            layer.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    circleMenu.closeMenu();
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            return true;
                        default:
                            break;
                    }
                    return false;
                }
            });

            layer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    circleMenu.closeMenu();
                }
            });
        }
        return layer;
    }

    private void onShow() {
    }

    private void onClose() {

    }

    private boolean isEpdInteractive() {
        try {
            return YotaBridgeManager.getInstance().isEpdInteractive();
        }
        catch (Exception ex) {
            return false;
        }
    }

    private boolean isInMirrorMode() {
        try {
            return YotaBridgeManager.getInstance().isMirroringStarted();
        }
        catch (Exception ex) {
            return false;
        }
    }

    public RelativeLayout getLayer() {
        return layer;
    }
}
