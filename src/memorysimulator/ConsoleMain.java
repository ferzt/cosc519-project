

/**
 * Run this class to test only the simulators, without worrying about the UI.
 *
 */
public class ConsoleMain {
	public static void main( String[] args ) {
		/*if (args.length != 1) {
			Externals.invalidUsageExit();
		}*/
		
		MultiSimulator sim = new MultiSimulator(args.length);
		sim.setSlotAlgorithms(args);
		
		while (true) {
			sim.timeStep();
			sim.printStuff();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
	}
}
