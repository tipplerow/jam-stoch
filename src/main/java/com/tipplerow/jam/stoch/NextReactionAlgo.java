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
 * Implements the <em>next reaction</em> stochastic simulation method
 * of Gibson and Bruck [J. Phys. Chem. A (2000) 104, 1876-1889].
 *
 * @author Scott Shaffer
 */
public final class NextReactionAlgo extends StochAlgo {
    private final EventQueue eventQueue;

    private NextReactionAlgo(JamRandom random, StochSystem system) {
        super(random, system);
        this.eventQueue = EventQueue.create(StochEvent.first(system, random));
    }

    /**
     * Creates a new stochastic simulation algorithm that implements
     * the <em>next reaction</em> method of Gibson and Bruck [see
     * J. Phys. Chem. A (2000) 104, 1876-1889].
     *
     * @param random the random number source.
     *
     * @param system the stochastic system to simulate.
     *
     * @return a next-reaction simulation algorithm for the specified
     * system.
     */
    public static NextReactionAlgo create(JamRandom random, StochSystem system) {
        return new NextReactionAlgo(random, system);
    }

    @Override protected StochEvent nextEvent() {
        return eventQueue.nextEvent();
    }

    @Override protected void updateState(StochEvent event, Collection<? extends StochProc> dependents) {
        eventQueue.updateEvent(event.next(random));

        for (StochProc dependent : dependents) {
            StochEvent prevEvent = eventQueue.findEvent(dependent);
            StochEvent nextEvent = prevEvent.update(event, random);

            eventQueue.updateEvent(nextEvent);
        }
    }
}
