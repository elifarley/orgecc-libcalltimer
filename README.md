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

Sample log4j configuration for JBoss:
```xml
<appender name="call-timer" class="org.jboss.logging.appender.DailyRollingFileAppender">
  <param name="File" value="${jboss.server.log.dir}/call-timer.log"/>
  <param name="Append" value="false"/>
  <param name="Threshold" value="DEBUG"/>
  
  <param name="DatePattern" value="'.'yyyy-MM-dd"/>
  <layout class="org.apache.log4j.PatternLayout">
    <param name="ConversionPattern" value="%d{ISO8601}\t%-5p\t[%X{TBID}]\t%m%n"/>
  </layout>
</appender>

[...]

<category name="com.orgecc.calltimer.CallTimer" additivity="false">
  <appender-ref ref="call-timer"/>
  <priority value="DEBUG" />
</category>
```
