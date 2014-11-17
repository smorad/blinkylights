#include <FastLED.h>
#include <QueueList.h>
#include "image.h"

#define NUM_LEDS    28       // number of RGB LEDs or vertical pixels
#define TYPE        LPD8806  // LED driver model
#define BRIGHTNESS  255      // PWM frequency
#define N_PIXELS    120      // number of horizontal pixels

// color
#define RED     0xFF0000
#define GREEN   0x00FF00
#define BLUE    0x0000FF

#define YELLOW  0xFFFF00
#define MAGENTA 0xFF00FF
#define CYAN    0x00FFFF

#define BLACK   0x000000
#define WHITE   0xFFFFFF

// sensor
const int sensorPin = 9;
volatile int sensor_flag;

// timing
int timeNow;
int timeLast;
int rotationTime;
volatile int refreshInterval;
volatile int sync_flag;
QueueList <int> times;
IntervalTimer refreshTimer; // led refresh timer interrupt

// led strip
CRGB leds[NUM_LEDS];
//CRGB img[N_PIXELS][NUM_LEDS];
volatile int pixel;

// bluetooth
int bttx = 2;
int btrx = 3;






// setup test RGB stripes
void init_leds() {
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


inline void syncLEDs() {
  leds[0] = img[pixel][0];
  leds[1] = img[pixel][27];
  leds[2] = img[pixel][2];
  leds[3] = img[pixel][25];
  leds[4] = img[pixel][4];
  leds[5] = img[pixel][23];
  leds[6] = img[pixel][6];
  leds[7] = img[pixel][21];
  leds[8] = img[pixel][8];
  leds[9] = img[pixel][19];
  leds[10] = img[pixel][10];
  leds[11] = img[pixel][17];
  leds[12] = img[pixel][12];
  leds[13] = img[pixel][15];
  leds[14] = img[(pixel + 15) % N_PIXELS][14];
  leds[15] = img[(pixel + 8) % N_PIXELS][13];
  leds[16] = img[(pixel + 6) % N_PIXELS][16];
  leds[17] = img[(pixel + 5) % N_PIXELS][11];
  leds[18] = img[(pixel + 4) % N_PIXELS][18];
  leds[19] = img[(pixel + 3) % N_PIXELS][9];
  leds[20] = img[(pixel + 3) % N_PIXELS][20];
  leds[21] = img[(pixel + 3) % N_PIXELS][7];
  leds[22] = img[(pixel + 3) % N_PIXELS][22];
  leds[23] = img[(pixel + 4) % N_PIXELS][5];
  leds[24] = img[(pixel + 4) % N_PIXELS][24];
  leds[25] = img[(pixel + 5) % N_PIXELS][3];
  leds[26] = img[(pixel + 6) % N_PIXELS][26];
  leds[27] = img[(pixel + 10) % N_PIXELS][1];
  pixel++;
}

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
  
  //init_leds();
  
  sensor_flag = 0;
  rotationTime = 0;
  timeNow = micros();
  timeLast = 0;
}

void loop() {

  if (sync_flag) {
    sync_flag = 0;
    syncLEDs();
    
    if (pixel == N_PIXELS) { 
      refreshTimer.end();
      pixel = 0;
    }
  }
  
  // compute time for one rotation and LED refresh interval
  if (sensor_flag) {
    pixel = 0;
    syncLEDs();
    refreshISR();
    refreshTimer.begin(refreshISR, refreshInterval);
    
    sensor_flag = 0;
    timeLast = timeNow;
    timeNow = micros();
    rotationTime = timeNow - timeLast;
    if (timeNow - timeLast > 3000)
      times.push(rotationTime);
    if (times.count() > 100) 
      times.pop();
    
    refreshInterval = (times.sum() / times.count()) / N_PIXELS;
  }

	//check bt buffer for data, move it to our arrays
	//no need to update LEDs, should sync upon sensor flag
	if(bluetooth.available()){
		for(i=0; i<sizeof(img); i++){
			img[i] = serial.read();
		}
		//if buffer isn't empty, we fucked up
		//lets empty it and await the next transfer
		while(serial.read());
	}  
}


// refresh the LEDs
void refreshISR (void) {
  FastLED.show();
  
  sync_flag = 1;
}


// set sensor flag
void sensorISR (void) {
    sensor_flag = 1;
    
    // disable the refresh interrupt
    refreshTimer.end();
}
