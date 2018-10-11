#include "MeMegaPi.h"
#include <EEPROM.h>

#include <SoftwareSerial.h>

#include "motor.h"
#include "calibration.h"

MeMegaPiDCMotor motor1(PORT1B); // Shoulder 
MeMegaPiDCMotor motor2(PORT2B); // Elbow
MeMegaPiDCMotor motor3(PORT3B); // Base 
MeMegaPiDCMotor motor4(12); // Gripper

// Movement ranges from the pots, dummy init values
int shoulderMax   = -1;
int shoulderMin   = -1;
int shoulderRange = -1;
int neckMax       = -1;
int neckMin       = -1;
int neckRange     = -1;


MeBluetooth bluetooth(PORT_5);

/*
 * Setup establishes a serial connection 
 * and calibrates the arm and base if needed
**/
void setup()
{
  Serial.begin(9600); 
  bluetooth.begin(115200);    //The factory default baud rate is 115200
  Serial3.begin(115200); 
  if(!readCalibration()) {
    calibrateArm();
  }
}


bool     deadMan     = false;
uint32_t basePos     = 950;
uint32_t shoulderPos = 400;
uint32_t neckPos     = 180;

void pos_low() {
  shoulderPos = 333;
  neckPos = 46;
}

void pos_mlow() {
  shoulderPos = 559;
  neckPos = 10;
}

void pos_med() {
  shoulderPos = 777;
  neckPos = 14;
}

void pos_mhi() {
  shoulderPos = 892;
  neckPos = 155;
}

void pos_hi() {
  shoulderPos = 1024;
  neckPos = 22;
}
void pos_new() {
  shoulderPos = 769;
  neckPos = 367;
  basePos = 900;
}

void processCommand(const String &data) {
  
    // Activate or disengage motors
    if(data[0] == 'd') {
      deadMan = !deadMan;
      Serial.print("Deadman: ");
      Serial.println(deadMan);
      killMotors();
    } 
    
    // Read the angle values for animation
    else if(data[0] == 'r') { 
      uint16_t shouldr, neck, base_=420;
      shouldr = readShoulder();
      neck    = readNeck();
      base_   = readBase();
      Serial.print(base_);
      Serial.print(", ");
      Serial.print(shouldr);
      Serial.print(", ");
      Serial.println(neck);
      Serial3.print(base_);
      Serial3.print(", ");
      Serial3.print(shouldr);
      Serial3.print(", ");
      Serial3.println(neck);
    } 
    else if(data[0] == 'c' && data.length() >= 3)  {
      int dir = data[2] == '-' ? -1 : 1;
      deadMan = true;
      if(data[1] == 'b') {
        motor3.run(dir * 50);
      }
      if(data[1] == 's') {
        motor1.run(dir * 50);
      }
      if(data[1] == 'e') {
        motor2.run(dir * 50);
      }
    }else if(data[0] == 's') {
      deadMan = true;
      killMotors();
    }
    else if(data[0] == 'm' && data.length() >= 3) {
      int angle = atoi(&data.c_str()[2]);
      angle = angle < 0 ? 0 : (angle > 1023 ? 1023: angle); 
      
      if(data[1] == 'b') {
        basePos = angle;
      }
      if(data[1] == 's') {
        shoulderPos = angle;
      }
      if(data[1] == 'e') {
        neckPos = angle;
      }
    }
    
    // These are some pre-defined positions
    else if (data == "p0") {
      pos_low();
    } else if (data == "p1") {
      pos_mlow();
    } else if (data == "p2") {
      pos_med();
    } else if (data == "p3") {
      pos_mhi();
    } else if (data == "p4") {
      pos_hi();
    } else if (data == "go") {
      openGripper();
    }else if (data == "gc") {
      closeGripper();
    } else if(data == "xxx") {
      Serial.println("Calibrating...");
      Serial3.println("Calibrating...");
      calibrateArm();
    } 
}

void loop()
{
  // First, process commands
  if(Serial.available() > 0) {
    // Grab a line from the serial port
    String data = "";
    char datum = Serial.read();
    while(datum != '\n') {
      data += datum;
      while(Serial.available() <= 0);
      datum = Serial.read();
    }
    processCommand(data);
  }
  
  if(Serial3.available() > 0) {
    // Grab a line from the serial port
    String data = "";
    char datum = Serial3.read();
    unsigned long t = millis();
    while(datum != '\n' && millis() - t < 10) {
      data += datum;
      while(Serial3.available() <= 0);
      datum = Serial3.read();
    }
    processCommand(data);
  }
  
  if(!deadMan) {
    moveShoulderTo(shoulderPos);
    moveNeckTo(neckPos);
    moveBaseTo(basePos);
    moveGripper();
  }
}

