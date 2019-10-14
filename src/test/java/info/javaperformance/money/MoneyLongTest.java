/*
 * Copyright 2019 OmniBene, s.r.o..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.javaperformance.money;

import static org.junit.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 *
 * @author OmniBene, s.r.o.
 */
public class MoneyLongTest {

    @Test
    public void testDivideExact() {
        MoneyLong m = (MoneyLong) MoneyFactory.fromUnits(10675933, 7);
        Money d = MoneyFactory.FIVE;
        Money res = m.divideWithPrecisionGuarantee(d, 7, false);
        // precise result: 0.21351866

        Money expected = MoneyFactory.fromUnits(2135186, 7);
        assertEquals(res, expected);
    }

    @Test
    public void testDivideAllowMore() {
        MoneyLong m = (MoneyLong) MoneyFactory.fromUnits(10675933, 7);
        Money d = MoneyFactory.FIVE;
        Money res = m.divideWithPrecisionGuarantee(d, 7, true);
        // precise result: 0.21351866

        Money expected = MoneyFactory.fromUnits(21351866, 8);
        assertEquals(res, expected);
    }

    @Test
    public void testDivideHigherThanRequestedPrecisionAvailableStrict() {
        MoneyLong m = (MoneyLong) MoneyFactory.fromUnits(10675933, 7);
        Money d = MoneyFactory.FIVE;
        Money res = m.divideWithPrecisionGuarantee(d, 6, false);
        // precise result: 0.21351866

        Money expected = MoneyFactory.fromUnits(213518, 6);
        assertEquals(res, expected);
    }

    @Test
    public void testDivideHigherThanRequestedPrecisionAvailable() {
        MoneyLong m = (MoneyLong) MoneyFactory.fromUnits(10675933, 7);
        Money d = MoneyFactory.FIVE;
        Money res = m.divideWithPrecisionGuarantee(d, 6, true);
        // precise result: 0.21351866

        Money expected = MoneyFactory.fromUnits(21351866, 8);
        assertEquals(res, expected);
    }

}
