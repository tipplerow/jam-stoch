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
import java.util.Comparator;

import com.tipplerow.jam.lang.Ordinal;
import com.tipplerow.jam.lang.OrdinalIndex;

/**
 * Represents a process that may occur in a stochastic simulation.
 *
 * @author Scott Shaffer
 */
public abstract class StochProc extends Ordinal {
    private static final OrdinalIndex ordinalIndex = OrdinalIndex.create();

    /**
     * Creates a new stochastic process with a unique index that is
     * assigned automatically.
     */
    protected StochProc() {
        super(ordinalIndex.next());
    }

    /**
     * A comparator that orders processes by their instantaneous rates
     * in ascending order (slowest process first).
     */
    public static final Comparator<StochProc> ASCENDING_RATE_COMPARATOR =
            (proc1, proc2) -> StochRate.ASCENDING_COMPARATOR.compare(proc1.getStochRate(), proc2.getStochRate());

    /**
     * A comparator that orders processes by their instantaneous rates
     * in descending order (fastest process first).
     */
    public static final Comparator<StochProc> DESCENDING_RATE_COMPARATOR =
            (proc1, proc2) -> StochRate.DESCENDING_COMPARATOR.compare(proc1.getStochRate(), proc2.getStochRate());

    /**
     * Computes the total rate for a collection of stochastic processes.
     *
     * @param procs the stochastic processes of interest.
     *
     * @return the total rate taken over every input process.
     */
    public static StochRate computeTotalRate(Collection<? extends StochProc> procs) {
        double totalRate = 0.0;

        for (StochProc proc : procs)
            totalRate += proc.getStochRate().doubleValue();

        return StochRate.valueOf(totalRate);
    }

    /**
     * Returns the instantaneous rate of this process.
     *
     * @return the instantaneous rate of this process.
     */
    public abstract StochRate getStochRate();

    /**
     * Returns the unique ordinal index for this process.
     *
     * @return the unique ordinal index for this process.
     */
    public final int getProcIndex() {
        return (int) getIndex();
    }
}
