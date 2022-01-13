# Blue Gate Server

Repository for building a simple Raspberry Pi Bluetooth server (in java)


### Deployment

It is possible that it is missing the `javax.bluetooth` libraries. Those can be found in the `bluecove-arm-03-02-15.zip` file (from this [link](https://lukealderton.com/media/1002/bluecove-arm-03-02-15.zip)). This zip contains the following files:

 * `bluecove-2.1.1-SNAPSHOT.jar` (Main BlueCove module)
 * `bluecove-gpl-2.1.1-SNAPSHOT.jar` ([additional module for BlueCove to support bluecove on Linux](http://www.bluecove.org/bluecove-gpl/))
 * `bluecove-emu-2.1.1-SNAPSHOT.jar` (Emulator)
 * `libbluecove_arm.so`


The `.jar` files need to be added to the classpath so that `javac` and `java` can use them to compile and run your code.
