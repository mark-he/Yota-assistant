package com.eagletsoft.yotaassistant.assistant;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FloatService extends Service {
    private AssistantHelper assistant = new AssistantHelper();
    public FloatService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        assistant.create(this.getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        assistant.destroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY_COMPATIBILITY;
        return super.onStartCommand(intent, flags, startId);
    }
}


