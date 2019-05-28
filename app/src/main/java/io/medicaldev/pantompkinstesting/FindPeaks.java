package io.medicaldev.pantompkinstesting;


import android.content.Context;

public class FindPeaks {

    private Context mContext ;
    private int[] peakArray ;
    private static final int windowWidth = 44 ;
    private FixedQueue<Double> peakFindingQueue = new FixedQueue<>(windowWidth) ;
    private int currentMaxima ;


    public FindPeaks(Context mContext) {
        this.mContext = mContext;
    }

    public void calculatePeak(){

    }

    public int[] getPeakArray() {
        return peakArray;
    }


}
