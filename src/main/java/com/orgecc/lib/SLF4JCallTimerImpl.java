package com.orgecc.lib;

import org.slf4j.Logger;

final class SLF4JCallTimerImpl extends BaseCallTimer {

    private final Logger logger;

    SLF4JCallTimerImpl( final Logger logger ) {
        this.logger = logger;
    }

    @Override
    protected final void saveEvent( final Throwable t, final String msg ) {

        if ( t == null ) {
            this.logger.info( msg );
            return;
        }

        this.logger.error( msg );

    }

}
