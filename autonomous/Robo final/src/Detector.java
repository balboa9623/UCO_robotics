import java.util.concurrent.atomic.AtomicReference;


import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import org.freedesktop.dbus.Message;


public class Detector  implements Runnable {
 
	Thread thread = null;
	static EV3ColorSensor color_sensor;
	int x;
	boolean colorId;
	//AtomicReference<Integer> x = new AtomicReference<>();
	
	//AtomicReference<Boolean> colorId = new AtomicReference<>();
	
	// use vars like these to pass things between threads.
	// atomicReference.setRelease() to set
	// atomicReference.get() to get

	@Override
	public void run() {
		
		color_sensor = Controller.color_sensor;
//		SensorMode colorIdMode = color_sensor.getColorIDMode();
		
		System.out.println("Thread");
		while(true) {
			x = color_sensor.getColorID();
			colorId = Controller.getColor(x);
			
			// x.setRelease(color_sensor.getColorID());
			//colorId.setRelease(Controller.getColor(x.get()));
			//System.out.println(colorId);
			if(colorId) {
				Controller.closeClaw();
				Controller.closeCheck();
				break;
			}
		}
		
		
	}
	
	public void stop() {
		this.thread.interrupt();
	}
	
	Detector(){
		//#TODO whatever we need in constructor
		//CALL BUILD TO INSTANTIATE
		//Constructor is just used by build, not developer
		
	}
	
	public static Detector build() {
		Detector detector = new Detector();
		detector.thread = new Thread(detector);
		detector.thread.setDaemon(true);
		detector.thread.start();
		return detector;
	}

}
