
int hallPin = 1;
int hallValue = 0;
int analogPin = 11;

void setup() {
  pinMode(analogPin, OUTPUT);
  pinMode(hallPin, INPUT);
}

void loop() {        
  // map it to the range of the analog out:
  //outputValue = map(sensorValue, 0, 1023, 0, 255);

  //hallValue = digitalRead(hallPin);

  if (1) {
    analogWrite(analogPin, 255);
  }
  else {
    analogWrite(analogPin, 0);
  }
  delay (1000);
}