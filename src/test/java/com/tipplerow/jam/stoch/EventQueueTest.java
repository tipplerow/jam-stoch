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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tipplerow.jam.math.JamRandom;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class EventQueueTest {
    private final JamRandom random = JamRandom.generator(20210501);
    private final EventQueue queue;
    private final List<StochEvent> events = new ArrayList<>();
    private final List<FixedRateProc> procs = new ArrayList<>();

    private static final int PROC_COUNT = 25;
    private static final int NEXT_COUNT = 1000;

    public EventQueueTest() {
        createProcesses();
        createEvents();

        this.queue = EventQueue.create(events);
    }

    private void createProcesses() {
        while (procs.size() < PROC_COUNT)
            procs.add(FixedRateProc.create(1.0));
    }

    private void createEvents() {
        for (FixedRateProc proc : procs)
            events.add(StochEvent.first(proc, random));
    }

    @Test public void testNext() {
        for (int trial = 0; trial < NEXT_COUNT; ++trial)
            executeTrial();
    }

    private void executeTrial() {
        Collections.sort(events);

        int actualIndex = queue.nextEvent().getProcIndex();
        int expectedIndex = events.get(0).getProcIndex();

        assertEquals(expectedIndex, actualIndex);

        StochEvent actualEvent = queue.nextEvent();
        StochEvent updatedEvent = actualEvent.next(random);

        queue.updateEvent(updatedEvent);
        events.set(0, updatedEvent);
        queue.validateOrder();
    }
}
