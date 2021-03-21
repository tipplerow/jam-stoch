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
package com.tipplerow.jam.stoch.agent;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Multiset;

import com.tipplerow.jam.lang.JamException;

/**
 * Represents a capacity-limited process: The rate of an underlying
 * process drops to zero when the total population of a subset of
 * stochastic agents reaches a fixed capacity threshold. (The rate
 * is unchanged below the threshold.)
 *
 * @author Scott Shaffer
 */
public final class CappedProc extends AgentProc {
    private final int capacity;
    private final AgentProc baseProc;
    private final Set<StochAgent> capped;

    private CappedProc(AgentProc baseProc, Set<StochAgent> capped, int capacity) {
        super();

        validateCapacity(capacity);

        this.capped = capped;
        this.capacity = capacity;
        this.baseProc = baseProc;
    }

    /**
     * Creates a new capacity-limited process.
     *
     * @param baseProc the underlying base process.
     *
     * @param capped the stochastic agents that contribute to the
     * population limit.
     *
     * @param capacity the maximum population of the capped agents.
     *
     * @return a new capacity-limited process with the specified
     * parameters.
     */
    public static CappedProc create(AgentProc baseProc, Set<StochAgent> capped, int capacity) {
        return new CappedProc(baseProc, capped, capacity);
    }

    /**
     * Ensures that the capacity of a population-limited process is
     * positive.
     *
     * @param capacity the capacity to validate.
     *
     * @throws RuntimeException unless the capacity is positive.
     */
    public static void validateCapacity(int capacity) {
        if (capacity < 1)
            throw JamException.runtime("Capacity must be positive.");
    }

    /**
     * Returns a read-only view of the stochastic agents that
     * contribute to the population limit.
     *
     * @return a read-only view of the stochastic agents that
     * contribute to the population limit.
     */
    public Set<StochAgent> viewCapped() {
        return Collections.unmodifiableSet(capped);
    }

    @Override public Multiset<StochAgent> getReactants() {
        return baseProc.getReactants();
    }

    @Override public Multiset<StochAgent> getProducts() {
        return baseProc.getProducts();
    }

    @Override public double getRateConstant(AgentSystem system) {
        if (system.countAgents(capped) < capacity)
            return baseProc.getRateConstant(system);
        else
            return 0.0;
    }

    @Override public void updatePopulation(AgentPopulation population) {
        baseProc.updatePopulation(population);
    }
}
