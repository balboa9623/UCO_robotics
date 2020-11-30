/*
 * Group 5: Josh Layton, Aditi Panvelkar, Jon Craig, 
 * 			Chase Minden, Trevor Pendarvis, Richu Mathews
 * CMSC 4193: Introduction to Robotics
 * Lejos EV3 Project
 * Controller.java: 
 * 			This program controls the main function that calls the robot to run.
 * It requires the java programs: Maze.java, MazeDriver.java, Coordinate.java, 
 * BFSMazeSolver.java, DFSMazeSolver.java.
 * 
 */

import java.awt.Color;
import java.util.Arrays;



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

public class Controller {

	static EV3LargeRegulatedMotor LEFT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.B);
	static EV3LargeRegulatedMotor RIGHT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.A);
	static EV3MediumRegulatedMotor CLAW_MOTOR = new EV3MediumRegulatedMotor(MotorPort.C);
	static SensorModes uss_sensor = new EV3UltrasonicSensor(SensorPort.S4);
	static SensorModes gyro_sensor = new EV3GyroSensor(SensorPort.S3);

	static int[][] grid = new int[6][5]; //TODO 23 width x 28 length
	//0 - empty, 1 - block, 2 unknown, 3 path
	
	static int startY = 5;
	static int startX = 4;
	static int startRotation = 0;
	static int endX = 2;
	static int endY = 1;
  static final int OPEN = 0;
	static final int OBSTACLE = 1;
	static final int PATH = 3;
	static final int MOVEAMOUNT=-17;
	static final double MAXDISTANCE = .2;
	static boolean hasReachedGoal = false;
	static boolean hasBall = false;
	public static boolean isClosed = false;
	static int x;
	static int y;
	static int r;
	static Wheel wheel1 = WheeledChassis.modelWheel(LEFT_MOTOR, 4.22).offset(-4.7);
	static Wheel wheel2 = WheeledChassis.modelWheel(RIGHT_MOTOR, 4.22).offset(4.7);
	static Chassis chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
	static MovePilot pilot = new MovePilot(chassis);
	static EV3ColorSensor color_sensor = new EV3ColorSensor(SensorPort.S2);
	public static boolean timer;
	
	public static void main(String[] args) throws Exception {
		
		   color_sensor.setCurrentMode("ColorID");
		   color_sensor.setFloodlight(false);
		// Set up the wheel for pilot.
		CLAW_MOTOR.setSpeed(2000);
		//openClaw();
////////////////////////////////////////////////////////////////////////////////////////////////
		pilot.setLinearSpeed(6);
		pilot.setAngularSpeed(60);
		// set up the chassis type: Differential pilot 
		// Chassis chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 },
		// WheeledChassis.TYPE_DIFFERENTIAL);
		
////////////////////////////////////////////////////////////////////////////////////////////////
//		
		
//		SensorMode colorIdMode = color_sensor.getColorIDMode();
		boolean colorId;
		
		Detector detector = Detector.build();
		float distanceValue;
		initializeMap(startX, startY, startRotation);// x,y,r
	
		
	
		int index = 0;
		int searchIndex=0;
		int[] searchTargetX = new int[]{0,1,2,3,4};
		int[] searchTargetY = new int[]{0,5,0,5,0,5};
		endX=searchTargetX[0];
		endY=searchTargetX[0];
		resetGrid();
		
		hasReachedGoal = false;
		hasBall = false;
		while(true){
			
			int x = color_sensor.getColorID();
			colorId = getColor(x);
			if(!hasBall && !hasReachedGoal){
				if(colorId) {
					System.out.println("Going to Goal");
					hasBall = true;
					endX = 0;
					endY = 0;
					hasReachedGoal=false;
					resetGrid();
					
					//close claw
				}else {
						if(isClosed) {
							openClaw();
						}
						distanceValue = getDistance();
						NextMove(distanceValue);	
				}
			}
			if(hasReachedGoal && !hasBall ){
				searchIndex++;
				endX = searchTargetX[searchIndex];
				endY = searchTargetY[searchIndex];
				hasReachedGoal=false;
				resetGrid();
				System.out.println("finding New Search");
			}

			if(hasBall && !hasReachedGoal) {
				
						distanceValue = getDistance();
						NextMove(distanceValue);
						System.out.println("Has Ball");
			}
			if(hasBall && hasReachedGoal) {
				RotateTowards(0,0);
				hasBall=false;
				hasReachedGoal=true;
				openClaw();
				Timer timer = Timer.build();
				Move(-MOVEAMOUNT);
				Move(-MOVEAMOUNT);
				y+=4;
				
				
				detector = Detector.build();
			}
			
		}
		
		
		
		}
		
	

	public static void printMap() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				System.out.print(grid[i][j] + " , ");

			}
			System.out.println("");
		}
	}

	// Gets the Distance
	public static float getDistance() {


		float distance =  200;


		SampleProvider distance_provider = uss_sensor.getMode("Distance");
		float[] sample = new float[distance_provider.sampleSize()];
		distance_provider.fetchSample(sample, 0);
		for(int i = 0; i < distance_provider.sampleSize(); i++){
			if(sample[i]<distance)
			distance = sample[i];
		}


		return distance;

	}

	public static float getAngle(){
		float angle = 0;
		SampleProvider angle_provider = gyro_sensor.getMode("Angle");
		float[] sample = new float[angle_provider.sampleSize()];
		angle_provider.fetchSample(sample,0);
		for(int i = 0; i < angle_provider.sampleSize();i++){
			angle += sample[i];
		}
		angle = angle / angle_provider.sampleSize();
		return angle;
	}

	public static void Rotate(int degree) {
		
		pilot.rotate(degree);
		int currentAngle = (int)getAngle();
		while(currentAngle > 360){
			currentAngle = currentAngle - 360; //gyro will add or subtract past 360, correct for this.
		}
		while(currentAngle < 0){
			currentAngle = currentAngle + 360;
		}

//		if(currentAngle < degree)
//		{// if it couldn't get an accurate turn the first time, it likely won't the second either. we can tighten it a bit though.
//			pilot.rotate((degree - currentAngle) / 2); // try to move about half the distance, to get a little more accuracy
//		} 
//		else if(currentAngle > degree){
//			pilot.rotate((currentAngle - degree) / 2); // try to move about half the distance, to get a little more accuracy
//		}
		r += degree; // Josh likely depends on this somewhere, but you could set it based off gyro, youd have to adjust for inaccuracies.
		
		while(r<0 || r >=360) {
			if (r >= 360) {
				r = r-360;
			}
			if(r<0) {
				r = 360+r;
			}
		}
		
	}



   /*
	public static void Rotate(int degree) {
		
		pilot.rotate(degree);
		
		r += 90;
		if (r == 360) {
			r = 0;
		}
	}
	*/


	// moves an amount int centimeters.
	public static void Move(int amount) {
		pilot.travel(amount);
		
			switch (r) {
			case 90:
				x++;
				break;
			case 180:
				y++;
				break;
			case 270:
				x--;
				break;
			case 0:
				y--;
				break;
			}
		
		
		
	}

	public static void NextMove(float distanceVal) throws Exception {
		
		if (distanceVal < MAXDISTANCE) {
			// Mark object as blocked. If the object is incorrectly marked in the map,
			// restart the algorithm and mark the path.
			switch (r) {
			case 90:

				if (x < grid[0].length -1 && grid[y][x + 1] != OBSTACLE) { //is there obstacle on map?
					System.out.println(" Obstacle at y, x+1 ");
					grid[y][x + 1] = OBSTACLE; // mark map
					startPathfinding(); //find new path
				}
				 else {
					MoveUp();
					System.out.println(" Keep Moving  ");
				}

				break;
			case 180:
				if (y < grid.length -1 &&  grid[y + 1][x] != OBSTACLE) {
					grid[y + 1][x] = OBSTACLE;
					System.out.println(" Obstacle at y+1, x ");
					startPathfinding();
				} else {
					MoveUp();
					System.out.println(" Keep Moving  ");
				}
				break;
			case 270:

				if (x>0 && grid[y][x - 1] != OBSTACLE) {
					System.out.println(" Obstacle at y, x-1 ");
					grid[y][x - 1] = OBSTACLE;

					startPathfinding();
				} else {
					MoveUp();
					System.out.println(" Keep Moving  ");
				}
				break;
			case 0:

				if (y>0 && grid[y - 1][x] != OBSTACLE) {
					System.out.println(" Obstacle at y-1");
					grid[y - 1][x] = OBSTACLE;
					startPathfinding();
				} else {
					MoveUp();
					System.out.println(" Keep Moving  ");
				}
				break;

			}

		}

		else {
			MoveUp();
		}
	}

	// Moves Towards the Next "3" marked by Pathfinder.java
	public static void MoveUp() {

//		if (grid[y][x + 1] == PATH) { 
//			RotateTowards(y, x + 1);
//			Move(5);
//			
//			grid[x][y] = OPEN;
//		} else if (grid[y][x - 1] == PATH) {
//			RotateTowards(y, x - 1);
//			Move(5);
//			
//			grid[x][y] = OPEN;
//		} else if (grid[y + 1][x] == PATH) {
//			RotateTowards(y + 1, x);
//			Move(5);
//		
//			grid[x][y] = OPEN;
//		} else if (grid[y - 1][x] == PATH) {
//			RotateTowards(y - 1, x);
//			Move(5);
//		
//			grid[x][y] = OPEN;
		
		if (x < grid[0].length-1 && grid[y][x + 1] == PATH) { //checking right grid
			System.out.println("Checked right grid and rotate towards y, x+1");
			RotateTowards(x + 1, y);
			int newx = x + 1;
			System.out.println("rotated x + 1, y: " + newx + ", " + y);
			grid[y][x] = OPEN;
			if(getDistance() > MAXDISTANCE) {
				Move(MOVEAMOUNT);
				grid[y][x] = OPEN;
			}
			
			
			
		} else if (x > 0 && grid[y][x - 1] == PATH) {  //checking left grid
			System.out.println("Checked right grid and rotate towards y, x-1");
			RotateTowards(x - 1, y);
			int newx = x - 1;
			System.out.println("rotated x - 1, y: " + newx + ", " + y);
			grid[y][x] = OPEN;
			if(getDistance() > MAXDISTANCE) {
				Move(MOVEAMOUNT);
				grid[y][x] = OPEN;
			}
			
		
		} else if (y < grid.length-1 && grid[y + 1][x] == PATH) { //checking bottom grid
			System.out.println("Checked right grid and rotate towards y+1,x");
			RotateTowards(x, y + 1);
			int newy = y + 1;
			System.out.println("rotated x, y + 1: " + x + ", " + newy);
			grid[y][x] = OPEN;
			if(getDistance() > MAXDISTANCE) {
				Move(MOVEAMOUNT);
				grid[y][x] = OPEN;
			}
			
			
		} else if (y > 0 && grid[y - 1][x] == PATH) { //checking top grid
			System.out.println("Checked right grid and rotate towards y-1, x");
			RotateTowards(x, y - 1);
			int newy = y - 1;
			System.out.println("rotated x, y - 1: " + x + ", " + newy);
			grid[y][x] = OPEN;
			if(getDistance() > MAXDISTANCE) {
				Move(MOVEAMOUNT);
				grid[y][x] = OPEN;
			}
			

		}
		else{
			hasReachedGoal = true;
		}

	}

	public static void startPathfinding() throws Exception {
		hasReachedGoal=false;
		resetGrid();
	}

	// Rotates towards a given x and y.
	public static void RotateTowards(int localX, int localY) {
		if (localX > x) {
			if (r == 0) {
				Rotate(90);
			} else if (r == 90) {
				// Do Nothing
			} else if (r == 180) {
				Rotate(-90);
			} else if (r == 270) {
				Rotate(180);
			}
		}

		if (localX < x) {
			if (r == 0) {
				Rotate(-90);
			} else if (r == 90) {
				Rotate(180);
			} else if (r == 180) {
				Rotate(90);
			} else if (r == 270) {
				// Do Nothing
			}
		}

		if (localY > y) {
			if (r == 0) {
				Rotate(180);
			} else if (r == 90) {
				Rotate(90);
			} else if (r == 180) {
				// Do Nothing
			} else if (r == 270) {
				Rotate(-90);
			}
		}

		if (localY < y) {
			if (r == 0) {
				// Do Nothing
			} else if (r == 90) {
				Rotate(-90);
			} else if (r == 180) {
				Rotate(180);
			} else if (r == 270) {
				Rotate(90);
			}
		}
	}

	// Resets all the 4s and 3s back to 0s
	public static void resetGrid() throws Exception {
		int i;
		int j;
		for (i = 0; i < grid.length; i++) {
			for (j = 0; j < grid[i].length; j++) {
				if (grid[i][j] != OBSTACLE) {
					grid[i][j] = OPEN;
				}
			}
	     i = 0;
	     j = 0;
	     		grid[endY][endX] = 4;
	     		grid[y][x]=2;
			
				int[][] tempMaze = MazeDriver.execute(grid);
				for (i = 0; i < grid.length; i++) {
					for (j = 0; j < grid[i].length; j++) {
						if (tempMaze[i][j] == PATH) {
							grid[i][j] = PATH;
						}
						
						
					}

				}
				grid[endY][endX] = 3;
				
		}

	}

	public static void initializeMap(int xLocal, int yLocal, int rLocal) {

		x = xLocal;
		y = yLocal;
		r = rLocal;
//
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				grid[i][j] = OPEN;

			}
		}
	

	}

	public static boolean getColor(int colorID) {
		
     System.out.println(colorID);
     
     
		
		switch(colorID) {

        case -1: return false;
                 
        case 0: return true;
                
        case 1: return false;
                
        case 2: return false;
                
        case 3: return false;
                
        case 4: return false;
                
        case 5: return false;
               
        case 6: return false;
        
        case 7: return false;
        
        case 8: return false;
        
        case 9: return false;
        
        case 10: return false;
        
        case 11: return false;
        
        case 12: return false;
        
        case 13: return false;


        default: return false;
		
	}
		
	
}
	public static void closeCheck() {
		isClosed=false;
	}

	public static void openClaw() {
		isClosed = false;
		CLAW_MOTOR.rotate(1120);	
		}

	public static void closeClaw() {
		// TODO Auto-generated method stub
		CLAW_MOTOR.rotate(-1120);
	}}