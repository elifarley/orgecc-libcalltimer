# orgecc-libcalltimer

Usage example:
```Java
CallTimer timer = this.sessionInfo.getTimer().callStart();

// [...find out className, methodName and paramCount...]

timer.setCallName( className, methodName, paramCount );

try {
  // [...perform call...]
  
  getTimer().callEnd( serializedResponse.length() );

} catch( Exception e ) {
  timer.callEnd( e );

}
```
