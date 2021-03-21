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
import java.util.HashMap;
import java.util.Map;

import com.tipplerow.jam.lang.OrdinalMap;

/**
 * Provides a collection of stochastic agents indexed by their ordinal
 * value.
 *
 * @author Scott Shaffer
 */
public final class AgentMap extends OrdinalMap<StochAgent> {
    private AgentMap(Map<Long, StochAgent> map) {
        super(map);
    }

    /**
     * Creates a new, empty agent map backed by a {@code HashMap}.
     *
     * @return a new, empty agent map backed by a {@code HashMap}.
     */
    public static AgentMap create() {
        return new AgentMap(new HashMap<>());
    }

    /**
     * Creates an agent map backed by a {@code HashMap} and
     * populates it with a collection of stochastic agents.
     *
     * @param agents the stochastic agents to add to the map.
     *
     * @return an agent map backed by a {@code HashMap}
     * containing the specified agents.
     */
    public static AgentMap create(Collection<? extends StochAgent> agents) {
        AgentMap map = create();
        map.addAll(agents);
        return map;
    }
}
