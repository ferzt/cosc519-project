

/**
 * Main class sets up and runs the simulation,
 *
 */
public class Main {
	
	static int TIME;
	
	public static void main( String[] args ) {
		if (args.length != 2) {
			Externals.invalidUsageExit();
		}
		
		String simName1 = args[0].trim();
		MemorySimulatorBase sim1 = null;
		
		if ( "first".equals(simName1) ) {
			sim1 = new FirstFitMemorySimulator( );
		} else if ( "best".equals(simName1) ) {
			sim1 = new BestFitMemorySimulator();
		} else if ( "next".equals(simName1) ) {
			sim1 = new NextFitMemorySimulator();
		} else if ( "worst".equals(simName1) ) {
			sim1 = new WorstFitMemorySimulator();
		}
			else {
			Externals.invalidUsageExit();
		}
	
		sim1.timeStepUntil(0);
		sim1.printMemory();
	
		int count1 = 0;
		while (sim1.processesRemaining() != 0) {
			sim1.timeStepUntil(count1++);
		}
		
		System.out.println("No more events to process... ending this simulation!");
		
		
		String simName2 = args[1].trim();
		MemorySimulatorBase sim2 = null;
		
		if ( "first".equals(simName2) ) {
			sim2 = new FirstFitMemorySimulator(sim1.main_memory, sim1.copy );
		} else if ( "best".equals(simName2) ) {
			sim2 = new BestFitMemorySimulator(sim1.main_memory, sim1.copy );
		} else if ( "next".equals(simName2) ) {
			sim2 = new NextFitMemorySimulator(sim1.main_memory, sim1.copy );
		} else if ( "worst".equals(simName2) ) {
			sim2 = new WorstFitMemorySimulator(sim1.main_memory, sim1.copy );
		}
			else {
			Externals.invalidUsageExit();
		}
		
		sim2.timeStepUntil(0);
		sim2.printMemory();
	
		int count2 = 0;
		while (sim2.processesRemaining() != 0) {
			sim2.timeStepUntil(count2++);
		}
		
		System.out.println("No more events to process... ending this simulation!");
	}
}
