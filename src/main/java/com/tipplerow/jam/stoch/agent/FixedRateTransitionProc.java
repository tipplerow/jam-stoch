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
 * Represents a transition process with a fixed first-order rate constant.
 *
 * @author Scott Shaffer
 */
public final class FixedRateTransitionProc extends TransitionProc {
    private final double rateConst;

    private FixedRateTransitionProc(StochAgent reactant, StochAgent product, double rateConst) {
        super(reactant, product);

        validateRateConstant(rateConst);
        this.rateConst = rateConst;
    }

    /**
     * Creates a new transition process with a fixed rate constant.
     *
     * @param reactant the reactant agent for the process.
     *
     * @param product the product agent for the process.
     *
     * @param rateConst the fixed rate constant for the process.
     *
     * @return a new transition process with the specified parameters.
     */
    public static TransitionProc create(StochAgent reactant, StochAgent product, double rateConst) {
        return new FixedRateTransitionProc(reactant, product, rateConst);
    }

    @Override public double getRateConstant(AgentSystem system) {
        return rateConst;
    }
}
