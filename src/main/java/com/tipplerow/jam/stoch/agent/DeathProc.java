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

/**
 * Represents the first-order stochastic process of cell death.  Given
 * the cellular agent {@code C}, the process occurs at a rate equal to
 * {@code k * nC}, where {@code k} is the instantaneous rate constant
 * and {@code nC} is the current number of cells.  After the process
 * occurs, the number of cells decreases by one: {@code nC => nC - 1}.
 *
 * @author Scott Shaffer
 */
public abstract class DeathProc extends FirstOrderProc {
    /**
     * Creates a new death process with a fixed agent.
     *
     * @param agent the target agent for the process.
     */
    protected DeathProc(StochAgent agent) {
        super(agent);
    }

    @Override public Multiset<StochAgent> getProducts() {
        return ImmutableMultiset.of();
    }

    @Override public void updatePopulation(AgentPopulation population) {
        population.remove(reactant);
    }
}
