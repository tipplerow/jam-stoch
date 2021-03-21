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
package com.tipplerow.jam.stoch.agent;

/**
 * Represents a birth process with a fixed first-order rate constant.
 *
 * @author Scott Shaffer
 */
public final class FixedRateBirthProc extends BirthProc {
    private final double rateConst;

    private FixedRateBirthProc(StochAgent parent, StochAgent child, double rateConst) {
        super(parent, child);

        validateRateConstant(rateConst);
        this.rateConst = rateConst;
    }

    /**
     * Creates a new non-mutating birth process with a fixed rate
     * constant.
     *
     * @param agent the replicating agent in the process.
     *
     * @param rateConst the fixed rate constant for the process.
     *
     * @return a new non-mutating birth process with the specified
     * parameters.
     */
    public static BirthProc create(StochAgent agent, double rateConst) {
        return new FixedRateBirthProc(agent, agent, rateConst);
    }

    /**
     * Creates a new mutation process with a fixed rate constant.
     *
     * @param parent the parent agent for the process.
     *
     * @param child the child agent for the process.
     *
     * @param rateConst the fixed rate constant for the process.
     *
     * @return a new mutation process with the specified parameters.
     */
    public static BirthProc create(StochAgent parent, StochAgent child, double rateConst) {
        return new FixedRateBirthProc(parent, child, rateConst);
    }

    @Override public double getRateConstant(AgentSystem system) {
        return rateConst;
    }
}
