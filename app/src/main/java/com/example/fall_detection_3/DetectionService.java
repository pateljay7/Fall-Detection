package com.example.fall_detection_3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Coundown_timer;

import java.text.DecimalFormat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static android.content.Context.SENSOR_SERVICE;

public class DetectionService extends Service implements SensorEventListener{
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView textView1,textView2,textView3,textView4,textView5;
    private View cid;
    NotificationCompat.Builder notificationBuilder;
    Notification notification;
    NotificationManager manager;
    String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) DetectionService.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        if(accelerometer == null){
            Toast.makeText(this,"The divise has no Accelerometer!",Toast.LENGTH_LONG).show();

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
        return START_NOT_STICKY;

    }
    private ServiceConnection boundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        float xVal = event.values[0];
        float yVal = event.values[1];
        float zVal = event.values[2];

        Log.d(TAG, "onsensorChanged  " + "X : " + event.values[0] + "  Y : " + event.values[1] + "  Z : " + event.values[2]);


        double loAccelerationReader = Math.sqrt(Math.pow(xVal, 2)
                + Math.pow(yVal, 2)
                + Math.pow(zVal, 2));
        DecimalFormat precision = new DecimalFormat("0.00");
        double ldAccRound = Double.parseDouble(precision.format(loAccelerationReader));
        updateNotification(ldAccRound);
        if (ldAccRound > 0.3d && ldAccRound<0.5d ) {
Toast.makeText(DetectionService.this,"Fall Detected",Toast.LENGTH_SHORT).show();
            Intent i =new Intent(getApplicationContext(), Coundown_timer.class);
            i.addFlags(i.FLAG_ACTIVITY_NEW_TASK|i.FLAG_ACTIVITY_REORDER_TO_FRONT);
            getApplicationContext().startActivity(i);

        }
    }
    private void startMyOwnForeground() {

        // Toast.makeText(getApplicationContext(), "start sharing", Toast.LENGTH_LONG);
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "background fall detection";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
         manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.logo_icon)
                .setContentTitle("background fall detecting")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(2, notification);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        Toast.makeText(DetectionService.this, "Stop Detecting", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void updateNotification(double data)
    {
        //Toast.makeText(DetectionService.this,String.valueOf(data),Toast.LENGTH_SHORT).show();
        //notificationBuilder.setContentText(String.valueOf(data));
     //   manager.notify(2, notificationBuilder.build());
    }
}