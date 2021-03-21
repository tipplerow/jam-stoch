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

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import com.tipplerow.jam.stoch.StochRate;

/**
 * Represents a stochastic process with a single reactant, {@code R}.
 * The process occurs at a rate {@code k * nR}, where {@code k} is the
 * instantaneous rate constant and {@code nR} is the current number of
 * instances of {@code R}.  After the process occurs, the population of
 * {@code R} decreases by one: {@code nR => nR - 1}.
 *
 * @author Scott Shaffer
 */
public abstract class FirstOrderProc extends AgentProc {
    /**
     * The reactant agent.
     */
    protected final StochAgent reactant;

    /**
     * Creates a new first-order process with a fixed reactant.
     *
     * @param reactant the reactant agent for the process.
     */
    protected FirstOrderProc(StochAgent reactant) {
        this.reactant = reactant;
    }

    /**
     * Returns the reactant agent for this process.
     *
     * @return the reactant agent for this process.
     */
    public StochAgent getReactant() {
        return reactant;
    }

    @Override public StochRate computeRate(AgentSystem system) {
        return computeRate(system, getRateConstant(system), reactant);
    }

    @Override public Multiset<StochAgent> getReactants() {
        return ImmutableMultiset.of(reactant);
    }

    @Override public void updatePopulation(AgentPopulation population) {
        population.remove(reactant);
        population.add(getProducts());
    }
}
