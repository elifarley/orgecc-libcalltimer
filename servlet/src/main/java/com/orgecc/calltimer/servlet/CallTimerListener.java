package com.orgecc.calltimer.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orgecc.calltimer.CallTimer;
import com.orgecc.calltimer.CallTimerBuilder;
import com.orgecc.calltimer.Ticker;

public class CallTimerListener implements ServletContextListener, HttpSessionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger( CallTimerListener.class );

    private static final String CONTEXT_CALL_TIMER = "context-call-timer";

    private static final String SESSION_CALL_TIMER = "session-call-timer";

    private static CallTimer newCallTimer( final String className, final String methodName ) {
        return new CallTimerBuilder().withTicker( Ticker.WALL_CLOCK ).withLogger( LOGGER ).build()
                .callStart().setCallName( className, methodName );
    }

    @Override
    public void contextInitialized( final ServletContextEvent sce ) {

        sce.getServletContext().setAttribute( CONTEXT_CALL_TIMER,
                newCallTimer( "CONTEXT", sce.getServletContext().getContextPath() ) );

    }

    @Override
    public void contextDestroyed( final ServletContextEvent sce ) {

        final CallTimer callTimer =
                (CallTimer) sce.getServletContext().getAttribute( CONTEXT_CALL_TIMER );

        if ( callTimer != null ) {
            callTimer.callEnd();
            return;
        }

        newCallTimer( "CONTEXT-ERROR", sce.getServletContext().getContextPath() ).callEnd(
                new RuntimeException( "Missing call timer for servlet context" ) );

    }

    @Override
    public void sessionCreated( final HttpSessionEvent hse ) {

        hse.getSession().setAttribute( SESSION_CALL_TIMER,
                newCallTimer( "SESSION", hse.getSession().getId() ) );

    }

    @Override
    public void sessionDestroyed( final HttpSessionEvent hse ) {

        final HttpSession session = hse.getSession();

        final CallTimer callTimer =
                (CallTimer) ( session == null ? null : session.getAttribute( SESSION_CALL_TIMER ) );
        if ( callTimer != null ) {
            callTimer.callEnd();
            return;
        }

        assert callTimer == null;

        final String sessionID = session == null ? "NULL" : session.getId();

        newCallTimer( "SESSION-ERROR", sessionID ).callEnd(
                new RuntimeException( "Missing call timer for session" ) );

    }

}
