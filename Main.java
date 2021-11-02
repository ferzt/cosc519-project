

/**
 * Main class sets up and runs the simulation,
 *
 */
public class Main {
	
	static int TIME;
	
	public static void main( String[] args ) {
		if (args.length != 1) {
			Externals.invalidUsageExit();
		}
		
		String simName = args[0].trim();
		MemorySimulator sim = new MemorySimulator();
		sim.setSlotAlgorithm(simName);
	
		sim.timeStepUntil(0);
		sim.printMemory();
	
		int count1 = 0;
		while (sim.processesRemaining() != 0) {
			sim.timeStepUntil(count1++);
		}
		
		System.out.println("No more events to process... ending this simulation!");
	}
}
