package com.orgecc.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CallTimerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger( CallTimer.class );

    static {
        saveSLF4JHeader( LOGGER );
    }

    private CallTimerFactory() {
        // Utility class
    }

    public static void saveSLF4JHeader( final Logger logger ) {
        logger.warn( BaseCallTimer.HEADER );
    }

    public static CallTimer newSLF4JCallTimer() {
        return newSLF4JCallTimer( LOGGER );
    }

    public static CallTimer newSLF4JCallTimer( final Logger logger ) {
        return new SLF4JCallTimerImpl( logger );
    }

}
