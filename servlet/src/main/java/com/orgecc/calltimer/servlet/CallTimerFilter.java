package com.orgecc.calltimer.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orgecc.calltimer.CallTimer;
import com.orgecc.calltimer.CallTimerBuilder;
import com.orgecc.calltimer.Ticker;

public class CallTimerFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger( CallTimerFilter.class );

    @Override
    public void init( final FilterConfig filterConfig ) throws ServletException {
        //
    }

    @Override
    public void doFilter( final ServletRequest request, ServletResponse response,
            final FilterChain chain ) throws IOException, ServletException {

        final CallTimer callTimer =
                new CallTimerBuilder().withTicker( Ticker.CPU ).withLogger( LOGGER ).build()
                        .callStart().setInputSize( request.getContentLength() );

        try {

            final HttpServletRequest httpReq =
                    request instanceof HttpServletRequest ? (HttpServletRequest) request : null;

            final String remoteAddr = request.getRemoteAddr();

            if ( httpReq == null ) {
                callTimer.setCallName( remoteAddr, "-" );

            } else {
                String queryString = httpReq.getQueryString();
                queryString =
                        queryString == null ? "-" : String
                                        .valueOf( queryString.split( "&" ).length );
                callTimer.setCallName( remoteAddr + ":" + queryString, httpReq.getRequestURI() );

            }

            final HttpServletResponse httpRes =
                    response instanceof HttpServletResponse ? (HttpServletResponse) response : null;

            if ( httpRes != null ) {
                response = new com.orgecc.calltimer.servlet.HttpServletResponseWrapper( httpRes );

            }

            callTimer.setThreadDetails();
            chain.doFilter( request, response );

        } catch ( final Exception e ) {
            callTimer.callEnd( e );

        } finally {

            if ( ! ( response instanceof HttpServletResponseWrapper ) ) {
                callTimer.callEnd();
                return;
            }

            final HttpServletResponseWrapper wrappedRes = (HttpServletResponseWrapper) response;
            callTimer.setOutputSize( wrappedRes.getContentLength() );

            if ( wrappedRes.getErrorMsg().length() > 0 ) {
                callTimer.callEnd( new ServletException( wrappedRes.getErrorMsg() ) );

            } else {
                callTimer.callEnd();
            }

        }

    }

    @Override
    public void destroy() {
        //
    }

}
