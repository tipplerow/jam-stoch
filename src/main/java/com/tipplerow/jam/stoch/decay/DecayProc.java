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
package com.tipplerow.jam.stoch.decay;

import com.tipplerow.jam.lang.JamException;
import com.tipplerow.jam.stoch.StochProc;
import com.tipplerow.jam.stoch.StochRate;
import com.tipplerow.jam.stoch.StochTime;

/**
 * Represents a first-order decay process.
 *
 * @author Scott Shaffer
 */
public final class DecayProc extends StochProc {
    private final int initPop;
    private final double rateConst;

    private int population;

    private DecayProc(int initPop, double rateConst) {
        validateInitPop(initPop);
        validateRateConst(rateConst);

        this.initPop = initPop;
        this.rateConst = rateConst;

        this.population = initPop;
    }

    private static void validateInitPop(int initPop) {
        if (initPop <= 0)
            throw JamException.runtime("Initial population must be positive.");
    }

    private static void validateRateConst(double rateConst) {
        if (rateConst <= 0.0)
            throw JamException.runtime("Decay rate constant must be positive.");
    }

    /**
     * Creates a new first-order decay process with a fixed rate
     * constant.
     *
     * @param initPop the initial population of the undecayed state.
     *
     * @param rateConst the unit rate constant for the process.
     *
     * @return a new first-order decay process with the specified
     * parameters.
     */
    public static DecayProc create(int initPop, double rateConst) {
        return new DecayProc(initPop, rateConst);
    }

    void decay() {
        if (population <= 0)
            throw JamException.runtime("Population must be non-negative.");

        --population;
    }

    /**
     * Computes the expected population of the undecayed state at a
     * particular time.
     *
     * @param time the elapsed time.
     *
     * @return the expected population of the undecayed state at the
     * specified time.
     */
    public int getExpectedPopulation(StochTime time) {
        return (int) Math.round(initPop * Math.exp(-rateConst * time.doubleValue()));
    }

    /**
     * Returns the initial population of the undecayed state.
     *
     * @return the initial population of the undecayed state.
     */
    public int getInitialPopulation() {
        return initPop;
    }

    /**
     * Returns the current population of the undecayed state.
     *
     * @return the current population of the undecayed state.
     */
    public int getPopulation() {
        return population;
    }

    /**
     * Returns the unit rate constant for this process.
     *
     * @return the unit rate constant for this process.
     */
    public double getRateConst() {
        return rateConst;
    }

    @Override public StochRate getStochRate() {
        return StochRate.of(population * rateConst);
    }

    @Override public String toString() {
        return String.format("DecayProc(%d, %d)", getProcIndex(), population);
    }
}
