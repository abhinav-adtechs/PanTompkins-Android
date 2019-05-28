package io.cardeadev.pantompkinstesting;

/**
 * HeartyFilter.java
 * Copyright (C) 2012 Pattern Recognition Lab, University Erlangen-Nuremberg.
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License (the "License");
 * you may not use this file except in compliance with the License.
 * To view a copy of this License, visit http://creativecommons.org/licenses/by-nc-sa/3.0/.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 *
 * This file is part of the "Hearty" Android Application. It was released as supplementary material related to the publication [1]:
 * [1] S. Gradl, P. Kugler, C. Lohm¸ller, and B. Eskofier, ìReal-time ECG monitoring and arrhythmia detection using Android-based mobile devices,î in 34th Annual International Conference of the IEEE EMBS, 2012, pp. 2452ñ2455.
 *
 * It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * If you reuse this code you have to keep or cite this comment.
 *
 */

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a filtering algorithm using numerator and denominator
 * coefficients.
 *
 * @author Stefan Gradl
 */
public class HeartyFilter {
    protected double a[] = null;
    protected double b[] = null;
    public double y[] = null;
    public double x[] = null;

    protected HeartyFilter() {
    }

    /**
     * @param b_taps
     *            numerator coefficients
     * @param a_taps
     *            denominator coefficients, can be null. if not null, a[0] must
     *            not be 0 or an {@link InvalidParameterException} will be
     *            thrown.
     */
    public HeartyFilter(double[] b_taps, double[] a_taps) {
        // make sure the coefficients are valid
        if (b_taps == null || b_taps.length < 1
                || (b_taps.length == 1 && b_taps[0] == 0)
                || (a_taps != null && a_taps[0] == 0)) {
            throw new InvalidParameterException();
        }

        // copy denominators
        if (a_taps == null) {
            a = new double[1];
            a[0] = 1d;
        } else {
            a = new double[a_taps.length];
            System.arraycopy(a_taps, 0, a, 0, a_taps.length);
        }

        // copy numerators
        b = new double[b_taps.length];
        System.arraycopy(b_taps, 0, b, 0, b_taps.length);

        // create x & y arrays
        y = new double[a_taps.length];
        x = new double[b_taps.length];
    }

    public HeartyFilter(double b0, double b1, double b2, double b3, double b4,
                        double a0, double a1, double a2) {
        if (a0 == 0f) {
            throw new InvalidParameterException();
        }

        a = new double[3];
        a[0] = a0;
        a[1] = a1;
        a[2] = a2;

        b = new double[5];
        b[0] = b0;
        b[1] = b1;
        b[2] = b2;
        b[3] = b3;
        b[4] = b4;

        // create x & y arrays
        y = new double[a.length];
        x = new double[b.length];
    }

    private transient int t_iter = 0;

    /**
     * Performs the filtering operation for the next x value.
     *
     * @param xnow
     *            x[n]
     * @return y[n]
     */
    public double next(double xnow) {
        if (b.length > 1)
            System.arraycopy(x, 0, x, 1, b.length - 1);
        x[0] = xnow;

        // shift y
        if (a.length > 1)
            System.arraycopy(y, 0, y, 1, a.length - 1);
        y[0] = 0d;

        // sum( b[n] * x[N-n] )
        for (t_iter = 0; t_iter < b.length; ++t_iter) {
            y[0] += b[t_iter] * x[t_iter];
        }

        // sum( a[n] * y[N-n] )
        for (t_iter = 1; t_iter < a.length; ++t_iter) {
            y[0] += a[t_iter] * y[t_iter];
        }

        // a0
        if (a[0] != 1d) {
            y[0] /= a[0];
        }

        return y[0];
    }

    /**
     * @return The current y[0] value from last calculation step
     */
    public double current() {
        return y[0];
    }

    /**
     * Implements running <i>mean</i> filter.
     *
     * y[n] = 1/(N+1) * ( y[n-1] * N + x[n] )
     *
     * @author sistgrad
     *
     */
    public static class MeanFilter extends HeartyFilter {
        public int num = 0;
        public int maxNum = 0;

        public MeanFilter() {
            a = new double[2];
            a[0] = 0f;

            b = new double[2];

            // create x & y arrays
            y = new double[a.length];
            x = new double[b.length];
        }

        /**
         * Stop increasing the num counter at maxNum values.
         *
         * @param maxNum
         */
        public MeanFilter(int maxNum) {
            a = new double[2];
            a[0] = 0f;

            b = new double[2];

            // create x & y arrays
            y = new double[a.length];
            x = new double[b.length];

            this.maxNum = maxNum;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lme.plotview.HeartyFilter#next(double)
         */
        @Override
        public double next(double xnow) {
            y[1] = y[0];
            y[0] = (y[1] * num + xnow) / (num + 1);
            if (maxNum == 0 || num < maxNum)
                ++num;
            return y[0];
        }
    }

    /**
     * Represents a statistical object tp keep track of running mean, min and
     * max values.
     *
     * @author sistgrad
     *
     */
    public static class StatFilter extends HeartyFilter {
        protected MeanFilter meanFilter = null;

        public double mean = 0;
        public double min = Double.MAX_VALUE;
        public double max = Double.MIN_VALUE;
        public double range = 0;
        public double value = 0;

        public StatFilter() {
            meanFilter = new MeanFilter(16);
        }

        /**
         * Stop increasing the num counter at maxNum values.
         *
         * @param maxNum
         */
        public StatFilter(int maxNum) {
            meanFilter = new MeanFilter(maxNum);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lme.plotview.HeartyFilter#next(double)
         */
        @Override
        public double next(double xnow) {
            mean = meanFilter.next(xnow);
            value = xnow;

            if (xnow > max) {
                max = xnow;
                range = max - min;
            }

            if (xnow < min) {
                min = xnow;
                range = max - min;
            }

            return value;
        }

        public String formatValue() {
            return String.format("%.0f", value);
        }

        public String formatMean() {
            return String.format("%.0f", mean);
        }

        public String formatMin() {
            return String.format("%.0f", min);
        }

        public String formatMax() {
            return String.format("%.0f", max);
        }
    }

    /**
     * Implements the <i>von-Hann</i> filter using the last 3 values.
     *
     * y[n] = 1/4 * ( x[n] + 2 * x[n-1] + x[n-2] )
     *
     * @author sistgrad
     *
     */
    public static class HannFilter extends HeartyFilter {
        public HannFilter() {
            a = new double[1];
            a[0] = 1f;

            b = new double[3];
            b[0] = b[2] = 0.25f;
            b[1] = 0.5f;

            // create x & y arrays
            y = new double[a.length];
            x = new double[b.length];
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lme.plotview.HeartyFilter#next(double)
         */
        @Override
        public double next(double xnow) {
            // performance override
            x[2] = x[1];
            x[1] = x[0];
            x[0] = xnow;
            return (0.25f * x[0] + 0.5f * x[1] + 0.25f * x[2]);
        }
    }

    /**
     * Implements a <i>peak detection</i> filter.
     *
     * next() will always return the peak-decision for the central value of
     * minRange. If minRange == 3 then the third call to next() returns the
     * peak-decision for the previous value. The very first and second call ever
     * made to next() after creating the object will, in this exemplary case,
     * always return <code>Double.NaN</code>.
     *
     *
     * @author sistgrad
     *
     */
    public static class PeakDetectionFilter extends HeartyFilter {
        protected int minRange;
        protected double minDiff;
        public int peakIdx;
        public double peakValue = Double.NaN;
        protected int block = 1;

        /**
         * @param minRange
         *            range of surrounding values to test against for peak
         *            evaluation (must be >= 1)
         * @param minDiff
         *            minimal (absolute) difference between two values to be
         *            considered different
         */
        public PeakDetectionFilter(int minRange, double minDiff) {
            if (minRange > 0)
                this.minRange = minRange;
            else
                this.minRange = 1;

            this.minDiff = Math.abs(minDiff);

            // create x & y arrays
            y = new double[1];
            x = new double[minRange << 1 + 1];

            peakIdx = minRange;

            block = x.length;
        }

        /**
         *
         */
        public PeakDetectionFilter() {
            this.minRange = 1;
            this.minDiff = 0f;

            // create x & y arrays
            y = new double[1];
            x = new double[3];

            peakIdx = 1;
            block = 3;
        }

        /**
         * resets blocking to reuse the filter
         */
        public void reset() {
            block = x.length;
        }

        protected transient int _i;

        /**
         * @return Double.NaN, if not part of a peak, peakIdx will also be set
         *         to -1. Or the value of the peak, if it IS part of a peak.
         *         this.peakIdx will contain the current index of said peak.
         */
        @Override
        public double next(double xnow) {
            System.arraycopy(x, 0, x, 1, x.length - 1);
            x[0] = xnow;

            peakValue = Double.NaN;
            peakIdx = -1;

            // block until the buffer is filled entirely
            if (block > 0) {
                --block;
                return Double.NaN;
            }

            for (_i = 1; _i <= minRange; ++_i) {
                // values before mid-value
                if (x[minRange] - minDiff <= x[minRange + _i])
                    return Double.NaN;

                // values after mid-value
                if (x[minRange] - minDiff < x[minRange - _i])
                    return Double.NaN;
            }

            // value IS part of a peak
            peakValue = x[minRange];
            peakIdx = minRange;

            return peakValue;
        }
    }

    /**
     * Implements a <i>minimum detection</i> filter.
     *
     * next() will always return the peak-decision for the central value of
     * minRange. If minRange == 3 then the third call to next() returns the
     * peak-decision for the previous value. The very first and second call ever
     * made to next() after creating the object will, in this exemplary case,
     * always return <code>Double.NaN</code>.
     *
     *
     * @author sistgrad
     *
     */
    public static class MinDetectionFilter extends PeakDetectionFilter {

        /**
         *
         */
        public MinDetectionFilter() {
            super();
            // TODO Auto-generated constructor stub
        }

        /**
         * @param minRange
         * @param minDiff
         */
        public MinDetectionFilter(int minRange, double minDiff) {
            super(minRange, minDiff);
            // TODO Auto-generated constructor stub
        }

        @Override
        public double next(double xnow) {
            System.arraycopy(x, 0, x, 1, x.length - 1);
            x[0] = xnow;

            peakValue = Double.NaN;
            peakIdx = -1;

            // block until the buffer is filled entirely
            if (block > 0) {
                --block;
                return Double.NaN;
            }

            for (_i = 1; _i <= minRange; ++_i) {
                // values before mid-value
                if (x[minRange] - minDiff >= x[minRange + _i])
                    return Double.NaN;

                // values after mid-value
                if (x[minRange] - minDiff > x[minRange - _i])
                    return Double.NaN;
            }

            // value IS part of a peak
            peakValue = x[minRange];
            peakIdx = minRange;

            return peakValue;
        }
    }

    /**
     * Implements the <i>Savitzky-Golay</i> filter using 5 points.
     *
     * y[n] = ...
     *
     * @author sistgrad
     *
     */
    public static class SavGolayFilter extends HeartyFilter {
        /**
         * @param sg_order
         *            The Savitzky-Golay order to use.
         */
        public SavGolayFilter(int sg_order) {
            a = new double[1];
            a[0] = 1f;

            if (sg_order <= 1) {
                b = new double[5];
                b[0] = -0.0857f;
                b[1] = 0.3429f;
                b[2] = 0.4857f;
                b[3] = 0.3429f;
                b[4] = -0.0857f;
            } else if (sg_order == 2) {
                b = new double[7];
                b[0] = -0.095238f;
                b[1] = 0.1428571f;
                b[2] = 0.285714f;
                b[3] = 0.33333f;
                b[4] = 0.285714f;
                b[5] = 0.1428571f;
                b[6] = -0.095238f;
            } else
                throw new InvalidParameterException();

            // create x & y arrays
            y = new double[a.length];
            x = new double[b.length];
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lme.plotview.HeartyFilter#next(double)
         */
        @Override
        public double next(double xnow) {
            // performance override
            if (b.length == 7) {
                // TODO: check at what point System.arraycopy is worth the call!
                // Is it inlined?
                x[6] = x[5];
                x[5] = x[4];
                x[4] = x[3];
                x[3] = x[2];
                x[2] = x[1];
                x[1] = x[0];
                x[0] = xnow;
                y[0] = (b[0] * x[0] + b[1] * x[1] + b[2] * x[2] + b[3] * x[3]
                        + b[4] * x[4] + b[5] * x[5] + b[6] * x[6]);
                return y[0];
            } else {
                x[4] = x[3];
                x[3] = x[2];
                x[2] = x[1];
                x[1] = x[0];
                x[0] = xnow;
                y[0] = (b[0] * x[0] + b[1] * x[1] + b[2] * x[2] + b[3] * x[3] + b[4]
                        * x[4]);
                return y[0];
            }
        }
    }

    /**
     * Implements a <i>moving window integrator</i> filter.
     *
     * y[n] = 1/N * ( x[n] + x[n-1] + x[n-2] + ... )
     *
     * @author sistgrad
     *
     */
    public static class WndIntFilter extends HeartyFilter {
        protected List<Float> m_int = null;

        public WndIntFilter(int wndLength) {
            a = new double[1];
            a[0] = wndLength;

            b = new double[1];

            // create x & y arrays
            y = new double[a.length];
            x = new double[b.length];

            m_int = new ArrayList<Float>();
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lme.plotview.HeartyFilter#next(double)
         */
        @Override
        public double next(double xnow) {
            m_int.add((float) xnow);
            y[0] = getMean(m_int);
            return y[0];
        }

        private float getMean(List m_int) {
            float mod = 0 ;
            for (int i = 0; i < m_int.size(); i++) {
                mod = mod + Float.parseFloat(m_int.get(i).toString()) ;
            }
            return mod;
        }
    }

    /**
     * Implements a <i>moving window accumulation</i> filter.
     *
     * y[n] = ( x[n] + x[n-1] + x[n-2] + ... )
     *
     * @author sistgrad
     *
     */
    public static class AccuFilter extends WndIntFilter {
        /**
         * @param wndLength
         */
        public AccuFilter(int wndLength) {
            super(wndLength);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lme.plotview.HeartyFilter#next(double)
         */
        @Override
        public double next(double xnow) {
            m_int.add((float) xnow);

            y[0] = sumVal(m_int);

            return y[0];
        }

        private double sumVal(List<Float> m_int) {
            double sum = 0 ;
            for (int i = 0; i < m_int.size(); i++) {
                sum += m_int.get(i) ;
            }
            return sum ;
        }
    }

    /**
     * Implements the <i>first order derivative</i> filter.
     *
     * y[n] = 1/T * ( x[n] - x[n-1] )
     *
     * @author sistgrad
     *
     */
    public static class FirstDerivativeFilter extends HeartyFilter {
        public FirstDerivativeFilter(double T) {
            a = new double[1];
            a[0] = 1 / T;

            b = new double[2];
            b[0] = 1f;
            b[1] = -1f;

            // create x & y arrays
            y = new double[a.length];
            x = new double[b.length];
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lme.plotview.HeartyFilter#next(double)
         */
        @Override
        public double next(double xnow) {
            // performance override
            x[1] = x[0];
            x[0] = xnow;

            return a[0] * (x[0] - x[1]);
        }
    }

    /**
     * Implements the <i>second order derivative</i> filter.
     *
     * y[n] = 1/T≤ * ( x[n] - 2 * x[n-1] + x[n-2] )
     *
     * @author sistgrad
     *
     */
    public static class SecondDerivativeFilter extends HeartyFilter {
        public SecondDerivativeFilter(double T) {
            a = new double[1];
            a[0] = 1 / (T * T);

            b = new double[3];
            b[0] = 1f;
            b[1] = -2f;
            b[2] = 1f;

            // create x & y arrays
            y = new double[a.length];
            x = new double[b.length];
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lme.plotview.HeartyFilter#next(double)
         */
        @Override
        public double next(double xnow) {
            // performance override
            x[2] = x[1];
            x[1] = x[0];
            x[0] = xnow;
            return a[0] * (x[0] + 2 * x[1] + x[2]);
        }
    }

    /**
     * Implements the <i>three point central difference</i> filter.
     *
     * y[n] = 1/2T * ( x[n] - x[n-2] )
     *
     * @author sistgrad
     *
     */
    public static class TpcdFilter extends HeartyFilter {
        public TpcdFilter(double T) {
            a = new double[1];
            a[0] = 1 / (2 * T);

            b = new double[3];
            b[0] = 1f;
            b[1] = 0f;
            b[2] = -1f;

            // create x & y arrays
            y = new double[a.length];
            x = new double[b.length];
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lme.plotview.HeartyFilter#next(double)
         */
        @Override
        public double next(double xnow) {
            // performance override
            x[2] = x[1];
            x[1] = x[0];
            x[0] = xnow;

            return a[0] * (x[0] - x[2]);
        }
    }

    /**
     * Implements an <i>improved derivative</i> filter.
     *
     * y[n] = 1/T * ( x[n] - x[n-1] ) + (1 - T) * y[n-1]
     *
     * @author sistgrad
     *
     */
    public static class ImpDerivativeFilter extends HeartyFilter {
        public ImpDerivativeFilter(double T, Double initValue) {
            a = new double[2];
            a[0] = 1 / T;
            a[1] = 1 - T;

            b = new double[2];
            b[0] = b[1] = 1f;

            // create x & y arrays
            y = new double[a.length];
            x = new double[b.length];

            if (initValue != null) {
                x[0] = initValue.floatValue();
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lme.plotview.HeartyFilter#next(double)
         */
        @Override
        public double next(double xnow) {
            // performance override
            x[1] = x[0];
            x[0] = xnow;

            y[1] = y[0];
            y[0] = a[0] * (x[0] - x[1]) + a[1] * y[1];

            return y[0];
        }
    }

    /**
     * Implements the <i>Butterworth</i> filter.
     *
     * y[n] = sum( b[k] * x[n-k] ) - sum( a[k] * y[n-k] )
     *
     * @author sistgrad
     *
     */
    public static class ButterworthFilter extends HeartyFilter {
        // double b[] = { 0.046582f, 0.186332f, 0.279497f, 0.186332f, 0.046583f
        // };
        // double a[] = { 1f, -0.776740f, 0.672706f, -0.180517f, 0.029763f };
    }

}

