package com.orgecc.calltimer.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class HttpServletResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper {

    private int contentLength;

    private String errorMsg = "";

    public int getContentLength() {
        return this.contentLength;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public HttpServletResponseWrapper( final HttpServletResponse response ) {
        super( response );
    }

    @Override
    public void setContentLength( final int len ) {
        this.contentLength = len;
        super.setContentLength( len );
    }

    @Override
    public void setStatus( final int sc ) {
        this.errorMsg = sc >= 500 && sc < 600 ? String.valueOf( sc ).intern() : "";
        super.setStatus( sc );
    };

    @Override
    public void sendError( final int sc ) throws IOException {
        this.errorMsg = String.valueOf( sc ).intern();
        super.sendError( sc );
    }

    @Override
    public void sendError( final int sc, final String msg ) throws IOException {
        this.errorMsg = msg == null ? String.valueOf( sc ).intern() : msg;
        super.sendError( sc, msg );
    }

}
