package com.eagletsoft.yotaassistant.ui;

import android.app.Activity;
import android.os.Bundle;

import com.eagletsoft.yotaassistant.assistant.ShortcutHandler;

public class MirrorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ShortcutHandler.toggleMirror(this);
            this.finish();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
