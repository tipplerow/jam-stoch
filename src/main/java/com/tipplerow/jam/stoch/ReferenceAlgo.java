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

import java.util.Collection;

import com.tipplerow.jam.lang.JamException;
import com.tipplerow.jam.math.DoubleComparator;
import com.tipplerow.jam.math.JamRandom;

/**
 * Implements the direct stochastic simulation method of Gillespie
 * without any optimizations.
 *
 * <p>The calculation time per step scales linearly with the number
 * of stochastic processes (reactions or pathways).  This algorithm
 * is unlikely to be the best choice for real-world problems; it is
 * provided primarily as a baseline against which to benchmark more
 * efficient algorithms.
 *
 * @author Scott Shaffer
 */
public final class ReferenceAlgo extends StochAlgo {
    private ReferenceAlgo(JamRandom random, StochSystem system) {
        super(random, system);
    }

    /**
     * Creates a new reference simulation algorithm that implements
     * the original <em>direct method</em> of Gillespie without any
     * efficiency optimizations.
     *
     * @param random the random number source.
     *
     * @param system the stochastic system to simulate.
     *
     * @return a reference simulation algorithm for the specified
     * stochastic system.
     */
    public static ReferenceAlgo create(JamRandom random, StochSystem system) {
        return new ReferenceAlgo(random, system);
    }

    @Override protected StochEvent nextEvent() {
        StochRate totalRate = StochProc.computeTotalRate(system.viewProcesses());
        return StochEvent.mark(nextProc(totalRate), nextTime(totalRate));
    }

    @Override protected void updateState(StochEvent event, Collection<? extends StochProc> dependents) {
        //
        // This algorithm does not maintain any internal state
        // variables (the total reaction rate is recomputed at
        // every time step), so there is nothing to update...
        //
    }

    private StochProc nextProc(StochRate totalRate) {
        //
        // Accumulate the process rates until we find one greater than
        // U * totalRate, where U is a uniform random deviate on [0, 1]...
        //
        double procTotal = 0.0;
        double threshold = random.nextDouble() * totalRate.doubleValue();

        for (StochProc proc : system.viewProcesses()) {
            procTotal += proc.getStochRate().doubleValue();

            if (DoubleComparator.DEFAULT.GE(procTotal, threshold))
                return proc;
        }

        // This should never happen...
        throw JamException.runtime("Next process selection failed.");
    }

    private StochTime nextTime(StochRate totalRate) {
        return totalRate.sampleTime(system.lastEventTime(), random);
    }
}

