package com.orgecc.calltimer;

import java.util.Date;

import org.slf4j.Logger;

public final class ErrorOnlyCallTimer extends BaseCallTimer {

    private static final String MSG_FORMAT = "%s\t%s\tE\t%s\t%s\t%s";

    private static final String TO_STRING_FORMAT =
            "%s:%s [%s %s.%s inputSize=%s, outputSize=E, output=, callEnded=%s]";

    ErrorOnlyCallTimer( final Ticker ticker, final Logger logger ) {
        super( ticker, logger );
    }

    @Override
    protected void saveEvent( final Throwable throwable, final String msg ) {
        // Inlined; not used
    }

    @Override
    public void callEnd( final Throwable throwable ) {

        final long durationInMillis = durationInMillis();

        if ( this.callEnded ) {
            return;
        }

        this.callEnded = true;

        if ( throwable == null ) {
            return;
        }

        final String outputInfo = "** " + normalizeOutMessage( throwable.toString() ) + " **";

        final String msg =
                String.format( MSG_FORMAT, durationInMillis, this.inputSize, outputInfo,
                        this.methodName, this.className );

        this.logger.error( msg );

    }

    @Override
    public String toString() {
        return String.format( TO_STRING_FORMAT, this.getClass().getSimpleName(), this.ticker,
                new Date( this.startMillis ), this.className, this.methodName, this.inputSize,
                this.callEnded );
    }

}
