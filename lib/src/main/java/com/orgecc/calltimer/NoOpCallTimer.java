package com.orgecc.calltimer;

import org.slf4j.Logger;

public final class NoOpCallTimer extends BaseCallTimer {

    NoOpCallTimer( final Ticker ticker, final Logger logger ) {
        super( Ticker.NO_OP, null );
    }

    @Override
    protected void saveEvent( final Throwable throwable, final String msg ) {
        // not used
    }

    @Override
    public void callEnd( final Throwable throwable ) {
        this.callEnded = true;
        restoreThreadName();
    }

}
