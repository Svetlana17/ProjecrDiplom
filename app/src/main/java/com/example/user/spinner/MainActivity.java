package com.example.user.spinner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends AppCompatActivity
        implements SensorEventListener {
    private SensorManager mSensorManager;
    Sensor sensorAccelerometr;
    GraphView graph;
    private double graph2LastXValue = 5d;
    private double graph2LastYValue = 5d;
    private double graph2LastZValue = 5d;
    private Double[] dataPoints;
    private Thread thread;
    private boolean plotData = true;
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> seriesY;
    LineGraphSeries<DataPoint> seriesZ;
    private boolean state;
    Spinner spinner;
    String[] data = {"Ускорение по оси х", "Ускорение по оси y", "Ускорение по оси z"};
    private int timer = 0;

    ///* Called when the activity is first created. /
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        state = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometr = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        graph = (GraphView) findViewById(R.id.graph);


        series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 0),
        });
        series.setColor(Color.GREEN);
        graph.addSeries(series);
//
//
        seriesY = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 0),
        });
        seriesY.setColor(Color.RED);
        graph.addSeries(seriesY);
//
        seriesZ = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 0),
        });
        seriesZ.setColor(Color.BLACK);
        graph.addSeries(seriesZ);
//
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(20);
//        feedMultiple();
//

//// адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
// заголовок
        spinner.setPrompt("Title");
// выделяем элемент
        spinner.setSelection(2);
// устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
// показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Ускорение по" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
//
//
   }
private void addDataPoint(double acceleration) {
    dataPoints[499] = acceleration;
}
//
        private void feedMultiple () {
            if (thread != null) {
                thread.interrupt();
            }

            thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        plotData = true;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                ///додобавила отсюда

//            public void run() {
//                while (true) {
//                    plotData = true;
//                try {
//                    while (!Thread.interrupted()) {
//                        Thread.sleep(10);
//                        System.out.println("Producer");
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }


            });
            thread.start();
        }
//
        public void addEntry (SensorEvent event){
            /*     LineGraphSeries<DataPoint> series = new LineGraphSeries<>();*/
            float[] values = event.values;
            // Movement
            float x = values[0];
            System.out.println(x);
            float y = values[1];
            System.out.println(y);
            float z = values[2];
            System.out.println(z);

            if (state) {
                timer++;
                if (timer % 5 == 0) {
                    System.out.println(timer);
                    // saveText(event);
                }
            }
            graph2LastXValue += 1d;
        graph2LastYValue += 1d;
        graph2LastZValue += 1d;
            series.appendData(new DataPoint(graph2LastXValue, x), true, 20);
        seriesY.appendData(new DataPoint(graph2LastYValue, y), true, 20);
        seriesZ.appendData(new DataPoint(graph2LastZValue, z), true, 20);
//
            graph.addSeries(series);
        graph.addSeries(seriesY);
        graph.addSeries(seriesZ);
        }
//
        @Override
        protected void onPause () {
            super.onPause();

            if (thread != null) {
                thread.interrupt();
            }
            mSensorManager.unregisterListener(this);

        }

        @Override
        public void onSensorChanged ( final SensorEvent event){
            if (plotData) {
                addEntry(event);
                //
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                addEntry(event);
                            }
                        });


                    }

                }).start();

                //
                plotData = false;
            }
        }

        @Override
        public void onAccuracyChanged (Sensor sensor,int accuracy){

        }
        @Override
        protected void onResume () {
            super.onResume();
            mSensorManager.registerListener(this, sensorAccelerometr, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        protected void onDestroy () {
            mSensorManager.unregisterListener(MainActivity.this);
            thread.interrupt();
            super.onDestroy();
        }
//
    }
