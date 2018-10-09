#ifndef __PKMOTOR__
#define __PKMOTOR__

#define SHOULDER_ANALOG_PIN  8
#define NECK_ANALOG_PIN      9
#define BASE_ANALOG_PIN      11
#define SAFE_SHOULDER_SPEED  150
#define SAFE_NECK_SPEED      150


uint16_t readShoulder();
uint16_t readNeck();
uint16_t readBase();


void moveShoulderTo(const uint16_t &resistance);
void moveNeckTo(const uint16_t &resistance);
void moveBaseTo(const uint16_t &resistance);
void killMotors();

void moveGripper();
void openGripper();
void closeGripper();
 
#endif // __PKMOTOR
