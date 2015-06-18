package com.orgecc.calltimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CallTimerBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger( CallTimer.class );

    static {
        BaseCallTimer.saveHeader( LOGGER );
    }

    private Ticker ticker;

    private Logger logger;

    public CallTimerBuilder() {
        withTicker( Ticker.CPU ).withLogger( LOGGER );
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

        if ( this.logger != null && this.logger.isInfoEnabled() ) {
            return new BaseCallTimer( this.ticker, this.logger );
        }

        if ( this.logger != null && this.logger.isErrorEnabled() ) {
            return new ErrorOnlyCallTimer( this.ticker, this.logger );
        }

        return new NoOpCallTimer( this.ticker, this.logger );

    }

}
