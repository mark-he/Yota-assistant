package com.eagletsoft.yotaassistant.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.eagletsoft.yotaassistant.assistant.AssistantMenuLauncher;
import com.eagletsoft.yotaassistant.assistant.ShortcutHandler;


public class AssistantActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            boolean ret = ShortcutHandler.startHelper(this.getApplicationContext());
            if (ret) {
                AssistantMenuLauncher.getInstance().show(this.getApplicationContext(), 0, 0);
            }
            else {
                Toast.makeText(this.getApplicationContext(), "只能在镜像模式中启用", Toast.LENGTH_SHORT).show();
            }
            this.finish();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
