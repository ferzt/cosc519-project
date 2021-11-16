

/**
 * Main class sets up and runs the simulation,
 *
 */
public class Main {
	public static void main( String[] args ) {
		if (args.length != 1) {
			Externals.invalidUsageExit();
		}
		
		String simName = args[0].trim();
		MemorySimulator sim = new MemorySimulator();
		sim.setSlotAlgorithm(simName);
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
