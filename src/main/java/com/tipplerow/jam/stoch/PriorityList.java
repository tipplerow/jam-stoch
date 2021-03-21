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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.tipplerow.jam.lang.JamException;
import com.tipplerow.jam.math.DoubleComparator;
import com.tipplerow.jam.math.JamRandom;

/**
 * Maintains a list of stochastic processes wherein processes with the
 * fastest rates are more likely to be near the head of the list, thus
 * allowing for more efficient process selection by direct simulation
 * algorithms.
 *
 * @author Scott Shaffer
 */
public final class PriorityList {
    private final List<StochProc> procList;

    private PriorityList(Collection<? extends StochProc> procs) {
        this.procList = new ArrayList<>(procs);
    }

    /**
     * Creates a new priority list for a fixed collection of
     * stochastic processes.
     *
     * @param procs the stochastic processes to include in the
     * list.
     *
     * @return a new priority list for the specified stochastic
     * processes.
     */
    public static PriorityList create(Collection<? extends StochProc> procs) {
        return new PriorityList(procs);
    }

    /**
     * Creates a new priority list for the processes in a stochastic
     * system.
     *
     * @param system the system of stochastic processes to include in
     * the list.
     *
     * @return a new priority list for the specified stochastic
     * system.
     */
    public static PriorityList create(StochSystem system) {
        return create(system.viewProcesses());
    }

    /**
     * Selects a process {@code k} at random from this list with a
     * probability equal to {@code r(k) / rT}, where {@code r(k)} is
     * the instantaneous rate of process {@code k} and {@code rT} is
     * the total rate of all processes in this list.
     *
     * @param random a random number source.
     *
     * @param totalRate the total instantaneous transition rate over
     * all processes in this list.
     *
     * @return a process {@code k} chosen randomly with probability
     * {@code r(k) / rT}.
     *
     * @throws RuntimeException unless the total rate is positive.
     */
    public StochProc select(JamRandom random, StochRate totalRate) {
        if (!totalRate.isPositive())
            throw JamException.runtime("Total transition rate must be positive.");
        
        double rateTotal = 0.0;
        double threshold = random.nextDouble() * totalRate.doubleValue();

        for (int procIndex = 0; procIndex < procList.size(); ++procIndex) {
            StochProc process = procList.get(procIndex);
            rateTotal += process.getStochRate().doubleValue();

            if (DoubleComparator.DEFAULT.GE(rateTotal, threshold)) {
                //
                // Move the selected rate up the list, which allows
                // for rates that increase during the simulation to
                // "bubble up" to the head of the list...
                //
                if (procIndex > 0)
                    Collections.swap(procList, procIndex, procIndex - 1);

                return process;
            }
        }

        // This should never happen if the threshold has been
        // specified properly....
        throw JamException.runtime("Process selection failed.");
    }
}
