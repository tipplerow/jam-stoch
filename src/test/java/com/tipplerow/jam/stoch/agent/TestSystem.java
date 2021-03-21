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

import java.util.List;

public final class TestSystem extends AgentSystem {
    private TestSystem() {
        super(mapAgents(), initialPopulation(), listProcesses(), List.of());
    }
        
    public static final int INIT_POP_A = 1000;
    public static final int INIT_POP_B = 2000;
    public static final int INIT_POP_C = 3000;

    public static final double A_BIRTH_RATE = 1.0;
    public static final double B_DEATH_RATE = 2.0;
    public static final double C_TRANS_RATE = 3.0;

    public static final BirthProc BIRTH_PROC =
        FixedRateBirthProc.create(TestAgent.A, A_BIRTH_RATE);

    public static final DeathProc DEATH_PROC =
        FixedRateDeathProc.create(TestAgent.B, B_DEATH_RATE);

    public static final TransitionProc TRANS_PROC =
        FixedRateTransitionProc.create(TestAgent.C, TestAgent.D, C_TRANS_RATE);

    public static AgentMap mapAgents() {
        return AgentMap.create(List.of(TestAgent.A, TestAgent.B, TestAgent.C, TestAgent.D));
    }

    public static AgentPopulation initialPopulation() {
        AgentPopulation population = AgentPopulation.create();

        population.set(TestAgent.A, INIT_POP_A);
        population.set(TestAgent.B, INIT_POP_B);
        population.set(TestAgent.C, INIT_POP_C);

        return population;
    }

    public static List<AgentProc> listProcesses() {
        return List.of(BIRTH_PROC, DEATH_PROC, TRANS_PROC);
    }

    public static TestSystem create() {
        return new TestSystem();
    }
}
