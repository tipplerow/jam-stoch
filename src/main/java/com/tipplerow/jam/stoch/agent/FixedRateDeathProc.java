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
 * Represents a death process with a fixed first-order rate constant.
 *
 * @author Scott Shaffer
 */
public final class FixedRateDeathProc extends DeathProc {
    private final double rateConst;

    private FixedRateDeathProc(StochAgent agent, double rateConst) {
        super(agent);

        validateRateConstant(rateConst);
        this.rateConst = rateConst;
    }

    /**
     * Creates a new death process with a fixed rate constant.
     *
     * @param agent the target agent for the process.
     *
     * @param rateConst the fixed rate constant for the process.
     *
     * @return a new death process with the specified parameters.
     */
    public static DeathProc create(StochAgent agent, double rateConst) {
        return new FixedRateDeathProc(agent, rateConst);
    }

    @Override public double getRateConstant(AgentSystem system) {
        return rateConst;
    }
}
