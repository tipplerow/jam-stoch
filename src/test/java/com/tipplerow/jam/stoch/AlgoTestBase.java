/*
 * Copyright (C) 2021 Scott Shaffer - All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tipplerow.jam.stoch;

import com.google.common.base.Stopwatch;

import com.tipplerow.jam.math.DoubleUtil;
import com.tipplerow.jam.math.JamRandom;
import com.tipplerow.jam.stoch.decay.DecayProc;
import com.tipplerow.jam.stoch.decay.DecaySystem;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public abstract class AlgoTestBase {
    protected final JamRandom random;
    protected final DecaySystem system;

    protected AlgoTestBase() {
        this.system = createSystem();
        this.random = JamRandom.generator(20210501);
    }

    // Three fast decay processes with rates 1.0, 2.0, 3.0...
    private static final double FAST_RATE1 = 1.0;
    private static final double FAST_RATE2 = 2.0;
    private static final double FAST_RATE3 = 3.0;

    // 1000 slow decay processes with rate 0.1...
    private static final int SLOW_COUNT = 1000;
    private static final double SLOW_RATE = 0.1;

    // Fast processes start with a large population...
    private static final int FAST_POPULATION = 100000;

    // Slow processes start with a small population...
    private static final int SLOW_POPULATION = 10000;

    // Run the simulation long enough until about 10% of the initial
    // population has decayed...
    private static final int TRIAL_COUNT = 500000;

    private static DecaySystem createSystem() {
        int[] pops = new int[SLOW_COUNT + 3];
        double[] rates = new double[SLOW_COUNT + 3];

        for (int index = 0; index < SLOW_COUNT; ++index) {
            pops[index] = SLOW_POPULATION;
            rates[index] = SLOW_RATE;
        }

        pops[SLOW_COUNT] = FAST_POPULATION;
        pops[SLOW_COUNT + 1] = FAST_POPULATION;
        pops[SLOW_COUNT + 2] = FAST_POPULATION;

        rates[SLOW_COUNT] = FAST_RATE1;
        rates[SLOW_COUNT + 1] = FAST_RATE2;
        rates[SLOW_COUNT + 2] = FAST_RATE3;

        return DecaySystem.create(pops, rates);
    }

    public abstract StochAlgo createAlgorithm();

    public void runAlgorithmTest() {
        StochAlgo algorithm = createAlgorithm();
        Stopwatch stopwatch = Stopwatch.createStarted();

        for (int trialIndex = 0; trialIndex < TRIAL_COUNT; ++trialIndex)
            algorithm.advance();

        stopwatch.stop();

        StochTime eventTime = system.lastEventTime();
        assertEquals(0.359, eventTime.doubleValue(), 0.001);

        for (DecayProc decayProc : system.viewProcesses())
            assertPopulation(eventTime, decayProc, 0.01);

        System.out.println(eventTime);
        System.out.println(stopwatch);
    }

    private void assertPopulation(StochTime eventTime, DecayProc decayProc, double tolerance) {
        int actual = decayProc.getPopulation();
        int expected = decayProc.getExpectedPopulation(eventTime);
        double error = DoubleUtil.ratio(actual, expected) - 1.0;

        System.out.printf("%5d, %5d, %5d, %8.4f%n", decayProc.getProcIndex(), actual, expected, error);
        assertTrue(Math.abs(error) < tolerance);
    }
}
