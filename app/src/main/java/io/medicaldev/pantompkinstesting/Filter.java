package io.cardeadev.pantompkinstesting;

import android.util.Log;


public class Filter {

    private static final String TAG = "Debugging TAG";
    private double[] currentECGReading;
    private double currentECGVal ;
    private int samplingRate = 250 ;
    private static final double[] lowPassCoeff = { 1.0000,    2.0000,     1.0000,     1.0000,     -1.4755,    0.5869} ;
    private static final double[] highPassCoeff = {  1.0000,   -2.0000,    1.0000,    1.0000,   -1.8227,    0.8372} ;
    private static final double[] diffCoeff = {-0.1250,	-0.2500,	0,	0.2500,	0.1250} ;
    private static final int windowWidth = 80 ;
    private FixedQueue<Double> movingWindowQueue = new FixedQueue<>(windowWidth) ;
    private double movingWindowSum = 0;

    private double[] tempArrayX = new double[diffCoeff.length] ;

    private double[] lowPass_temp = new double[lowPassCoeff.length] ;
    private double[] highPass_temp = new double[highPassCoeff.length] ;

    private int i,n=12;
    private double y0=0,y1=0,y2=0, x[] = new double[26];


    ///////
    private double highy0=0,highy1=0, highx[] = new double[66];
    private int Highn=32;




    public Filter(double[] ecg_vals) {
        currentECGReading = ecg_vals ;
    }

    public Filter() {
        System.arraycopy(diffCoeff, 0, tempArrayX, 1, diffCoeff.length-1);
    }



    public void setCurrentECGReading(double[] currentECGReading) {
        this.currentECGReading = currentECGReading;

    }

    public void staticFilter(){
        double tempVar ;
        for (int i = 0; i < currentECGReading.length; i++) {
            tempVar = 0 ;

            tempVar = lowPassNext(currentECGReading[i]) ;
            tempVar = highPassNext(tempVar) ;
            tempVar = diffFilterNext(tempVar) ;
            tempVar = squareNext(tempVar) ;

            //or//
            //tempVar = squareNext(diffFilterNext(highPassNext(lowPassNext(currentECGReading[i])))) ;
        }
    }

    public void movingWindowhandler(){


    }

    public void queueInit(){

    }

    public double lowPassNext(double upVal){
        double mod = 0 ;
        for (int i = 0; i < lowPassCoeff.length; i++) {
            mod = mod + upVal* lowPassCoeff[i] ;
        }
        return mod ;
    }

    public double lowPassNext(String upValString){

        double mod = 0, upVal ;
        upVal = Double.parseDouble(upValString) ;

        for (int i = 0; i < lowPassCoeff.length; i++) {
            mod = mod + upVal* lowPassCoeff[i] ;
        }
        return mod ;
    }


    double LowPassFilter(double val){

        x[n] = x[n + 13] = val;
        y0 = (y1*2) - y2 + x[n] - (x[n +6]*2) + x[n +12];
        y2 = y1;
        y1 = y0;
        y0 = y0/32;
        if(--n < 0)
            n = 12;

        return y0 ;

    }

    double HighPassFilter(double val){

        highx[Highn] = highx[Highn + 33] = val;
        highy0 = highy1 + highx[Highn] - highx[Highn + 32];
        highy1 = highy0;
        if(--Highn < 0)
            Highn = 32;

       return highx[Highn + 16] - (highy0/32);
    }

    public double highPassNext(double upVal){
        double mod = 0 ;

        for (int i = 0; i < highPassCoeff.length; i++) {
            mod = mod + upVal* highPassCoeff[i] ;
        }
        return mod ;
    }

    public double diffFilterNext(double upVal){
        tempArrayX[0] = upVal ;

        double mod = 0 ;
        for (int i = 0; i < diffCoeff.length; i++) {
            mod = mod + tempArrayX[i] * diffCoeff[i] ;
        }
        return mod ;
    }

    public double squareNext(double upVal){
        return Math.pow(upVal, 2) ;
    }



    public double movingWindowNext(double upVal){
        double mod ;
        movingWindowQueue.add(upVal) ;
        movingWindowSum = 0 ;
        for (int i = 0; i < movingWindowQueue.size(); i++) {
            movingWindowSum = movingWindowSum + movingWindowQueue.get(i) ;
        }
        mod = movingWindowSum/movingWindowQueue.size() ;
        return mod;
    }



}
