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
import static org.junit.Assert.assertTrue;
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

    @Test
    public void testMultiplyWithZero1() {
        MoneyLong m = (MoneyLong) MoneyFactory.fromUnits(123456789123L, 7);
        Money d = MoneyFactory.ZERO;
        Money res = m.multiplyLimitedScale(d, 7);
        assertEquals(res, MoneyFactory.ZERO);
    }

    @Test
    public void testMultiplyWithZero2() {
        Money p = MoneyFactory.fromString("0.00266039");
        Money q = MoneyFactory.fromString("32824.977");
        Money pct = MoneyFactory.fromString("0.0000");

        Money cost = p.multiplyLimitedScale(q,8);
        Money res = cost.multiplyLimitedScale(pct, 8);
        assertEquals(res, MoneyFactory.ZERO);
    }

    @Test
    public void testMultiplyWithZero3() {
        Money p = MoneyFactory.fromString("123456789123");
        Money q = MoneyFactory.ZERO;

        Money cost = p.multiplyLimitedPrecision(q,8);
        assertEquals(cost, MoneyFactory.ZERO);
    }
    
    @Test
    public void testMultiplicationWithOverflowResultsInMoneyLong() {
        Money a = MoneyFactory.fromString("94390.46071144");
        assertTrue(a instanceof MoneyLong);
        Money b = MoneyFactory.fromString("0.87999998");
        assertTrue(b instanceof MoneyLong);
        short scale = 8;
        // exact value:   83063.6035382579857712
        // using scale=8: 83063.60353825
        Money result = a.multiplyLimitedScale(b, scale);
        assertTrue(result instanceof MoneyLong);
        assertEquals(result, MoneyFactory.fromString("83063.60353825"));
    }
}
