/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.javaperformance.money;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ratcash
 */
public class SlowArithmeticsCounter {

    private static final Logger LOGGER = Logger.getLogger(SlowArithmeticsCounter.class.getName());
    
    private static final long LOG_COUNT_MASK_EVERY_1024 = 0x3FF;
    
    // must be power of 2 -1
    public static long LOG_COUNT_MASK = LOG_COUNT_MASK_EVERY_1024;
    
    long counter = 0;
    
    public void notify(String name) {
        counter++;
        if(shouldLog(counter, counter)) {
            log(name);
        }
    }
    
    public void notifyAndLog(String name) {
        counter++;
        log(name);
    }
    
    private void log(String name) {
        LOGGER.log(Level.FINE, ()
                -> MessageFormat.format("Slow arithmetic operation at: {0}. Such event already happened {1} times.", name, counter));
    }
    
    static boolean shouldLog(long mask, long count) {
        return ((count) & mask) == 1;
    }

    public long getCount() {
        return counter;
    }

}
