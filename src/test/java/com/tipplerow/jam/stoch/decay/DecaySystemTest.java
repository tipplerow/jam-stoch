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
package com.tipplerow.jam.stoch.decay;

import com.tipplerow.jam.stoch.StochEvent;
import com.tipplerow.jam.stoch.StochRate;
import com.tipplerow.jam.stoch.StochTime;
import com.tipplerow.jam.testng.NumericTestBase;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class DecaySystemTest extends NumericTestBase {
    private static final int[] POPS = new int[] { 100, 200, 300 };
    private static final double[] RATES = new double[] { 1.0, 2.0, 3.0 };
    
    private static DecaySystem createSystem() {
        return DecaySystem.create(POPS, RATES);
    }

    private void assertPopulation(DecaySystem system, int... pops) {
        List<DecayProc> procs = List.copyOf(system.viewProcesses());
        assertEquals(procs.size(), pops.length);

        for (int index = 0; index < procs.size(); ++index)
            assertEquals(procs.get(index).getPopulation(), pops[index]);
    }

    private void assertRates(DecaySystem system, double... rates) {
        List<DecayProc> procs = List.copyOf(system.viewProcesses());
        assertEquals(procs.size(), rates.length);

        for (int index = 0; index < procs.size(); ++index)
            assertEquals(procs.get(index).getStochRate(), StochRate.of(rates[index]));
    }

    @Test public void testEvent() {
        DecaySystem system = createSystem();
        List<DecayProc> procs = List.copyOf(system.viewProcesses());

        DecayProc proc0 = procs.get(0);
        DecayProc proc1 = procs.get(1);
        DecayProc proc2 = procs.get(2);

        assertPopulation(system, 100, 200, 300);
        assertRates(system, 100.0, 400.0, 900.0);

        system.updateState(StochEvent.mark(proc0, StochTime.of(0.1)));
        system.updateState(StochEvent.mark(proc1, StochTime.of(0.2)));
        system.updateState(StochEvent.mark(proc1, StochTime.of(0.3)));
        system.updateState(StochEvent.mark(proc2, StochTime.of(0.4)));
        system.updateState(StochEvent.mark(proc2, StochTime.of(0.5)));
        system.updateState(StochEvent.mark(proc2, StochTime.of(0.6)));

        assertPopulation(system, 99, 198, 297);
        assertRates(system, 99.0, 396.0, 891.0);
    }
}
