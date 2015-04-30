package com.orgecc.calltimer;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;

class BaseCallTimer implements CallTimer {

    static final String HEADER =
            "YYYY-MM-DD HH:mm:ss.sss\tlevel\tTBID\tms\tinsize\toutsize\touttype\tmethod\tclass";

    private static final String MSG_FORMAT = "%s\t%s\t%s\t%s\t%s\t%s";

    private static Logger saveHeader( final Logger logger ) {
        logger.warn( HEADER );
        return logger;
    }

    private static String normalizeOutMessage( final String s ) {
        return s.replace( '\t', ' ' );
    }

    private long startNanos;

    private long startMillis;

    private String className;

    private String methodName;

    private long inputSize;

    private String outputSize;

    private Object output;

    private boolean callEnded;

    private final Ticker ticker;

    private final Logger logger;

    BaseCallTimer( final Ticker ticker, final Logger logger ) {
        this.ticker = ticker;
        this.logger = saveHeader( logger );
    }

    protected final void saveEvent( final Throwable t, final String msg ) {

        if ( t == null ) {
            this.logger.info( msg );
            return;
        }

        this.logger.error( msg );

    }

    public CallTimer callStart() {
        return callStart( 0 );
    }

    public CallTimer callStart( final byte[] b ) {
        return callStart( b == null ? 0 : b.length );
    }

    public CallTimer callStart( final long inputSize ) {
        this.startNanos = this.ticker.read();
        this.startMillis = System.currentTimeMillis();
        this.inputSize = inputSize;
        this.outputSize = null;
        this.output = null;
        this.callEnded = false;
        return setCallName( null, null );
    }

    public CallTimer setCallName( final String className, final String methodName,
            final int paramCount ) {
        return setCallName( className, methodName + "#" + paramCount );
    }

    public CallTimer setCallName( final String className, final String methodName ) {
        this.className = className == null ? "-" : className.replace( ' ', '-' );
        this.methodName = methodName == null ? "-" : methodName.replace( ' ', '-' );
        return this;
    }

    public CallTimer setInputSize( final long inputSize ) {
        this.inputSize = inputSize;
        return this;
    }

    public CallTimer setOutputSize( final long outputSize ) {
        this.outputSize = Long.toString( outputSize );
        return this;
    }

    private CallTimer setOutput( final Object output ) {
        this.output = output;
        return this;

    }

    private String normalizeOutputAndSize() {

        if ( this.output == null ) {

            if ( this.outputSize == null ) {
                this.outputSize = "-";
                return "NULL";
            }

            // this.outputSize != null
            // = method returned a serialized representation of the result class
            return "SER";

        }

        final String result = normalizeOutMessage( this.output.getClass().getName() );

        final Integer size = getSize( result );
        this.outputSize = size == null ? "?" : size.toString();

        return result;

    }

    public void callEnd( final Throwable t ) {

        final long durationInMillis = ( this.ticker.read() - this.startNanos ) / 1000000;

        final String outputInfo;

        if ( t == null ) {
            outputInfo = normalizeOutputAndSize();

        } else {
            outputInfo = "** " + normalizeOutMessage( t.toString() ) + " **";
            this.outputSize = "E";

        }
        this.output = null;

        final String msg =
                String.format( MSG_FORMAT, durationInMillis, this.inputSize, this.outputSize,
                        outputInfo, this.methodName, this.className );

        saveEvent( t, msg );

        this.callEnded = true;

    }

    public void callEnd() {
        callEnd( (Throwable) null );
    }

    public void callEnd( final byte[] output ) {
        callEnd( output == null ? 0 : output.length );
    }

    public void callEnd( final long outputSize ) {
        setOutputSize( outputSize ).callEnd();
    }

    public void callEnd( final Object output ) {
        setOutput( output ).callEnd();
    }

    @Override
    public String toString() {
        final String format = "%s [%s %s.%s inputSize=%s, outputSize=%s, output=%s, callEnded=%s]";
        return String.format( format, this.getClass().getSimpleName(),
                new Date( this.startMillis ), this.className, this.methodName, this.inputSize,
                this.outputSize, this.output, this.callEnded );
    }

    @Override
    protected void finalize() throws Throwable {
        if ( !this.callEnded ) {
            callEnd( new Throwable( "CALL NOT ENDED" ) );
        }
        super.finalize();
    }

    @SuppressWarnings( "rawtypes" )
    private static Integer getSize( final Object output ) {

        if ( output instanceof String ) {
            return ( (String) output ).length();
        }

        if ( output instanceof Collection ) {
            return ( (Collection) output ).size();
        }

        if ( output instanceof Map ) {
            return ( (Map) output ).size();
        }

        if ( output.getClass().isArray() ) {
            return Array.getLength( output );
        }

        return null;
    }

}
