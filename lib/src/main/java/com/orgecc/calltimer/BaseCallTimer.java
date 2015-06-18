package com.orgecc.calltimer;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;

class BaseCallTimer implements CallTimer {

    private static final String HEADER =
            "YYYY-MM-DD HH:mm:ss.sss\tlevel\tTBID\tms\tinsize\toutsize\touttype\tmethod\tclass";

    private static final String MSG_FORMAT = "%s\t%s\t%s\t%s\t%s\t%s";

    private static final String TO_STRING_FORMAT =
            "%s:%s [%s %s.%s inputSize=%s, outputSize=%s, output=%s, callEnded=%s]";

    private static final String THREAD_NAME_FORMAT = "%s(%s.%s%s) %s [%s]";

    private static final String TYPE_PREFIX_TO_OMIT = "java.lang.";

    private static final int NO_THREAD = -1;

    static Logger saveHeader( final Logger logger ) {
        logger.warn( HEADER );
        return logger;
    }

    static String normalizeOutMessage( final String str ) {
        return ( str.startsWith( TYPE_PREFIX_TO_OMIT ) ? str.substring( TYPE_PREFIX_TO_OMIT
                .length() ) : str ).replace( '\t', ' ' );
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

    long startNanos;

    long startMillis;

    String className;

    String methodName;

    long inputSize;

    private String outputSize;

    private Object output;

    boolean callEnded;

    final transient Ticker ticker;

    final transient Logger logger;

    private transient long originalThreadId = NO_THREAD;

    private transient String originalThreadName;

    BaseCallTimer( final Ticker ticker, final Logger logger ) {
        this.ticker = ticker;
        this.logger = logger;
    }

    void saveEvent( final Throwable throwable, final String msg ) {

        if ( throwable == null ) {
            this.logger.info( msg );
            return;
        }

        this.logger.error( msg );

    }

    public final CallTimer callStart() {
        return callStart( 0 );
    }

    public final CallTimer callStart( final byte[] bytes ) {
        return callStart( bytes == null ? 0 : bytes.length );
    }

    public final CallTimer callStart( final long inputSize ) {
        this.startNanos = this.ticker.read();
        this.startMillis = System.currentTimeMillis();
        this.inputSize = inputSize;
        this.outputSize = null;
        this.output = null;
        this.callEnded = false;
        return setCallName( null, null );
    }

    public final CallTimer setCallName( final String className, final String methodName,
            final int paramCount ) {
        return setCallName( className, methodName + "#" + paramCount );
    }

    public final CallTimer setCallName( final String className, final String methodName ) {
        this.className = className == null ? "-" : className.replace( ' ', '-' );
        this.methodName = methodName == null ? "-" : methodName.replace( ' ', '-' );
        return this;
    }

    public final CallTimer setInputSize( final long inputSize ) {
        this.inputSize = inputSize;
        return this;
    }

    public final CallTimer setOutputSize( final long outputSize ) {
        this.outputSize = Long.toString( outputSize );
        return this;
    }

    public final CallTimer setThreadDetails() {
        return setThreadDetails( null );
    }

    public final CallTimer setThreadDetails( final String extraDetails ) {

        final Thread currentThread = Thread.currentThread();
        if ( NO_THREAD == this.originalThreadId ) {
            this.originalThreadId = currentThread.getId();
            this.originalThreadName = currentThread.getName();

        } else if ( this.originalThreadId != currentThread.getId() ) {
            this.logger.error( String.format( "Expected thread ID: %s; name: %s; details: '%s'",
                    this.originalThreadId, this.originalThreadName, extraDetails ) );
        }

        assert currentThread.getId() == this.originalThreadId : String.format(
                "Thread ID mismatch: expected '%s'; actual '%s'", this.originalThreadId,
                currentThread.getId() );

        final String name =
                String.format( THREAD_NAME_FORMAT, extraDetails == null ? "" : extraDetails + "; ",
                        this.className, this.methodName,
                        this.inputSize == 0 ? "" : ":" + String.valueOf( this.inputSize ),
                                new Date( this.startMillis ), this.originalThreadName );

        Thread.currentThread().setName( name );
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

            assert this.outputSize != null;
            // = method returned a serialized representation of the result class
            return "SER";

        }

        assert this.output != null;

        final String result = normalizeOutMessage( this.output.getClass().getName() );

        final Integer size = getSize( this.output );
        this.outputSize = size == null ? "?" : size.toString();

        return result;

    }

    final long durationInMillis() {
        return ( this.ticker.read() - this.startNanos ) / 1000000;
    }

    public void callEnd( final Throwable throwable ) {

        final long durationInMillis = durationInMillis();

        if ( this.callEnded ) {
            return;
        }

        final String outputInfo;

        if ( throwable == null ) {
            outputInfo = normalizeOutputAndSize();

        } else {
            outputInfo = "** " + normalizeOutMessage( throwable.toString() ) + " **";
            this.outputSize = "E";

        }
        this.output = null;

        final String msg =
                String.format( MSG_FORMAT, durationInMillis, this.inputSize, this.outputSize,
                        outputInfo, this.methodName, this.className );

        saveEvent( throwable, msg );

        this.callEnded = true;
        restoreThreadName();

    }

    protected final void restoreThreadName() {

        if ( NO_THREAD == this.originalThreadId ) {
            // Method 'setThreadDetails' hasn't been called
            return;
        }

        final Thread currentThread = Thread.currentThread();
        if ( currentThread.getId() == this.originalThreadId ) {
            currentThread.setName( this.originalThreadName );
            this.originalThreadId = NO_THREAD;
            this.originalThreadName = null;
            return;
        }

        this.logger.error( String.format( "Expected thread ID: %s; actual: %s; original name: %s",
                this.originalThreadId, currentThread.getId(), this.originalThreadName ) );
    }

    public final void callEnd() {
        callEnd( (Throwable) null );
    }

    public final void callEnd( final byte[] output ) {
        callEnd( output == null ? 0 : output.length );
    }

    public final void callEnd( final long outputSize ) {
        setOutputSize( outputSize ).callEnd();
    }

    public final void callEnd( final Object output ) {
        setOutput( output ).callEnd();
    }

    @Override
    public String toString() {
        return String.format( TO_STRING_FORMAT, this.getClass().getSimpleName(), this.ticker,
                new Date( this.startMillis ), this.className, this.methodName, this.inputSize,
                this.outputSize, this.output, this.callEnded );
    }

    @Override
    protected final void finalize() throws Throwable {
        if ( !this.callEnded ) {
            callEnd( new Throwable( "CALL NOT ENDED" ) );
        }
        super.finalize();
    }

}
