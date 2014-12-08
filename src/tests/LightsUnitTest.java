public class LightsUnitTest {
  // Declares test button, red
  color_button button_test = new color_button (255, 0, 0, 255, RED);
  
  // Renders testGlobe
  Globe testGlobe = new Globe (100, 100, .1f);
  globe.render();
  
  // Sets color of pixel where selected on testGlobe
  testGlobe.touchDragged (10, 10, 50);
  
  // Returns false
  boolean testTouchUp = touchUp(10, 10, 50, 5);
  
  // Creates and initializes buttons and canvas
  create();

  // Sends compressed pixmap
  sendFrame();
}