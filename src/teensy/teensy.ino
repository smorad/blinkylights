#include <FastLED.h>

#define N_LEDS      34      // vertical pixels
#define TYPE        APA102  // LED chipset
#define BRIGHTNESS  255     // PWM frequency
#define N_PIXELS    180     // horizontal pixels

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
int comm_index;
char string_buf[100];

// sensor
const int sensorPin = 9;
volatile int sensor_flag;

// timing
#define alpha 0.05
int timeNow;
int timeLast;
int rotationTime;
double accumulator;
volatile int refreshInterval;
volatile int sync_flag;
IntervalTimer refreshTimer; // led refresh timer interrupt

// led strip
CRGB leds[N_LEDS];
CRGB img[N_PIXELS][N_LEDS];

volatile int pixel;

void setup() {
  // power-up safety delay
  delay(1000);
  
  // set up photointerrupter sensor
  pinMode(sensorPin, INPUT);
  attachInterrupt(sensorPin, sensorISR, FALLING);

  // start listening to hardware serial 1
  bluetooth.begin(115200);
  
  // set up LED strip
  FastLED.addLeds<TYPE, BGR>(leds, N_LEDS);
  FastLED.setBrightness(BRIGHTNESS);
  FastLED.setCorrection(0xFFFFFF);
  FastLED.setTemperature(0xFFFFFF);
  
  // stripes
  init_leds_stripes();
  for(int i = 0; i < N_LEDS; i++) leds[i] = BLACK;
  FastLED.show();
  
  // init flags
  sensor_flag = 0;
  comm_index = 0;
  rotationTime = 0;
  timeNow = micros();
  timeLast = 0;
  pixel = 0;
  accumulator = 0;
}

void loop() {

  while(bluetooth.available()) {
    int row = comm_index % N_LEDS;
    int col = comm_index / N_LEDS;
    unsigned char b = bluetooth.read();

    if (b == 128) {
      if (comm_index > N_LEDS) {
        String s = String((int)accumulator);
        s.toCharArray(string_buf, 100);
        bluetooth.write("rot time: ");
        bluetooth.write(string_buf);
        s = String(refreshInterval);
        bluetooth.write("\nrot interval: ");
        s.toCharArray(string_buf, 100);
        bluetooth.write(string_buf);
        bluetooth.write("\n");
      }
      comm_index = 0;
    } else {
      img[col][row] = BLACK;
      if (b & 1) img[col][row].r = 255;
      if (b & 2) img[col][row].g = 255;
      if (b & 4) img[col][row].b = 255;
    }
    comm_index++;
  }

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
    if (now - timeLast > 20000) {
      refreshTimer.end();
      timeLast = timeNow;
      timeNow = now;
      rotationTime = timeNow - timeLast;
      // using expodential moving average to compute rolling average
      accumulator = (alpha * rotationTime) + (1.0 - alpha) * accumulator;
      refreshInterval = accumulator / N_PIXELS;

      pixel = 0;
      syncLEDs();
      FastLED.show();

      refreshTimer.begin(refreshISR, refreshInterval);
    }
  }
}

// setup test RGB stripes
void init_leds_stripes() {
  for (int i = 0; i < N_PIXELS; i++) {
    for (int j = 0; j < N_LEDS; j++) {
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
    for (int j = 0; j < N_LEDS; j++) {
      img[i][j] = BLACK;
    }
  }
}

// copy the indexed frame buffer column to the led buffer, 
// then advance the column index
inline void syncLEDs() {
  for (int i = 0; i < N_LEDS; i++) {
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
