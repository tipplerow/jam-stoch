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

import com.tipplerow.jam.math.JamRandom;

/**
 * Implements the direct stochastic simulation method of Gillespie
 * with a few performance optimizations.
 *
 * @author Scott Shaffer
 */
public final class DirectAlgo extends StochAlgo {
    private final RateManager rateManager;
    private final PriorityList priorityList;

    private DirectAlgo(JamRandom random, StochSystem system) {
        super(random, system);

        this.rateManager = RateManager.create(system);
        this.priorityList = PriorityList.create(system);
    }

    /**
     * Creates a new stochastic simulation algorithm that implements
     * the original <em>direct method</em> of Gillespie (with a few
     * performance optimizations).
     *
     * @param random the random number source.
     *
     * @param system the stochastic system to simulate.
     *
     * @return a direct simulation algorithm for the specified system.
     */
    public static DirectAlgo create(JamRandom random, StochSystem system) {
        return new DirectAlgo(random, system);
    }

    @Override protected StochEvent nextEvent() {
        StochRate totalRate =
            rateManager.getTotalRate();

        return StochEvent.mark(nextProc(totalRate),
                               nextTime(totalRate));
    }

    private StochProc nextProc(StochRate totalRate) {
        return priorityList.select(random, totalRate);
    }

    private StochTime nextTime(StochRate totalRate) {
        return totalRate.sampleTime(system.lastEventTime(), random);
    }

    @Override protected void updateState(StochEvent event, Collection<? extends StochProc> dependents) {
        rateManager.updateTotalRate(event.getProc(), dependents);
    }
}
