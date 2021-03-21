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

import com.tipplerow.jam.math.JamRandom;
import com.tipplerow.jam.stat.Stat;
import com.tipplerow.jam.vector.JamVector;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class StochRateTest {
    private static final StochRate rate1 = StochRate.valueOf(1.0);
    private static final StochRate rate2 = StochRate.valueOf(2.0);
    private static final StochRate rate3 = StochRate.valueOf(3.0);
    private static final StochRate rate4 = StochRate.valueOf(4.0);

    @Test public void testComparators() {
        List<StochRate> rates = new ArrayList<>();

        rates.add(rate3);
        rates.add(rate4);
        rates.add(rate1);
        rates.add(rate2);

        rates.sort(StochRate.ASCENDING_COMPARATOR);
        assertEquals(List.of(rate1, rate2, rate3, rate4), rates);

        rates.sort(StochRate.DESCENDING_COMPARATOR);
        assertEquals(List.of(rate4, rate3, rate2, rate1), rates);
    }

    @Test public void testSampleInterval() {
        int SAMPLE_COUNT = 10000;

        JamRandom random = JamRandom.generator(20201111);
        JamVector samples = JamVector.dense(SAMPLE_COUNT);

        for (int index = 0; index < samples.length(); ++index)
            samples.set(index, rate2.sampleInterval(random));

        assertEquals(0.5, Stat.mean(samples), 0.005);
        assertEquals(0.5 * Math.log(2.0), Stat.median(samples), 0.005);
    }

    @Test public void testTotal() {
        assertEquals(StochRate.valueOf(10.0), StochRate.total(List.of(rate1, rate2, rate3, rate4)));
        assertEquals(StochRate.valueOf(10.0), StochRate.total(List.of(rate4, rate3, rate2, rate1)));
    }
}
