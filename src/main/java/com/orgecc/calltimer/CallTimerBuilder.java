package com.orgecc.calltimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CallTimerBuilder {

    private static final class LazyCallTimerHolder {

        static final Logger LOGGER = LoggerFactory.getLogger( CallTimer.class );
    }

    private Ticker ticker;

    private Logger logger;

    public CallTimerBuilder() {
        withTicker( Ticker.CPU ).withLogger( LazyCallTimerHolder.LOGGER );
    }

    public CallTimerBuilder withTicker( final Ticker ticker ) {
        this.ticker = ticker;
        return this;
    }

    public CallTimerBuilder withLogger( final Logger logger ) {
        this.logger = logger;
        return this;
    }

    public CallTimer build() {
        return new BaseCallTimer( this.ticker, this.logger );
    }

}
