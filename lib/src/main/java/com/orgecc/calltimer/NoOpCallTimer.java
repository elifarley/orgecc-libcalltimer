package com.orgecc.calltimer;

import org.slf4j.Logger;

public final class NoOpCallTimer extends BaseCallTimer {

    NoOpCallTimer( final Ticker ticker, final Logger logger ) {
        super( Ticker.NO_OP, null );
    }

    @Override
    protected final void saveEvent( final Throwable t, final String msg ) {
        // not used
    }

    @Override
    public final void callEnd( final Throwable t ) {
        this.callEnded = true;
    }

}
