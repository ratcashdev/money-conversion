/*
 * Copyright (c) 2018 OmniBene, s.r.o - All Rights Reserved
 * This file is subject to terms and conditions defined in file 'LICENSE.txt'
 * which is part of this source code package.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential. 
 * 
 */
package info.javaperformance.money;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class SlowArithmeticsCounterTest {
    
  
    @Test
    public void testShoudLog() {
        assertFalse(SlowArithmeticsCounter.shouldLog(0x3FF, 0));
        assertTrue(SlowArithmeticsCounter.shouldLog(0x3FF, 1));
        
        assertFalse(SlowArithmeticsCounter.shouldLog(0x3FF, 2));
        assertFalse(SlowArithmeticsCounter.shouldLog(0x3FF, 3));
        assertFalse(SlowArithmeticsCounter.shouldLog(0x3FF, 4));
        assertFalse(SlowArithmeticsCounter.shouldLog(0x3FF, 5));
        assertFalse(SlowArithmeticsCounter.shouldLog(0x3FF, 1023));
        assertFalse(SlowArithmeticsCounter.shouldLog(0x3FF, 1024));
        
        assertTrue(SlowArithmeticsCounter.shouldLog(0x3FF, 1025));
        assertTrue(SlowArithmeticsCounter.shouldLog(0x3FF, 2049));
        assertTrue(SlowArithmeticsCounter.shouldLog(0x3FF, 3073));
        assertTrue(SlowArithmeticsCounter.shouldLog(0x3FF, 4097));
        
        
        assertTrue(SlowArithmeticsCounter.shouldLog(0x1, 1));
        assertFalse(SlowArithmeticsCounter.shouldLog(0x1, 2));
    }

   
    
}
