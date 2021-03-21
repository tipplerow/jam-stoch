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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * Represents a directed dependency graph for a system of coupled
 * stochastic processes.
 *
 * @author Scott Shaffer
 */
public final class ProcGraph {
    //
    // The forward dependency relationships: Calling
    // forward.get(proc) returns all processes whose
    // rates depend on "proc"...
    //
    private final SetMultimap<StochProc, StochProc> forward = HashMultimap.create();

    // The reverse dependency relationships: Calling
    // reverse.get(proc) returns all processes that
    // determine the rate of "proc"...
    //
    private final SetMultimap<StochProc, StochProc> reverse = HashMultimap.create();

    private ProcGraph() {
    }

    private ProcGraph(Collection<RateLink> links) {
        for (RateLink link : links)
            link(link.getPredecessor(), link.getSuccessor());
    }

    /**
     * Creates an empty dependency graph.
     *
     * @return an empty dependency graph.
     */
    public static ProcGraph create() {
        return new ProcGraph();
    }

    /**
     * Creates and populates a dependency graph.
     *
     * @param links the links that define the process couplings.
     *
     * @return a new graph containing the specified dependencies.
     */
    public static ProcGraph create(Collection<RateLink> links) {
        return new ProcGraph(links);
    }

    /**
     * Adds a predecessor process and its direct successors to this
     * graph.
     *
     * @param predecessor the predecessor process to add.
     *
     * @param successors the direct successors of the predecessor
     * process.
     *
     * @throws RuntimeException unless the predecessor is separate and
     * distinct process from the all successors.
     */
    public void add(StochProc predecessor, StochProc... successors) {
        add(predecessor, List.of(successors));
    }

    /**
     * Adds a predecessor process and its direct successors to this
     * graph.
     *
     * @param predecessor the predecessor process to add.
     *
     * @param successors the direct successors of the predecessor
     * process.
     *
     * @throws RuntimeException unless the predecessor is separate and
     * distinct process from the all successors.
     */
    public void add(StochProc predecessor, Collection<? extends StochProc> successors) {
        for (StochProc successor : successors)
            link(predecessor, successor);
    }

    /**
     * Returns all direct successor processes to a given predecessor
     * process.
     *
     * @param predecessor a predecessor process of interest.
     *
     * @return a read-only set containing all direct successors of the
     * specified process.
     */
    public Set<? extends StochProc> get(StochProc predecessor) {
        return Collections.unmodifiableSet(forward.get(predecessor));
    }

    /**
     * Adds an edge to this directed dependency graph.  The rate of
     * the successor process may change when the predecessor process
     * occurs.
     *
     * @param predecessor the predecessor process.
     *
     * @param successor the direct successor (dependent) process.
     *
     * @throws RuntimeException if the predecessor and successor are
     * the same process.
     */
    public void link(StochProc predecessor, StochProc successor) {
        RateLink.validate(predecessor, successor);
        forward.put(predecessor, successor);
        reverse.put(successor, predecessor);
    }

    /**
     * Removes all edges containing a given process from this graph.
     *
     * @param process the process to remove.
     */
    public void remove(StochProc process) {
        forward.removeAll(process);
        reverse.removeAll(process);
    }

    /**
     * Removes an edge from this directed dependency graph (if it exists).
     *
     * @param predecessor the predecessor process.
     *
     * @param successor the successor (dependent) process.
     */
    public void remove(StochProc predecessor, StochProc successor) {
        forward.remove(predecessor, successor);
        reverse.remove(successor, predecessor);
    }
}
