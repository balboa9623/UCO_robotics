import lejos.hardware.Sound;

public class Timer implements Runnable {
	private long delay = 0L; 
	  Thread thread = null; //thread for the timer to run in
	  
	  

	  Timer() {
		  
		  
		  
	  }
	  

	  public static Timer build() {
		  Timer timer = new Timer();
		  timer.thread = new Thread(timer);
		  timer.thread.setDaemon(true);
		  timer.thread.start();
		  return timer;
	  }
	  

	
	   
	  @Override public void run() {
		    try {
		    	Controller.timer=true;
		      for(int i = 5; i > 0; i--) {
		       
		         
		         
		          System.out.println("Countdown: " + i);
		          Sound.beep();
		          Thread.sleep(1000L);
		  
		        
		      }
		      
		      Sound.buzz();
		      System.out.println("GOAL!!!!!");
		      Controller.timer=false;
		    } catch(InterruptedException e) {
		    	System.out.println("thread crash - timer");
		    }
		  }
		  
	  
	  // Stop the thread
	  public void stop() {
		  this.thread.interrupt();
	  }

		  
		  
	  }
	  
	  