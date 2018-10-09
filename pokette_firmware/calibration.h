#ifndef __PKCALI__
#define __PKCALI__


#define SHOULDER_MIN_EEPROM    0
#define SHOULDER_MAX_EEPROM    4
#define SHOULDER_RANGE_EEPROM  8
#define NECK_MIN_EEPROM        12
#define NECK_MAX_EEPROM        16
#define NECK_RANGE_EEPROM      20
#define CHECKSUM_EEPROM        24

#define CHECKSUM_MAGIC         0x0B1A5E17

void calibrateArm();
void calibrateShoulder();
void calibrateNeck();
bool readCalibration();
uint32_t calculateChecksum();

#endif // __PKCALI__
