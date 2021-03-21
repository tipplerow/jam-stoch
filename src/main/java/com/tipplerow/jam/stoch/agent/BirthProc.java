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
 * Represents the stochastic process of cellular division with the
 * possibility of mutation.  With the parent agent {@code P} and the
 * child agent {@code C}, the birth process occurs at a rate equal to
 * {@code k * nP}, where {@code k} is the instantaneous rate constant
 * and {@code nP} is the number of instances of {@code P}.  After the
 * birth process occurs, the number of child instances increases by
 * one: {@code nC => nC + 1}.
 *
 * <p>Notes: (1) For cellular division without mutation, the parent
 * and child agents are identical. (2) The rate constant may change
 * through time and/or be context-dependent.
 *
 * @author Scott Shaffer
 */
public abstract class BirthProc extends FirstOrderProc {
    /**
     * The child agent produced by this process.
     */
    protected final StochAgent child;

    /**
     * Creates a new birth process with a fixed parent and child.
     *
     * @param parent the parent agent for the process.
     *
     * @param child the child agent for the process.
     */
    protected BirthProc(StochAgent parent, StochAgent child) {
        super(parent);
        this.child = child;
    }

    /**
     * Returns the parent agent for this birth process.
     *
     * @return the parent agent for this birth process.
     */
    public StochAgent getParent() {
        return reactant;
    }

    /**
     * Returns the child agent for this birth process.
     *
     * @return the child agent for this birth process.
     */
    public StochAgent getChild() {
        return child;
    }

    @Override public Multiset<StochAgent> getProducts() {
        return ImmutableMultiset.of(reactant, child);
    }

    @Override public void updatePopulation(AgentPopulation population) {
        population.add(child);
    }
}
