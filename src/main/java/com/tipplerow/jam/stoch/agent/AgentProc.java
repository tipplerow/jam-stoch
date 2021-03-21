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

import com.google.common.collect.Multiset;

import com.tipplerow.jam.math.DoubleComparator;
import com.tipplerow.jam.stoch.StochProc;
import com.tipplerow.jam.stoch.StochRate;

/**
 * Represents an agent-based process that may occur in a stochastic
 * simulation.
 *
 * @author Scott Shaffer
 */
public abstract class AgentProc extends StochProc {
    // The instantaneous rate of this process, updated as the
    // underlying stochastic system evolves...
    private StochRate stochRate = null;

    /**
     * Creates a new agent-based process with an unknown initial rate.
     * The rate must be assigned by calling {@code updateRate()} prior
     * to the first step in the stochastic simulation.
     */
    protected AgentProc() {
    }

    /**
     * Returns the reactive agents in this process, which are consumed
     * when this process occurs.
     *
     * @return the reactive agents in this process.
     */
    public abstract Multiset<StochAgent> getReactants();

    /**
     * Returns the agents that are produced when this process occurs.
     *
     * @return the agents that are produced when this process occurs.
     */
    public abstract Multiset<StochAgent> getProducts();

    /**
     * Returns the instantaneous rate constant for this process, which
     * may depend on the simulation time or context.
     *
     * @param system the stochastic system that contains this process.
     *
     * @return the rate constant for this process given the current
     * state of the stochastic system.
     */
    public abstract double getRateConstant(AgentSystem system);

    /**
     * Computes the rate of a first-order process from a rate constant
     * and agent population.
     *
     * @param rateConst the first-order rate constant for the process.
     *
     * @param agentCount the number of instances of the reactive agent
     * that are present.
     *
     * @return the rate of a first-order process with the specified rate
     * constant and agent population.
     */
    public static StochRate computeRate(double rateConst, int agentCount) {
        validateRateConstant(rateConst);
        return StochRate.valueOf(rateConst * agentCount);
    }

    /**
     * Computes the rate of a second-order process from a rate constant
     * and agent populations.
     *
     * @param rateConst the second-order rate constant for the process.
     *
     * @param agent1Count the number of instances of the first reactive
     * agent that are present.
     *
     * @param agent2Count the number of instances of the second reactive
     * agent that are present.
     *
     * @return the rate of a second-order process with the specified rate
     * constant and agent populations.
     */
    public static StochRate computeRate(double rateConst, int agent1Count, int agent2Count) {
        validateRateConstant(rateConst);
        return StochRate.valueOf(rateConst * agent1Count * agent2Count);
    }

    /**
     * Computes the rate of a first-order process for a stochastic
     * system with a given rate constant and reactive agent.
     *
     * @param system a stochastic system.
     *
     * @param rateConst the rate constant for the process.
     *
     * @param agent the reactive agent in the process.
     *
     * @return the rate of a first-order process in the specified
     * stochastic system.
     */
    public static StochRate computeRate(AgentSystem system, double rateConst, StochAgent agent) {
        return computeRate(rateConst, system.countAgent(agent));
    }

    /**
     * Computes the rate of a second-order process for a stochastic
     * system with a given rate constant and reactive agents.
     *
     * @param system a stochastic system.
     *
     * @param rateConst the rate constant for the process.
     *
     * @param agent1 the first reactive agent in the process.
     *
     * @param agent2 the second reactive agent in the process.
     *
     * @return the rate of a second-order process in the specified
     * stochastic system.
     */
    public static StochRate computeRate(AgentSystem system, double rateConst, StochAgent agent1, StochAgent agent2) {
        return computeRate(rateConst, system.countAgent(agent1), system.countAgent(agent2));
    }


    /**
     * Validates a rate constant.
     *
     * @param rateConst the rate constant for a stochastic process.
     *
     * @return the specified rate constant, if it is non-negative.
     *
     * @throws IllegalArgumentException if the rate constant is negative.
     */
    public static double validateRateConstant(double rateConst) {
        if (DoubleComparator.DEFAULT.isNegative(rateConst))
            throw new IllegalArgumentException("Negative rate constant.");
        else
            return rateConst;
    }

    /**
     * Computes the instantaneous rate of this process, which may
     * depend on the simulation time or context.
     *
     * @param system the stochastic system that contains this process.
     *
     * @return the instantaneous rate of this process in the current
     * state of the stochastic system.
     */
    public StochRate computeRate(AgentSystem system) {
        double rate = getRateConstant(system);
        validateRateConstant(rate);

        for (StochAgent reactant : getReactants())
            rate *= system.countAgent(reactant);

        return StochRate.valueOf(rate);
    }

    /**
     * Updates the population of stochastic agents after this process
     * occurs.
     *
     * @param population the population of stochastic agents prior to
     * the occurrence of this process.
     */
    public void updatePopulation(AgentPopulation population) {
        for (StochAgent reactant : getReactants())
            population.remove(reactant);

        for (StochAgent product : getProducts())
            population.add(product);
    }

    /**
     * Updates the instantaneous rate of this process after an event
     * occurs.
     *
     * @param system the stochastic system that contains this process.
     */
    public void updateRate(AgentSystem system) {
        stochRate = computeRate(system);
    }

    @Override public StochRate getStochRate() {
        if (stochRate != null)
            return stochRate;
        else
            throw new IllegalStateException("The process rate has not been assigned.");
    }
}
