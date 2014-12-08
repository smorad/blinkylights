public class SerialTestUnitTest {
  // test is the port
  SerialPort test;
  
  // Initializes our test port
  test.initialize();
  
  // Sends a byte array over test
  byte[] array = new byte[0];
  test.send (array);
  
  // Tests to see if connected to test
  boolean testConnect = test.isConnected();
  
  // Handles event on test port
  SerialPortEvent event = new SerialPortEvent (test, 0, true, true);
  test.serialEvent (event);
  
  // Closes the port when we're done using it
  test.close();
}