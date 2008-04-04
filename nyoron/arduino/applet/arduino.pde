
//These are codes that will be recieved via UART from the robot.
/* Range in centimeters = (val * 1,24) */

 /*
 I, Benjamin, would appreciate code that would function like this:
The arduino should probably work like so,
buttonsOn is a boolean that defaults to true.
sent is a boolean that defaults to false.

if a push button sensor is pressed and buttonsOn == true and sent == false
	send a one to output.
	set some boolean sent = true;
if a push button sensor  depressed and buttonsOn == true and sent == true
	set some boolean sent = false;
if a stop code (say a byte 0x01) is received
	set boolean "buttonsOn" = false
if a start code (say a byte 0x02) is received
	set boolean "buttonsOn" = true
if a IRget code (say a byte 0x03) is received
	send the reading from the IR sensor.
*/

//There is a possibility we don't need this include
#include <SoftwareSerial.h>

//These are codes that will be recieved via UART from the robot.

#define GET_DATA 'd'
#define DATA_DELIMITER ','

int irRange = 0;       // signal pin ANALOG-0 for Range finder
int bump0 = 7;               // signal pin DIGITAL-1 for bump sensor
int irRangeData = 0;
int bumpData = 0;
int BAUD = 9600;
int rangeCM = 0; 

//The value read from the serial connection.
char input;


void setup() {

  input = 0;
  beginSerial(BAUD);
  pinMode(irRange, OUTPUT);  
  pinMode(bump0, OUTPUT);  
  //mySerial.begin(BAUD);
  
}

void loop() {

  //Check if a command has been sent.
  if (Serial.available() > 0 ){
    input =  Serial.read();
  }
  else{
    input = 0;
  }
  

  //Handle the command.
  //This might be better if re-written as a switch
  //statement.
  
  /*
  if(input == BUMPER_STOP){
    buttonsOn = false;
  }
  else if (input == BUMPER_START){
    buttonsOn = true;
  }
  else if (input == IR_GET){
    irRangeValue = analogRead(irRangeSignal); 
    Serial.print(irRangeValue);
    printByte(10);
    Serial.flush();
  }
  else if (input == TOUCH_GET){
      touchRangeValue = analogRead(touchRangeSignal); 
      Serial.print(touchRangeValue);
      printByte(10);
      Serial.flush();
  }
  else if (input == -1){
  }

  */
  if (input == GET_DATA){
    irRangeData = analogRead(irRange);
    bumpData = digitalRead(bump0);
    // equation for range.  NOT ACCURATE PLOT IN ECXCEL
    rangeCM = (2914 / (irRangeData + 5)) - 1;
   
     // send range value converted to CM
    Serial.print(rangeCM);
     // send delimiter to parse in java
    Serial.print(DATA_DELIMITER);
     // send bump sensor status 0 or 1
    Serial.print(bumpData);
    
 
    printByte(10);
    Serial.flush();
  }
  
  //These two delays are in the original code.
  //I think they should wrap around where we get information.
  //IE, they 50 millisecond wait should be moved after the
  //get from the IR sensor, and the 100 millisecond wait
  //should be moved after the send to the Channel.
  delay(50);    
 
  delay(100);    
}
