# orgecc-libcalltimer

Usage example:
```Java
CallTimer timer = new CallTimerBuilder().withTicker( Ticker.CPU ).build().callStart();

// [...find out className, methodName and paramCount...]

timer.setCallName( className, methodName, paramCount );

try {
  // [...perform call...]
  
  getTimer().callEnd( serializedResponse.length() );

} catch( Exception e ) {
  timer.callEnd( e );

}
```

Note: In order to register the servlet listener and filter to all web apps,
please edit $JBOSS_HOME/server/default/deploy/jboss-web.deployer/conf/web.xml


Sample log4j configuration for JBoss:
```xml
   <appender name="call-timer" class="org.jboss.logging.appender.DailyRollingFileAppender">
      <param name="File" value="${jboss.server.log.dir}/call-timer.log"/>
      <param name="Append" value="false"/>
      <param name="Threshold" value="DEBUG"/>

      <param name="DatePattern" value="'.'yyyy-MM-dd"/>
      <layout class="org.apache.log4j.PatternLayout">
         <!-- The default pattern: Date Priority [Category] Message\n -->
         <param name="ConversionPattern" value="%d{ISO8601}\t%-5p\t[%X{TBID}]\t%m%n"/>
      </layout>
   </appender>

   <category name="com.orgecc.calltimer.CallTimer" additivity="false">
     <priority value="DEBUG" />
     <appender-ref ref="call-timer"/>
   </category>

   <appender name="call-timer-listener" class="org.jboss.logging.appender.DailyRollingFileAppender">
      <param name="File" value="${jboss.server.log.dir}/call-timer-listener.log"/>
      <param name="Append" value="false"/>
      <param name="Threshold" value="DEBUG"/>

      <param name="DatePattern" value="'.'yyyy-MM-dd"/>
      <layout class="org.apache.log4j.PatternLayout">
         <!-- The default pattern: Date Priority [Category] Message\n -->
         <param name="ConversionPattern" value="%d{ISO8601}\t%-5p\t[%X{TBID}]\t%m%n"/>
      </layout>
   </appender>

   <appender name="call-timer-filter" class="org.jboss.logging.appender.DailyRollingFileAppender">
      <param name="File" value="${jboss.server.log.dir}/call-timer-filter.log"/>
      <param name="Append" value="false"/>
      <param name="Threshold" value="DEBUG"/>

      <param name="DatePattern" value="'.'yyyy-MM-dd"/>
      <layout class="org.apache.log4j.PatternLayout">
         <!-- The default pattern: Date Priority [Category] Message\n -->
         <param name="ConversionPattern" value="%d{ISO8601}\t%-5p\t[%X{TBID}]\t%m%n"/>
      </layout>
   </appender>

   <category name="com.orgecc.calltimer.servlet.CallTimerListener" additivity="false">
     <priority value="DEBUG" />
     <appender-ref ref="call-timer-listener"/>
   </category>

   <category name="com.orgecc.calltimer.servlet.CallTimerFilter" additivity="false">
     <priority value="DEBUG" />
     <appender-ref ref="call-timer-filter"/>
   </category>
```
