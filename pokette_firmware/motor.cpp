#include <EEPROM.h>
#include "calibration.h"
#include "motor.h"
#include "MeMegaPiDCMotor.h"

extern MeMegaPiDCMotor motor1; // Shoulder 
extern MeMegaPiDCMotor motor2; // Elbow
extern MeMegaPiDCMotor motor3; // Base 
extern MeMegaPiDCMotor motor4; // Gripper

extern int shoulderMax;
extern int shoulderMin;
extern int shoulderRange;
extern int neckMax;
extern int neckMin;
extern int neckRange;


void killMotors() {
  motor1.stop();
  motor2.stop();
  motor3.stop();
  motor4.stop();
}
uint16_t readShoulder(){
  int val = analogRead(SHOULDER_ANALOG_PIN);  
  int ret = 1023*((float(val-shoulderMin))/float(shoulderRange)); 
  return ret < 0 ? 0 : ret;
}

uint16_t readNeck(){
  int val = analogRead(NECK_ANALOG_PIN);  
  int ret = 1023*((float(val-neckMin))/float(neckRange)); 
  return ret < 0 ? 0 : ret;
}


uint16_t readBase(){
  int val = analogRead(BASE_ANALOG_PIN);  
  return val;
}

void moveShoulderTo(const uint16_t &resistance) {
  static int last_time = -1;
  
  int val = readShoulder(); 
  int difference = val - (int)resistance;
  int mspeed = 0;

  // Time delta == -1 means that we're entering this for the first time
  if(last_time == -1) {
      last_time = millis();
      mspeed = difference*20;
  } else {
      int this_time = millis();

      mspeed = difference*20;
      
      last_time = this_time;
  }
  mspeed = mspeed > 255? 255: (mspeed < -255 ? -255 : mspeed);
  
  motor1.run(mspeed); /* value: between -255 and 255. */
}

void moveNeckTo(const uint16_t &resistance) {
  static int last_time = -1;
  
  int val = readNeck(); 
  int difference = val - (int)resistance;
  int mspeed = 0;

  // Time delta == -1 means that we're entering this for the first time
  if(last_time == -1) {
      last_time = millis();
      mspeed = difference*20;
  } else {
      int this_time = millis();

      mspeed = difference*20;
      
      last_time = this_time;
  }
  mspeed = mspeed > 255? 255: (mspeed < -255 ? -255 : mspeed);
  
  motor2.run(-mspeed); /* value: between -255 and 255. */
}

void moveBaseTo(const uint16_t &resistance) {
  
  static int last_time = -1;
  
  int val = readBase(); 
  int difference = val - (int)resistance;
  int mspeed = 0;

  // Time delta == -1 means that we're entering this for the first time
  if(last_time == -1) {
      last_time = millis();
      mspeed = difference*10;
  } else {
      int this_time = millis();

      mspeed = difference*10;
      
      last_time = this_time;
  }
  mspeed = mspeed > 255? 255: (mspeed < -255 ? -255 : mspeed);
  
  motor3.run(-mspeed); /* value: between -255 and 255. */
}

unsigned long gripper_time = -1;
bool gripper_closing = true;

void moveGripper() {
  if(gripper_time != -1 && millis() - gripper_time < 1800 ) {
    motor4.run((gripper_closing ?-1:1)*255);
    
  } else {
    motor4.stop();
    gripper_time = -1;
  }
}
void openGripper() {
  if(gripper_time == -1) {
    gripper_closing = false;
    gripper_time = millis();
  }
}

void closeGripper() {
  if(gripper_time == -1) {
    gripper_closing = true;
    gripper_time = millis();
  }
}


