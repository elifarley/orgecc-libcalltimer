package com.orgecc.calltimer;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public abstract class Ticker {

    private static final class LazyThreadMXBeanHolder {

        static final ThreadMXBean TMXB = ManagementFactory.getThreadMXBean();

        static {
            if ( !TMXB.isCurrentThreadCpuTimeSupported() ) {
                throw new AssertionError( "isCurrentThreadCpuTimeSupported == false" );
            }
        }
    }

    public static final Ticker SYSTEM = new Ticker() {

        @Override
        public long read() {
            return System.nanoTime();
        };
    };

    public static final Ticker CPU = new Ticker() {

        @Override
        public long read() {
            return LazyThreadMXBeanHolder.TMXB.getCurrentThreadCpuTime();
        };
    };

    public abstract long read();

}
