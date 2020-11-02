package com.example.fall_detection_3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.myapplication.Coundown_timer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class HomeFragment extends Fragment implements SensorEventListener
{
    public HomeFragment()
    { }
    private static final String TAG = "MainActivity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private  Sensor sensors;
    private static boolean detect_state=true;
    private boolean tgstate;
    public static final String SHARED_PREFS= "sharedPrefs";
    public static final String TOGGLE ="toggle";
    public  static final String  SHARE="share" ;
    DatabaseReference datasaveinfo;
    ToggleButton tg,share;
    public boolean share_state;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData=true;
    TextView xa,ya,za;
    View v;
    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
          v=inflater.inflate(R.layout.fragment_home,container,false);


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.SEND_SMS)){
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.SEND_SMS}, 1);
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }


        share= v.findViewById(R.id.share_location);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Coundown_timer c = new Coundown_timer();
                String lid = c.getLocationId(getContext());
                ProfileFragment p = new ProfileFragment();
                String userid = p.getUpdateId(getContext());

                if(share.isChecked()) {
                    //share_state = false;
                    share.setBackgroundResource(R.drawable.stopsharing_button);
                        share.setTextOff("SHARING DISABLE");
                        //share_state = true;
                        saveData(getContext());
                    datasaveinfo= FirebaseDatabase.getInstance().getReference("live location");
                  //  datasaveinfo.child(userid).child(lid).removeValue();
                    datasaveinfo = datasaveinfo.child(lid);
                    datasaveinfo.removeValue();
                    /*datasaveinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //DataSnapshot appleSnapshot = null;
                            //appleSnapshot=dataSnapshot.getChildren();

                            dataSnapshot.getRef().removeValue();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());
                        }
                    });*/

                }
                else {
                    share.setBackgroundResource(R.drawable.sharing_button);
                    share.setTextOn("SHARING ENABLE");
                    //share_state=false;
                    saveData(getContext());
                }

                Intent serviceIntent = new Intent(getContext(), ExampleService.class);
                getActivity().stopService(serviceIntent);
            }
        });
        tg=(ToggleButton)v.findViewById(R.id.toggleButton);
        if(tg.isChecked())
        {
            detect_state=false;
        }
        else
            detect_state=true;
        final TextView state=(TextView)v.findViewById(R.id.state);

        final Button help=(Button)v.findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                detect_state=false;
                tg.onSaveInstanceState();
                Intent i= new Intent(getContext(), Coundown_timer.class);
                startActivity(i);

            }
        });
        state.setText("Active State");
        // toggle Button Event
        tg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(tg.isChecked())
                {
                    tg.setText("START FALL DETECTION");
                    tg.setBackgroundResource(R.drawable.start_button);
                    state.setText("Disable State");
                    // onPause();
                    stopService();
                    detect_state=false;
                    saveData(getContext());


                }
                else if(!tg.isChecked())
                {
                    saveData(getContext());
                    tg.setText("PAUSE FALL DETECTION");
                    tg.setBackgroundResource(R.drawable.pause_button);
                    state.setText("Active State");
                    startService();
                    //  onResume();
                    detect_state=true;

                }
            }
        });

        loadData();
        updatView();

        //GRAPH METHODS
        mSensorManager =(SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        if (mAccelerometer != null)
        {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        mChart = (LineChart) v.findViewById(R.id.chart1);

        // enable description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // set an alternative background color
       mChart.setBackgroundColor(Color.parseColor("#4A148C"));
     //   mChart.setBackgroundColor(R.color.colorPrimary);
        LineData data = new LineData();
        data.setValueTextColor(Color.parseColor("#4A148C"));

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.GREEN);


        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.parseColor("#4A148C"));
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(false);
        xl.setEnabled(true);
        xl.setAxisLineColor(Color.parseColor("#4A148C"));

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.parseColor("#4A148C"));
        leftAxis.setDrawGridLines(false);
        //   leftAxis.setAxisMaximum(20f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisLineColor(Color.parseColor("#4A148C"));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);
        feedMultiple();
        //---------------------------------
        return v;
    }
public Boolean getshareState(Context c)
{
    SharedPreferences sharedPreferences =c.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
    share_state=sharedPreferences.getBoolean(SHARE,false);
    return share_state;
}
    public  void saveData(Context c)
    {
        SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(TOGGLE,tg.isChecked());
        editor.putBoolean(SHARE, share.isChecked());
        editor.apply();

    }
    public  void savesharestate(Context c)
    {
        SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
         editor.putBoolean(SHARE, true);
        editor.apply();

    }
    public  void loadData()
    {
        SharedPreferences sharedPreferences =getContext().getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        tgstate=sharedPreferences.getBoolean(TOGGLE,true);
        share_state=sharedPreferences.getBoolean(SHARE,false);
    }
    public void updatView()
    {
        tg.setChecked(tgstate);
        if(tg.isChecked())
        {
            tg.setText("START FALL DETECTION");
            tg.setBackgroundResource(R.drawable.start_button);
        }
        else if(!tg.isChecked())
        {
            tg.setText("PAUSE FALL DETECTION");
            tg.setBackgroundResource(R.drawable.pause_button);


        }

        share.setChecked(share_state);
        if(share.isChecked())
        {
            share.setBackgroundResource(R.drawable.stopsharing_button);
            share.setText("SHARING ENABLE");
        }
        else if(!share.isChecked())
        {
            share.setBackgroundResource(R.drawable.sharing_button);
            share.setText("SHARING DISABLE");
        }
    }




    private void addEntry(SensorEvent event)
    {
        LineData data = mChart.getData();
        if (data!= null&tg.isChecked()==false)
        {
            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well
            if (set == null)
            {
                set = createSet();
                data.addDataSet(set);
            }
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int  z = (int) event.values[2];
            float avg=(x+y+z)/3;
            float mean = x*x+y*y+z*z;
            mean = (float) Math.sqrt(mean);
            String sx = Integer.toString(x);

            data.addEntry(new Entry(set.getEntryCount(), avg +10), 0);
            data.notifyDataChanged();
            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();
            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);
            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet()
    {
        LineDataSet set = new LineDataSet(null, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }


    private void feedMultiple()
    {
        if (thread != null)
        {
            thread.interrupt();
        }
        thread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (true)
                {
                    plotData = true;
                }
            }
        });
        thread.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        /* if (thread != null) {
             thread.interrupt();
         }*/
        mSensorManager.unregisterListener(this);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }
    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        if(plotData)
        {
            addEntry(event);
            plotData = false;
        }
    }
    @Override
    public void onResume()
    {
        //detect_state=true;
        super.onResume();
        //    thread.interrupt();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDestroy()
    {
        share_state=true;
        saveData(getContext());
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }
    public void startService() {
        Toast.makeText(getContext(),"Start Fall detection in Background",Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(getContext(), DetectionService.class);
        ContextCompat.startForegroundService(getContext(), serviceIntent);
    }
    public void stopService(){
        Intent serviceIntent = new Intent(getContext(), DetectionService.class);
        getActivity().stopService(serviceIntent);
    }

}
