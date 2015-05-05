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

    public static final Ticker NO_OP = new Ticker() {

        @Override
        public long read() {
            return 0;
        };

        @Override
        public String toString() {
            return "Ticker.NO_OP";
        }

    };

    public static final Ticker WALL_CLOCK = new Ticker() {

        @Override
        public long read() {
            return System.nanoTime();
        };

        @Override
        public String toString() {
            return "Ticker.WALL_CLOCK";
        }

    };

    public static final Ticker CPU = new Ticker() {

        @Override
        public long read() {
            return LazyThreadMXBeanHolder.TMXB.getCurrentThreadCpuTime();
        };

        @Override
        public String toString() {
            return "Ticker.CPU";
        }

    };

    public abstract long read();

}
