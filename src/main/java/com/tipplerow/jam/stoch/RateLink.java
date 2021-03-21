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

import com.tipplerow.jam.lang.JamException;

/**
 * Represents an edge in a directed dependency graph: a link between a
 * predecessor process and a successor process whose rate changes when
 * the predecessor process occurs.
 *
 * @author Scott Shaffer
 */
public final class RateLink {
    private final StochProc predecessor;
    private final StochProc successor;

    private RateLink(StochProc predecessor, StochProc successor) {
        validate(predecessor, successor);
        this.predecessor = predecessor;
        this.successor = successor;
    }

    /**
     * Creates a new link between processes: when the predecessor
     * occurs, it triggers a change in the rate of the successor
     * process.
     *
     * @param predecessor the predecessor process.
     *
     * @param successor the successor process.
     *
     * @return the new process link for the specified processes.
     *
     * @throws RuntimeException if the predecessor and successor are
     * the same process.
     */
    public static RateLink link(StochProc predecessor, StochProc successor) {
        return new RateLink(predecessor, successor);
    }

    /**
     * Ensures that the predecessor and successor are separate and
     * distinct processes.
     *
     * @param predecessor the predecessor process.
     *
     * @param successor the successor process.
     *
     * @throws RuntimeException if the predecessor and successor are
     * the same process.
     */
    public static void validate(StochProc predecessor, StochProc successor) {
        if (predecessor.equals(successor))
            throw JamException.runtime("Linked processes must be distinct.");
    }

    /**
     * Returns the predecessor process: the process, when it occurs,
     * that triggers a change in the rate of the successor process.
     *
     * @return the predecessor process.
     */
    public StochProc getPredecessor() {
        return predecessor;
    }

    /**
     * Returns the successor process: the process whose rate changes
     * in response to the occurrence of the predecessor process.
     *
     * @return the successor process.
     */
    public StochProc getSuccessor() {
        return successor;
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof RateLink) && equalsLink((RateLink) obj);
    }

    private boolean equalsLink(RateLink that) {
        return this.predecessor.equals(that.predecessor) && this.successor.equals(that.successor);
    }

    @Override public int hashCode() {
        return predecessor.hashCode() + 31 * successor.hashCode();
    }

    @Override public String toString() {
        return String.format("RateLink(%s => %s)", predecessor, successor);
    }
}
