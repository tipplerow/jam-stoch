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

import com.tipplerow.jam.lang.DomainDouble;
import com.tipplerow.jam.math.DoubleRange;

/**
 * Represents the time at which a stochastic process occurs.
 *
 * @author Scott Shaffer
 */
public final class StochTime extends DomainDouble implements Comparable<StochTime> {
    private StochTime(double value) {
        super(value, RANGE);
    }

    /**
     * Valid range for times.
     */
    public static final DoubleRange RANGE = DoubleRange.NON_NEGATIVE;

    /**
     * A globally sharable instance representing zero time.
     */
    public static final StochTime ZERO = of(0.0);

    /**
     * A globally sharable instance representing the end of time.
     */
    public static final StochTime INFINITY = of(Double.POSITIVE_INFINITY);

    /**
     * Creates a new time from a floating-point value.
     *
     * @param value the stochastic time.
     *
     * @return a {@code StochTime} object having the specified time.
     *
     * @throws IllegalArgumentException if the time is
     * negative.
     */
    public static StochTime of(double value) {
        return new StochTime(value);
    }

    /**
     * Adds a time interval to this time and returns the result in
     * a new object; this object is unchanged.
     *
     * @param time the time interval to add.
     *
     * @return a new stochastic time object representing the instant
     * occurring {@code time} units after this time.
     */
    public StochTime plus(double time) {
        return of(this.doubleValue() + time);
    }

    @Override public int compareTo(StochTime that) {
        return compare(this, that);
    }
}
