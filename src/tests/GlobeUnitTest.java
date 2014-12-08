public class GlobeUnitTest {
  // Initialize and declare testGlobe
  Globe testGlobe = new Globe (100, 100, .1f);
  
  // Adds speed to the rotation of testGlobe
  testGlobe.add_speed (4);
  
  // Sets testGlobe to be completely red
  testGlobe.SetColor (255, 0, 0, 255);
  
  // Sets specific point as white on testGlobe
  testGlobe.SetColorAt (10, 10, 255, 255, 255, 255);
  
  // Writes "sup" on the globe
  testGlobe.set_globe_text ("sup");
  
  // testCoord are testGlobe coordinates when mouse clicked
  Coord testCoord = testGlobe.GetCoord (100, 100);
  
  // Renders testGlobe
  testGlobe.render();
}