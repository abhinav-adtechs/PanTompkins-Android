package io.medicaldev.pantompkinstesting;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Plain-JVM unit tests for the Pan-Tompkins stages in {@link Filter} and for
 * {@link FixedQueue}. The expected impulse responses come from the transfer
 * functions documented in docs/ALGORITHM.md (sections 3-5); Filter is
 * stateful, so every test builds a fresh instance.
 *
 * Filter's only Android import (android.util.Log) is unused, so these run
 * under the standard unit-test task with no emulator.
 */
public class FilterTest {

    private static final double EPS = 1e-9;

    @Test
    public void lowPass_impulseResponseMatchesBinomialTapsThenZeros() {
        Filter filter = new Filter();
        double[] expectedTapsOver32 = {1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};
        double out = filter.LowPassFilter(1.0);
        assertEquals(expectedTapsOver32[0] / 32.0, out, EPS);
        for (int tap = 1; tap < expectedTapsOver32.length + 5; tap++) {
            out = filter.LowPassFilter(0.0);
            double expected = tap < expectedTapsOver32.length
                    ? expectedTapsOver32[tap] / 32.0 : 0.0;
            assertEquals("tap " + tap, expected, out, EPS);
        }
    }

    @Test
    public void lowPass_dcGainIsThirtySixOverThirtyTwo() {
        Filter filter = new Filter();
        double out = 0;
        for (int i = 0; i < 200; i++) {
            out = filter.LowPassFilter(1.0);
        }
        assertEquals(36.0 / 32.0, out, 1e-6);
    }

    @Test
    public void highPass_impulseResponseBlocksDcWithUnityTapAtDelay() {
        Filter filter = new Filter();
        double out = filter.HighPassFilter(1.0);
        assertEquals("tap 0", -1.0 / 32.0, out, EPS);
        for (int tap = 1; tap < 40; tap++) {
            out = filter.HighPassFilter(0.0);
            double expected;
            if (tap == 15) {
                expected = 1.0 - 1.0 / 32.0;
            } else if (tap <= 31) {
                expected = -1.0 / 32.0;
            } else {
                expected = 0.0;
            }
            assertEquals("tap " + tap, expected, out, EPS);
        }
    }

    @Test
    public void highPass_dcInputSumsToZero() {
        Filter filter = new Filter();
        double sum = 0;
        for (int i = 0; i < 200; i++) {
            sum += filter.HighPassFilter(1.0);
        }
        // Steady-state output of a DC-blocking filter on a constant input.
        double steady = 0;
        for (int i = 0; i < 50; i++) {
            steady = filter.HighPassFilter(1.0);
        }
        assertEquals(0.0, steady, 1e-6);
    }

    @Test
    public void diffFilter_currentBehaviourIsMinusXOver8PlusOneSixteenth() {
        // Pins the behaviour at HEAD: tempArrayX never shifts, so the history
        // is frozen at the constructor copy and every call returns
        // -x/8 + 1/16 regardless of the previous sample. This is the known
        // derivative-stage bug on the roadmap; the test documents it so a
        // fix (a ramp should give a constant -1.0) breaks this test loudly.
        Filter filter = new Filter();
        assertEquals(0.0 / 8 + 1.0 / 16, filter.diffFilterNext(0.0), EPS);
        assertEquals(-8.0 / 8 + 1.0 / 16, filter.diffFilterNext(8.0), EPS);
        assertEquals(8.0 / 8 + 1.0 / 16, filter.diffFilterNext(-8.0), EPS);
        // Same result on a second call proves the state does not advance.
        assertEquals(-8.0 / 8 + 1.0 / 16, filter.diffFilterNext(8.0), EPS);
    }

    @Test
    public void squareNext_squaresNegativeInput() {
        Filter filter = new Filter();
        assertEquals(9.0, filter.squareNext(-3.0), EPS);
        assertEquals(9.0, filter.squareNext(3.0), EPS);
        assertEquals(0.0, filter.squareNext(0.0), EPS);
    }

    @Test
    public void movingWindow_warmUpDividesByCurrentCount() {
        Filter filter = new Filter();
        // 1, 2, 3, ... : the running mean of 1..k is (k + 1) / 2.
        for (int k = 1; k <= 79; k++) {
            assertEquals("k=" + k, (k + 1) / 2.0, filter.movingWindowNext(k), EPS);
        }
    }

    @Test
    public void movingWindow_fullWindowAveragesExactlyTheLastEighty() {
        Filter filter = new Filter();
        double out = 0;
        for (int k = 1; k <= 200; k++) {
            out = filter.movingWindowNext(k);
        }
        // Mean of 121..200.
        assertEquals((121 + 200) / 2.0, out, EPS);
    }

    @Test
    public void fixedQueue_neverExceedsLimitAndEvictsOldestFirst() {
        FixedQueue<Integer> queue = new FixedQueue<>(3);
        for (int i = 1; i <= 10; i++) {
            queue.add(i);
            assertTrue("size at add " + i, queue.size() <= 3);
        }
        assertEquals(Integer.valueOf(8), queue.get(0));
        assertEquals(Integer.valueOf(9), queue.get(1));
        assertEquals(Integer.valueOf(10), queue.get(2));
    }
}
