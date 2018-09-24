# ShoulderParrot
UIST 2018 student competition -- Makeblock Ultimate 2.0 kit

# Bluetooth Communication
To get voice to text info from bluetooth using Android Voice Control >> use Serial3 
> ex.
> //if Serial3 contains more than 0 bytes, print contents of Serial3 over Serial port

> if (Serial3.available()) { Serial.println(Serial3.readString());}
