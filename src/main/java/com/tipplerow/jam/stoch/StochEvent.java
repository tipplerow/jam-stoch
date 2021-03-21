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
import java.util.List;

import com.tipplerow.jam.lang.JamException;
import com.tipplerow.jam.math.JamRandom;

import lombok.Getter;

/**
 * Represents a discrete event in a stochastic simulation.
 *
 * <p>Each {@code StochEvent} object may represent an event that has
 * occurred during the course of a stochastic simulation or an event
 * that <em>will</em> occur sometime in the future in the course of a
 * next-reaction method simulation.
 *
 * @author Scott Shaffer
 */
public final class StochEvent implements Comparable<StochEvent> {
    /**
     * The underlying stochastic process.
     */
    @Getter
    private final StochProc proc;

    /**
     * The instantaneous transition rate for the underlying process
     * at the time when this event event object was created.
     */
    @Getter
    private final StochRate rate;

    /**
     * The (absolute) time when this event occurred or will occur.
     */
    @Getter
    private final StochTime time;

    private StochEvent(StochProc proc, StochTime time) {
        this.proc = proc;
        this.time = time;
        this.rate = proc.getStochRate();
    }

    /**
     * Creates a new object to mark the occurrence of an event.
     *
     * @param proc the stochastic process that has occurred.
     *
     * @param time the (absolute) time when the event occurred.
     */
    public static StochEvent mark(StochProc proc, StochTime time) {
        return new StochEvent(proc, time);
    }

    /**
     * Samples the first occurrence of a stochastic process from an
     * exponential probability distribution with a rate parameter
     * equal to the instantaneous rate of the process.
     *
     * @param proc the stochastic process that will occur.
     *
     * @param random a source of uniform random number variables.
     *
     * @return the first event for the specified stochastic process.
     */
    public static StochEvent first(StochProc proc, JamRandom random) {
        StochRate rate = proc.getStochRate();
        StochTime time = rate.sampleTime(StochTime.ZERO, random);

        return new StochEvent(proc, time);
    }

    /**
     * Samples the first events for each process in a stochastic
     * system.
     *
     * @param system a stochastic system to simulate.
     *
     * @param random a source of uniform random number variables.
     *
     * @return the first events for each process in the stochastic
     * system.
     */
    public static List<StochEvent> first(StochSystem system, JamRandom random) {
        List<StochEvent> events = new ArrayList<>(system.countProcesses());

        for (StochProc proc : system.viewProcesses())
            events.add(StochEvent.first(proc, random));

        return events;
    }

    /**
     * Samples the next occurrence of the underlying stochastic
     * process from an exponential probability distribution with
     * a rate parameter equal to the instantaneous rate of the
     * underlying process; this event object is unchanged.
     *
     * @param random a source of uniform random number variables.
     *
     * @return the next event for the underlying stochastic process.
     */
    public StochEvent next(JamRandom random) {
        StochRate newRate = this.proc.getStochRate();
        StochTime oldTime = this.time;
        StochTime newTime = newRate.sampleTime(oldTime, random);

        return new StochEvent(proc, newTime);
    }

    /**
     * Updates the next occurrence of the underlying stochastic
     * process after a different <em>linked</em> process occurs first,
     * which changes the instantaneous rate of the underlying process;
     * this event object is unchanged.
     *
     * <p>The updated event time is computed by the next-reaction
     * method of Gibson and Bruck [J. Phys. Chem. A (2000) 104, 1876].
     *
     * @param linkedTime the (absolute) time when the linked process
     * occurred.
     *
     * @param random a source of uniform random number variables (used
     * only if the previous rate was zero and the next event time must
     * be sampled from an exponential distribution using the new rate).
     *
     * @return the next event for the underlying stochastic process.
     *
     * @throws RuntimeException if the linked event occurs after this
     * event.
     */
    public StochEvent update(StochTime linkedTime, JamRandom random) {
        //
        // The stochastic system that contains the underlying process
        // for this event will update its instantaneous rate after the
        // linked process occurs...
        //
        if (linkedTime.GT(this.time))
            throw JamException.runtime("Linked process occurred after this process.");

        StochRate oldRate = this.rate;
        StochTime oldTime = this.time;
        StochRate newRate = proc.getStochRate();
        StochTime newTime = null;

        if (newRate.isZero()) {
            //
            // Until the new rate changes, the underlying process will
            // never occur...
            //
            newTime = StochTime.INFINITY;
        }
        else if (oldRate.isZero()) {
            //
            // The previous time must be infinite, so we must sample a
            // new waiting time using the new rate...
            //
            newTime = newRate.sampleTime(linkedTime, random);
        }
        else {
            //
            // Gibson and Bruck show that the waiting time to the next
            // event is equal to the previously unelapsed waiting time
            // scaled by the ratio of the old to new rates...
            //
            double rateRatio = oldRate.doubleValue() / newRate.doubleValue();
            double unelapsed = oldTime.doubleValue() - linkedTime.doubleValue();

            newTime = linkedTime.plus(rateRatio * unelapsed);
        }

        return new StochEvent(proc, newTime);
    }

    /**
     * Updates the next occurrence of the underlying process after an
     * event occurs in the stochastic system, either to the underlying
     * process in this event object or to a different <em>linked</em>
     * process which changes the instantaneous rate of the underlying
     * process; this event object is unchanged.
     *
     * @param event the most recent event to occur in the stochastic
     * system.
     *
     * @param random a source of uniform random number variables (used
     * to sample new waiting times as necessary).
     *
     * @return the next event for the underlying stochastic process.
     */
    public StochEvent update(StochEvent event, JamRandom random) {
        if (event.equals(this))
            return next(random);
        else
            return update(event.time, random);
    }

    /**
     * Returns the ordinal index of the underlying stochastic process.
     *
     * @return the ordinal index of the underlying stochastic process.
     */
    public int getProcIndex() {
        return proc.getProcIndex();
    }

    /**
     * Defines the natural ordering of events as their chronological
     * order, with ties broken by the process rate (with higher rates
     * occurring first) and then by the index of the process (with the
     * lower index occurring first).
     *
     * @param that an event to compare with this event.
     *
     * @return an integer less than, equal to, or greater than zero
     * according whether this event occurs before, at the same time,
     * or later than the input event.
     */
    @Override public int compareTo(StochEvent that) {
        int timeCmp = this.time.compareTo(that.time);

        if (timeCmp != 0)
            return timeCmp;

        int rateCmp = this.rate.compareTo(that.rate); 

        if (rateCmp != 0)
            return -rateCmp; // Higher rate first...
        else
            return Integer.compare(this.proc.getProcIndex(), that.proc.getProcIndex());
    }

    @Override public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof StochEvent) && equalsEvent((StochEvent) obj));
    }

    private boolean equalsEvent(StochEvent that) {
        return this.proc.equals(that.proc)
            && this.rate.equals(that.rate)
            && this.time.equals(that.time);
    }

    @Override public String toString() {
        return String.format("StochEvent(%s, %f @ %f)", proc, rate.doubleValue(), time.doubleValue());
    }
}
