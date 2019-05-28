package io.cardeadev.pantompkinstesting;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Random;


public class SampleMulti extends AppCompatActivity {

    LineChart lineChart ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        init() ;
        setChart() ;

    }

    private void init() {
        lineChart = (LineChart) findViewById(R.id.main_lineChart) ;
    }

    private void setChart() {
        lineChart.setViewPortOffsets(0,20,0,0);

        lineChart.setData(new LineData());
        lineChart.setBackgroundColor(Color.BLACK);
        lineChart.setDrawGridBackground(false);
        lineChart.animateXY(1000,1000) ;
        feedData() ;

    }

    private void feedData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                 runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry() ;
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }).start();

    }

    ArrayList<Integer> colorList = new ArrayList<>() ;


    private void addEntry() {
        LineData data = lineChart.getData() ;

        colorList.add(0, Color.RED);
        colorList.add(1, Color.BLUE);
        colorList.add(2, Color.YELLOW);

        if(data.getDataSetCount() < 3){
            for (int i = data.getDataSetCount(); i < 3; i++) {
                ILineDataSet currentSet ;
                LineDataSet dataSet = new LineDataSet(null, "Row" + i) ;
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSet.setColor(colorList.get(i));
                currentSet = dataSet ;
                data.addDataSet(currentSet);

                for (int j = 0; j < 5; j++) {
                    data.getDataSetByIndex(i).addEntry(new Entry((float)Math.random(), j )) ;
                }

            }
        }


        lineChart.notifyDataSetChanged();
    }
}
