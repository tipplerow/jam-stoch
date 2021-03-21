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

import com.tipplerow.jam.lang.Ordinal;
import com.tipplerow.jam.lang.OrdinalIndex;

/**
 * Represents the discrete agents in a stochastic process.
 *
 * @author Scott Shaffer
 */
public abstract class StochAgent extends Ordinal {
    private static final OrdinalIndex ordinalIndex = OrdinalIndex.create();

    /**
     * Creates a new stochastic agent with a unique index that is
     * assigned automatically.
     */
    protected StochAgent() {
        super(ordinalIndex.next());
    }

    /**
     * Returns the unique integer index for this agent.
     *
     * @return the unique integer index for this agent.
     */
    public final int getAgentIndex() {
        return (int) getIndex();
    }
}
