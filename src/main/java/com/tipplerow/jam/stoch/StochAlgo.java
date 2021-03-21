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

import lombok.Getter;

/**
 * Provides a base class for stochastic simulation algorithms.
 * 
 * @author Scott Shaffer
 */
public abstract class StochAlgo {
    /**
     * The random number source.
     */
    @Getter
    protected final JamRandom random;

    /**
     * The stochastic system being simulated.
     */
    @Getter
    protected final StochSystem system;

    /**
     * Creates a new stochastic simulation algorithm.
     *
     * @param random the random number source.
     *
     * @param system the stochastic system to simulate.
     */
    protected StochAlgo(JamRandom random, StochSystem system) {
        this.random = random;
        this.system = system;
    }

    /**
     * Selects the next event to occur in the simulation.
     *
     * @return the next event in the simulation.
     */
    protected abstract StochEvent nextEvent();

    /**
     * Updates the internal state of this algorithm after an event
     * occurs.
     *
     * @param event the most recent event to occur.
     *
     * @param dependents processes whose rates have changed as a
     * result of the event (excluding the process that occurred).
     */
    protected abstract void updateState(StochEvent event, Collection<? extends StochProc> dependents);

    /**
     * Advances the simulation by selecting the next stochastic event
     * and updating the instantaneous process rates in the underlying
     * stochastic system.
     */
    public void advance() {
        StochEvent event = nextEvent();
        system.updateState(event);
        updateState(event, system.viewDependents(event.getProc()));
    }
}
