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


bool readCalibration() {
  uint32_t checksum = 0;
  EEPROM.get(SHOULDER_MIN_EEPROM,   shoulderMin);
  EEPROM.get(SHOULDER_MAX_EEPROM,   shoulderMax);
  EEPROM.get(SHOULDER_RANGE_EEPROM, shoulderRange);
  
  EEPROM.get(NECK_MIN_EEPROM,   neckMin);
  EEPROM.get(NECK_MAX_EEPROM,   neckMax);
  EEPROM.get(NECK_RANGE_EEPROM, neckRange);
  EEPROM.get(CHECKSUM_EEPROM, checksum);
  return checksum == calculateChecksum();
}


/**
 * Moves arm at a safe speed to 
 */
void calibrateArm() {
  // First find the min and max values for the shoulder
  calibrateShoulder();

  // Then move it into a safe location for the neck to move
  do{
    moveShoulderTo(488);
  }while(fabs(readShoulder() - 488.) > 5.);
  
  motor1.stop();
  motor2.stop();
  
  delay(1000);
  
  calibrateNeck(); 
  do{
    moveNeckTo(150);
  }while(fabs(readNeck() - 150.) > 5.);
  
  motor1.stop();
  motor2.stop();
  
  // Update the values in the EEPROM
  EEPROM.put(SHOULDER_MIN_EEPROM,   shoulderMin);
  EEPROM.put(SHOULDER_MAX_EEPROM,   shoulderMax);
  EEPROM.put(SHOULDER_RANGE_EEPROM, shoulderRange);
  
  EEPROM.put(NECK_MIN_EEPROM,   neckMin);
  EEPROM.put(NECK_MAX_EEPROM,   neckMax);
  EEPROM.put(NECK_RANGE_EEPROM, neckRange);
  EEPROM.put(CHECKSUM_EEPROM, calculateChecksum());
}


uint32_t calculateChecksum() {
  uint32_t chk = neckMin + neckMax + neckRange + shoulderMin + shoulderMax + shoulderRange;
  return chk ^ CHECKSUM_MAGIC;
}

void calibrateShoulder() {
  motor1.run(-SAFE_SHOULDER_SPEED); /* value: between -255 and 255. */
  delay(5000);
  shoulderMax = analogRead(SHOULDER_ANALOG_PIN);
  
  motor1.run(SAFE_SHOULDER_SPEED); /* value: between -255 and 255. */
  delay(5000);
  shoulderMin = analogRead(SHOULDER_ANALOG_PIN);
  
  shoulderRange = shoulderMax - shoulderMin;
  motor1.stop();
}


void calibrateNeck() {
  motor2.run(-SAFE_NECK_SPEED); /* value: between -255 and 255. */
  delay(5000);
  neckMin = analogRead(NECK_ANALOG_PIN);
  
  motor2.run(SAFE_NECK_SPEED); /* value: between -255 and 255. */
  delay(5000);
  neckMax = analogRead(NECK_ANALOG_PIN);
  
  neckRange = neckMax - neckMin;
  Serial.println(neckRange);             // debug value
  motor2.stop();//run(mspeed); /* value: between -255 and 255. */
}

