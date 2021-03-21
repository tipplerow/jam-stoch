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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tipplerow.jam.lang.JamException;
import com.tipplerow.jam.stoch.StochEvent;
import com.tipplerow.jam.stoch.StochSystem;

/**
 * Represents a stochastic system with a fixed number of independent
 * first-order decay processes.  This system has a simple analytical
 * solution and is provided primarily to test stochastic simulation
 * algorithms.
 *
 * @author Scott Shaffer
 */
public final class DecaySystem extends StochSystem {
    private DecaySystem(List<DecayProc> procs) {
        super(procs, List.of());
    }

    /**
     * Creates a new stochastic system with a fixed collection of
     * independent first-order decay processes.
     *
     * @param pops the initial populations of the undecayed states.
     *
     * @param rates the unit rate constants for each decay process.
     *
     * @return the stochastic system defined by the input parameters.
     *
     * @throws IllegalArgumentException unless the parameter arrays
     * have equal length and all populations and rate constants are
     * positive.
     */
    public static DecaySystem create(int[] pops, double[] rates) {
        return new DecaySystem(createProcs(pops, rates));
    }

    private static List<DecayProc> createProcs(int[] pops, double[] rates) {
        if (pops.length < 1)
            throw JamException.runtime("At least one process must be defined.");

        if (pops.length != rates.length)
            throw JamException.runtime("Populations and rates are not consistent.");

        List<DecayProc> procs = new ArrayList<DecayProc>(pops.length);

        for (int index = 0; index < pops.length; ++index)
            procs.add(DecayProc.create(pops[index], rates[index]));

        return procs;
    }

    @Override public DecayProc getProcess(int index) {
        return (DecayProc) super.getProcess(index);
    }

    @Override public DecayProc lastEventProcess() {
        return (DecayProc) super.lastEventProcess();
    }

    @Override public void updateState() {
        //
        // All decay processes are independent...
        //
        lastEventProcess().decay();
    }

    @SuppressWarnings("unchecked")
    @Override public Collection<DecayProc> viewProcesses() {
        return (Collection<DecayProc>) super.viewProcesses();
    }
}
