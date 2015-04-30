package com.orgecc.calltimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CallTimerFactory {

    private static final class LazyCallTimerHolder {

        static final Logger LOGGER = LoggerFactory.getLogger( CallTimer.class );

        static final CallTimer SLF4J_CALLTIMER = newSLF4JCallTimer( saveSLF4JHeader( LOGGER ) );

    }

    private CallTimerFactory() {
        // Utility class
    }

    public static Logger saveSLF4JHeader( final Logger logger ) {
        logger.warn( BaseCallTimer.HEADER );
        return logger;
    }

    public static CallTimer newSLF4JCallTimer() {
        return LazyCallTimerHolder.SLF4J_CALLTIMER;
    }

    public static CallTimer newSLF4JCallTimer( final Logger logger ) {
        return new SLF4JCallTimerImpl( logger );
    }

}
