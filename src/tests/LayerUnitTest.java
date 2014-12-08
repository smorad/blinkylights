public class LayerUnitTest {
  // Creates 100x100 pixel layer
  // Displays "test" on layer
  Layer testLayer = new Layer (100, 100, "test");
  
  // Gets int in pixel 50
  int getPixel = testLayer.GetPixelAt (50);
  
  // Sets pixel 50 to white
  testLayer.SetColor(255, 255, 255, getPixel);
  
  
}