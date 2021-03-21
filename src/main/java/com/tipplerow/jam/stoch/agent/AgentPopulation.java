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

import java.util.Collection;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Maintains a number count of each agent in a stochastic simulation.
 *
 * @author Scott Shaffer
 */
public final class AgentPopulation {
    private final Multiset<StochAgent> counts = HashMultiset.create();

    private AgentPopulation() {
    }

    /**
     * Creates a new empty agent population.
     *
     * @return a new empty agent population.
     */
    public static AgentPopulation create() {
        return new AgentPopulation();
    }

    /**
     * Creates a new agent population.
     *
     * @param agents the initial members of the population.
     *
     * @return a new agent population containing the specified agents.
     */
    public static AgentPopulation create(Collection<StochAgent> agents) {
        AgentPopulation population = create();
        population.add(agents);
        return population;
    }

    /**
     * Creates a new agent population.
     *
     * @param counts the initial population counts for the stochastic
     * agents.
     *
     * @return a new agent population with the specified agent counts.
     */
    public static AgentPopulation create(Multiset<StochAgent> counts) {
        AgentPopulation population = create();
        population.add(counts);
        return population;
    }

    /**
     * Adds one agent to this population.
     *
     * @param agent the agent to add.
     */
    public void add(StochAgent agent) {
        counts.add(agent);
    }

    /**
     * Adds instances of an agent to this population.
     *
     * @param agent the agent to add.
     *
     * @param count the number of instances to add.
     *
     * @throws IllegalArgumentException if the count is negative.
     */
    public void add(StochAgent agent, int count) {
        if (count < 0)
            throw new IllegalArgumentException("Agent count must be non-negative.");
        else
            counts.add(agent, count);
    }

    /**
     * Adds agents to this population.
     *
     * @param agents the agents to add.
     */
    public void add(Collection<StochAgent> agents) {
        for (StochAgent agent : agents)
            add(agent);
    }

    /**
     * Adds agents to this population.
     *
     * @param agents the agents to add.
     */
    public void add(Multiset<StochAgent> agents) {
        for (Multiset.Entry<StochAgent> entry : agents.entrySet())
            add(entry.getElement(), entry.getCount());
    }

    /**
     * Counts the number of instances of an agent in this population.
     *
     * @param agent the agent to count.
     *
     * @return the number of instances of the specified agent
     * contained in this population.
     */
    public int count(StochAgent agent) {
        return counts.count(agent);
    }

    /**
     * Removes one agent from this population.
     *
     * @param agent the agent to remove.
     *
     * @throws IllegalArgumentException unless this population
     * contains at least one instance of the specified agent.
     */
    public void remove(StochAgent agent) {
        remove(agent, 1);
    }

    /**
     * Removes one agent from this population.
     *
     * @param agent the agent to remove.
     *
     * @param count the number of instances to remove.
     *
     * @throws IllegalArgumentException unless this population
     * contains at least {@code count} instances of the agent.
     */
    public void remove(StochAgent agent, int count) {
        if (counts.count(agent) < count)
            throw new IllegalArgumentException("Agent count must remain non-negative.");
        else
            counts.remove(agent, count);
    }

    /**
     * Removes agents from this population.
     *
     * @param agents the agents to remove.
     */
    public void remove(Multiset<StochAgent> agents) {
        for (Multiset.Entry<StochAgent> entry : agents.entrySet())
            remove(entry.getElement(), entry.getCount());
    }

    /**
     * Assigns the population of an agent.
     *
     * @param agent the agent to add.
     *
     * @param count the number of agents to assign.
     *
     * @throws IllegalArgumentException if the count is negative.
     */
    public void set(StochAgent agent, int count) {
        if (count < 0)
            throw new IllegalArgumentException("Agent count must be non-negative.");
        else
            counts.setCount(agent, count);
    }
}
