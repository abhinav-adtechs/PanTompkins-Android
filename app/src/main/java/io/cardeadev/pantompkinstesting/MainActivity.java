package io.cardeadev.pantompkinstesting;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Debug tag";
    LineChart lineChart ;
    double lowVal, highVal, diffVal, sqVal, movingWindVal ;

    static int line_num = 0;

    Filter filter = new Filter() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init() ;

    }

    private void init() {

        lineChart = (LineChart) findViewById(R.id.main_lineChart) ;
        lineChart.setViewPortOffsets(0, 20, 0, 0);

        lineChart.setData(new LineData());
        lineChart.setData(new LineData());
        lineChart.setBackgroundColor(Color.BLACK);
        lineChart.setDrawGridBackground(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        YAxis yAxis = new YAxis() ;
        //lineChart.getAxisLeft().setAxisMinValue(-20);
        lineChart.getAxis(yAxis.getAxisDependency()).setDrawGridLines(false);
        //lineChart.moveViewToX(1000);

        //lineChart.animateXY(100, 100) ;


        dataInit() ;

        feedMultiple();

    }

    private void dataInit() {
        LineData data = lineChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntryA(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            // add a new x-value first
            for (int i = 0; i < 601; i++) {
                data.addXValue(String.valueOf(data.getXValCount()));
                data.addEntry(new Entry(0, set.getEntryCount()), 0);
            }
            //Log.d(TAG, "addEntryA: " + val);


            lineChart.notifyDataSetChanged();

            lineChart.setVisibleXRangeMaximum(600);
            lineChart.moveViewToX(data.getXValCount() - 601);
        }
    }

    private void feedMultiple() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                readCSV(Environment.getExternalStorageDirectory() + "/Movies/data.csv");
            }
        }).start();
    }

    public void readCSV(String csvPath) {


        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(csvPath));
            //reader.readLine();//
            String line = null;//


            while ((line = reader.readLine()) != null) {

                String item[] = line.split(",");

                lowVal = filter.LowPassFilter(Double.parseDouble(item[0])) ;
                highVal = filter.HighPassFilter(lowVal) ;

                /*addEntryA(highVal);*/
                /*highVal = filter.highPassNext(lowVal) ; */
                diffVal = filter.diffFilterNext(highVal) ;
                sqVal = filter.squareNext(diffVal) ;
                movingWindVal = filter.movingWindowNext(sqVal) ;

                //tempEntryInit() ;
                //addEntry(movingWindVal, 0);
                //addEntry(highVal, 1);
                //addEntryA(lowVal);
                addEntryA(movingWindVal, 1);
                //addCustomEntry(item[0], movingWindVal);
                   //Log.d(TAG, "readCSV: " + "Line: " + line_num + " Value:  " + item[0] + " lowVal: " + lowVal + " highVal: " + highVal + " diffVal: " + diffVal + " sqVal: " + sqVal + " movingWindowVal: " + movingWindVal);



                try {
                    Thread.sleep(10);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //System.out.println(dataAL.get(line_num));
                line_num++;
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private void tempEntryInit() {
        LineData data = lineChart.getData() ;



        if(data.getDataSetCount() < 2){
            for (int i = data.getDataSetCount(); i < 2; i++) {
                ILineDataSet currentSet ;
                LineDataSet dataSet = new LineDataSet(null, "Row" + i) ;
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSet.setColor(getResources().getColor(R.color.colorPrimaryDark));
                currentSet = dataSet ;
                data.addDataSet(currentSet);
            }
        }


    }

    private void addEntry(double val, int type){
        LineData data = lineChart.getData() ;
        ILineDataSet set = data.getDataSetByIndex(type) ;

        Log.d(TAG, "addEntry: " + val + " Type: " + type + " EntryCount:" + set.getEntryCount());


        data.addEntry(new Entry((float) val, set.getEntryCount()), type);

        lineChart.notifyDataSetChanged();

        lineChart.setVisibleXRangeMaximum(600);
        lineChart.moveViewToX(data.getXValCount() - 601);
    }





    /**
     * ORIGINAL CODE TO BE USED
     * */

    private void addEntryA(String val) {

        LineData data = lineChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntryA(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            // add a new x-value first
            data.addXValue(String.valueOf(data.getXValCount()));
            //Log.d(TAG, "addEntryA: " + val);
            data.addEntry(new Entry(Float.parseFloat(val), set.getEntryCount()), 0);


            lineChart.notifyDataSetChanged();

            lineChart.setVisibleXRangeMaximum(600);
            lineChart.moveViewToX(data.getXValCount() - 601);
        }
    }


    private void addEntryA(double val) {

        LineData data = lineChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntryA(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            // add a new x-value first
            data.addXValue(String.valueOf(data.getXValCount()));
            //Log.d(TAG, "addEntryA: " + val);
            data.addEntry(new Entry((float)val, set.getEntryCount()), 0);


            lineChart.notifyDataSetChanged();

            lineChart.setVisibleXRangeMaximum(600);
            lineChart.moveViewToX(data.getXValCount() - 601);
        }
    }

    private void addCustomEntry(String ecgVal, double movingVal) {

        LineData data = lineChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1) ;
            // set.addEntryA(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            if (set2 == null) {
                set2 = createSetB() ;
                data.addDataSet(set2);
            }

            // add a new x-value first
            data.addXValue(String.valueOf(data.getXValCount()));
            Log.d(TAG, "addEntryA: " + ecgVal + " movWindVal: " + movingWindVal*200);
            data.addEntry(new Entry(Float.parseFloat(ecgVal), set.getEntryCount()), 0);
            data.addEntry(new Entry((float) movingVal, set.getEntryCount()), 1);


            lineChart.notifyDataSetChanged();

            lineChart.setVisibleXRangeMaximum(600);
            lineChart.moveViewToX(data.getXValCount() - 601);
        }
    }


    private void addEntryA(double val, int type) {

        LineData data = lineChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(type);
            // set.addEntryA(...); // can be called as well

            if (set == null) {
                set = createSetB();
                data.addDataSet(set);
            }

            // add a new x-value first
            data.addXValue(String.valueOf(data.getXValCount()));
            Log.d(TAG, "addEntryA: " + val*200  );
            if(val*200 > 1000){
                data.addEntry(new Entry((float) val*200, set.getEntryCount()), type);
            }else {
                data.addEntry(new Entry( 0, set.getEntryCount()), type);
            }


            lineChart.notifyDataSetChanged();

            lineChart.setVisibleXRangeMaximum(600);
            lineChart.moveViewToX(data.getXValCount() - 1201);
        }
    }

    private void addEntryB(double val) {

        LineData data = lineChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(1);
            // set.addEntryA(...); // can be called as well

            if (set == null) {
                set = createSetB();
                data.addDataSet(set);
            }

            // add a new x-value first
            data.addXValue(String.valueOf(data.getXValCount()));
            data.addEntry(new Entry((float) val, set.getEntryCount()), 0);


            lineChart.notifyDataSetChanged();

            lineChart.setVisibleXRangeMaximum(600);
            lineChart.moveViewToX(data.getXValCount() - 601);
        }
    }



    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(getResources().getColor(R.color.colorPrimaryDark));
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(0.1f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    private LineDataSet createSetB(){
        LineDataSet setB = new LineDataSet(null, "Next Level Vals");
        setB.setAxisDependency(YAxis.AxisDependency.LEFT);
        setB.setColor(getResources().getColor(R.color.colorAccent));
        setB.setCircleColor(Color.BLACK);
        setB.setLineWidth(2f);
        setB.setCircleRadius(0.1f);
        setB.setFillAlpha(65);
        setB.setFillColor(ColorTemplate.getHoloBlue());
        setB.setHighLightColor(Color.rgb(244, 117, 117));
        setB.setValueTextSize(9f);
        setB.setDrawValues(false);
        return setB;
    }
}
