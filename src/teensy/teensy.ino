#include <FastLED.h>

#define NUM_LEDS    34      // number of RGB LEDs or vertical pixels
#define TYPE        APA102  // LED chipset
#define BRIGHTNESS  255     // PWM frequency
#define N_PIXELS    120     // number of horizontal pixels

// color
#define RED     0xFF0000
#define GREEN   0x00FF00
#define BLUE    0x0000FF

#define YELLOW  0xFFFF00
#define MAGENTA 0xFF00FF
#define CYAN    0x00FFFF

#define BLACK   0x000000
#define WHITE   0xFFFFFF

//bluetooth
#define bluetooth Serial1

// sensor
const int sensorPin = 9;
volatile int sensor_flag;

// timing
#define alpha 0.1
int timeNow;
int timeLast;
int rotationTime;
int accumulator;
volatile int refreshInterval;
volatile int sync_flag;
IntervalTimer refreshTimer; // led refresh timer interrupt

// led strip
CRGB leds[NUM_LEDS];
CRGB img[N_PIXELS][NUM_LEDS];
volatile int pixel;

void setup() {
  // power-up safety delay
  delay(500);
  
  // set up photointerrupter sensor
  pinMode(sensorPin, INPUT);
  attachInterrupt(sensorPin, sensorISR, FALLING);
  
  // set up LED strip
  FastLED.addLeds<TYPE, BRG>(leds, NUM_LEDS);
  FastLED.setBrightness(BRIGHTNESS);
  FastLED.setCorrection(0xFFFFFF);
  FastLED.setTemperature(0xFFFFFF);
  
  // stripes
  init_leds_stripes();
  
  // init flags
  sensor_flag = 0;
  rotationTime = 0;
  timeNow = micros();
  timeLast = 0;
  pixel = 0;
  accumulator = 0;
}

void loop() {

  // sync leds buffer and refresh
  if (sync_flag) {
    sync_flag = 0;
    syncLEDs();
    FastLED.show();
  }
  
  // compute time for one rotation and LED refresh interval
  if (sensor_flag) {
    
    sensor_flag = 0;
    int now = micros();
    if (now - timeLast > 30000) {
      refreshTimer.end();
      pixel = 0;
      syncLEDs();
      FastLED.show();
      timeLast = timeNow;
      timeNow = now;
      rotationTime = timeNow - timeLast;
      // using expodential moving average to compute rolling average
      accumulator = (alpha * rotationTime) + (1.0 - alpha) * accumulator;
      refreshInterval = accumulator / N_PIXELS;
      refreshTimer.begin(refreshISR, refreshInterval);
    }
  }
}

// setup test RGB stripes
void init_leds_stripes() {
  for (int i = 0; i < N_PIXELS; i++) {
    for (int j = 0; j < NUM_LEDS; j++) {
      switch (i / (N_PIXELS / 6)) {
        case 0: img[i][j] = RED; break;
        case 1: img[i][j] = BLUE; break;
        case 2: img[i][j] = GREEN; break;
        case 3: img[i][j] = RED; break;
        case 4: img[i][j] = BLUE; break;
        default: img[i][j] = GREEN; break;
      }
    }
  }
}

// set all led color values to 0
void init_leds_black() {
  for (int i = 0; i < N_PIXELS; i++) {
    for (int j = 0; j < NUM_LEDS; j++) {
      img[i][j] = BLACK;
    }
  }
}

// copy the indexed frame buffer column to the led buffer, 
// then advance the column index
inline void syncLEDs() {
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = img[pixel % N_PIXELS][i];  
  }
  pixel++;
}

// refresh the LEDs
void refreshISR (void) {
  sync_flag = 1;
}

// set sensor flag
void sensorISR (void) {
  sensor_flag = 1;
}
