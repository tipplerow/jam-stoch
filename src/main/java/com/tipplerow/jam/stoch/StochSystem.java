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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.tipplerow.jam.lang.JamException;

/**
 * Provides a base class for systems of coupled stochastic processes.
 *
 * @author Scott Shaffer
 */
public abstract class StochSystem {
    private final ProcGraph graph = ProcGraph.create();
    private final Map<Integer, StochProc> procs = new LinkedHashMap<>();

    // The number of events that have occurred...
    private long eventCount = 0L;

    // The most recent event to occur...
    private StochEvent lastEvent = null;

    /**
     * Creates an empty stochastic system; processes and links must be
     * added after the system is constructed.
     */
    protected StochSystem() {
    }

    /**
     * Creates a new coupled stochastic system.
     *
     * @param procs the stochastic process which compose the system.
     *
     * @param links the edges of the directed dependency graph for the
     * system.
     *
     * @throws RuntimeException if any rate links refer to processes
     * not contained in the input collection.
     */
    protected StochSystem(Collection<? extends StochProc> procs, Collection<RateLink> links) {
        addProcesses(procs);
        addLinks(links);
    }

    /**
     * Updates the internal state of this stochastic system after an
     * event occurs.  The most recent event to occur may be accessed
     * by calling {@code lastEvent()}.
     */
    protected abstract void updateState();

    /**
     * Adds a stochastic process to this system.
     *
     * @param proc the stochastic process to add.
     *
     * @throws RuntimeException if this system already contains
     * another process with the same index.
     */
    protected void addProcess(StochProc proc) {
        if (containsProcess(proc.getProcIndex()))
            throw JamException.runtime("Duplicate process index: [%d].", proc.getProcIndex());

        procs.put(proc.getProcIndex(), proc);
    }

    /**
     * Adds stochastic process to this system.
     *
     * @param procs the stochastic processes to add.
     *
     * @throws RuntimeException if this system already contains any
     * processes with the same indexes.
     */
    protected void addProcesses(Collection<? extends StochProc> procs) {
        for (StochProc proc : procs)
            addProcess(proc);
    }

    /**
     * Adds a rate link between two processes in this system.
     *
     * @param link the rate link to add.
     *
     * @throws RuntimeException unless this system contains both
     * processes in the link.
     */
    protected void addLink(RateLink link) {
        addLink(link.getPredecessor(), link.getSuccessor());
    }

    /**
     * Adds a rate link between two processes in this system.
     *
     * @param predecessor the predecessor process.
     *
     * @param successor the direct successor process.
     *
     * @throws RuntimeException unless this system contains both
     * processes in the link.
     */
    protected void addLink(StochProc predecessor, StochProc successor) {
        requireProcess(predecessor);
        requireProcess(successor);

        graph.link(predecessor, successor);
    }

    /**
     * Adds rate link between processes in this system.
     *
     * @param links the rate links to add.
     *
     * @throws RuntimeException unless this system contains every
     * process in the links.
     */
    protected void addLinks(Collection<RateLink> links) {
        for (RateLink link : links)
            addLink(link);
    }

    /**
     * Removes a stochastic process from this system.
     *
     * @param index the unique ordinal index of the process to remove.
     *
     * @throws RuntimeException unless this system contains a process
     * with the specified index.
     */
    protected void removeProcess(int index) {
        requireProcess(index);

        procs.remove(index);
        graph.remove(getProcess(index));
    }

    /**
     * Removes a stochastic process from this system.
     *
     * @param proc the process to remove.
     *
     * @throws RuntimeException unless this system contains the
     * specified process.
     */
    protected void removeProcess(StochProc proc) {
        removeProcess(proc.getProcIndex());
    }

    /**
     * Returns a runtime exception for an invalid process index.
     *
     * @param index the invalid process index.
     *
     * @return a runtime exception for the specified process index.
     */
    public static RuntimeException invalidProcessException(int index) {
        return JamException.runtime("Invalid process index: [%d].", index);
    }

    /**
     * Returns a runtime exception for an invalid process.
     *
     * @param proc the invalid process.
     *
     * @return a runtime exception for the specified process.
     */
    public static RuntimeException invalidProcessException(StochProc proc) {
        return invalidProcessException(proc.getProcIndex());
    }

    /**
     * Identifies processes contained in this stochastic system.
     *
     * @param index the ordinal index of the process in question.
     *
     * @return {@code true} iff this system contains a process with
     * the specified index.
     */
    public boolean containsProcess(int index) {
        return procs.containsKey(index);
    }

    /**
     * Identifies processes contained in this stochastic system.
     *
     * @param proc the process in question.
     *
     * @return {@code true} iff this system contains the specified
     * process.
     */
    public boolean containsProcess(StochProc proc) {
        return containsProcess(proc.getProcIndex());
    }

    /**
     * Returns the number of events that have occurred.
     *
     * @return the number of events that have occurred.
     */
    public long countEvents() {
        return eventCount;
    }

    /**
     * Returns the number of stochastic processes in this system.
     *
     * @return the number of stochastic processes in this system.
     */
    public int countProcesses() {
        return procs.size();
    }

    /**
     * Accesses processes in this system by their ordinal index.
     *
     * @param index the ordinal index of the desired process.
     *
     * @return the process with the specified index.
     *
     * @throws RuntimeException unless this system contains a process
     * with the specified index.
     */
    public StochProc getProcess(int index) {
        StochProc process = procs.get(index);

        if (process != null)
            return process;
        else
            throw invalidProcessException(index);
    }

    /**
     * Accesses the instantaneous rates of the processes in this
     * system by their ordinal index.
     *
     * @param index the ordinal index of the desired process.
     *
     * @return the instantaneous rate of the process with the
     * specified index.
     *
     * @throws RuntimeException unless this system contains a process
     * with the specified index.
     */
    public StochRate getStochRate(int index) {
        return getProcess(index).getStochRate();
    }

    /**
     * Returns the most recent event to occur in this system.
     *
     * @return the most recent event to occur in this system
     * ({@code null} before any events have occurred).
     */
    public StochEvent lastEvent() {
        return lastEvent;
    }

    /**
     * Returns the most recent process to occur.
     *
     * @return the most recent process to occur ({@code null} before
     * any events have occurred).
     */
    public StochProc lastEventProcess() {
        if (lastEvent != null)
            return lastEvent.getProc();
        else
            return null;
    }

    /**
     * Returns the (absolute) time when the most recent event occurred.
     *
     * @return the (absolute) time when the most recent event occurred.
     */
    public StochTime lastEventTime() {
        if (lastEvent != null)
            return lastEvent.getTime();
        else
            return StochTime.ZERO;
    }

    /**
     * Requires that this system contains a specific process.
     *
     * @param index the unique ordinal index of the required process.
     *
     * @throws RuntimeException unless this system contains a process
     * with the specified index.
     */
    public void requireProcess(int index) {
        if (!containsProcess(index))
            throw invalidProcessException(index);
    }

    /**
     * Requires that this system contains a specific process.
     *
     * @param proc the required process.
     *
     * @throws RuntimeException unless this system contains the
     * specified process.
     */
    public void requireProcess(StochProc proc) {
        requireProcess(proc.getProcIndex());
    }

    /**
     * Updates the state of this stochastic system after an event
     * occurs.
     *
     * @param event the most recent event to occur in this system.
     *
     * @throws RuntimeException unless the event occurs after the
     * previous event in this system and this system contains the
     * process that occurred.
     */
    public void updateState(StochEvent event) {
        validateEvent(event);

        ++eventCount;
        lastEvent = event;

        updateState();
    }

    private void validateEvent(StochEvent event) {
        if (event.getTime().compareTo(lastEventTime()) <= 0)
            throw JamException.runtime("Next event must occur after the previous event.");

        if (!containsProcess(event.getProc()))
            throw JamException.runtime("Event occurred outside this system.");
    }

    /**
     * Returns a read-only view of the stochastic processes that
     * compose this system.
     *
     * @return a read-only view of the stochastic processes that
     * compose this system.
     */
    public Collection<? extends StochProc> viewProcesses() {
        return Collections.unmodifiableCollection(procs.values());
    }

    /**
     * Returns a read-only view of the processes whose rates may
     * change after another process occurs.
     *
     * @param proc a process that affects the rate of other
     * processes.
     *
     * @return a read-only view of the processes whose rates may
     * change after the specified process occurs.
     */
    public Set<? extends StochProc> viewDependents(StochProc proc) {
        return graph.get(proc);
    }
}
