package com.orgecc.lib;

public interface CallTimer {

    CallTimer callStart();

    CallTimer callStart( byte[] b );

    CallTimer callStart( long inputSize );

    CallTimer setCallName( String className, String methodName, int paramCount );

    CallTimer setCallName( String className, String methodName );

    CallTimer setInputSize( long inputSize );

    CallTimer setOutputSize( long outputSize );

    void callEnd();

    void callEnd( byte[] output );

    void callEnd( long outputSize );

    void callEnd( Object output );

    void callEnd( Throwable t );

}
