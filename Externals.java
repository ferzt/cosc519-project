
/**
 * A place for our constants, macros, and helper functions
 */
public class Externals {
	//public static final int MAIN_MEMORY_SIZE = 2400;

	/**
	 * Wrong command-line arguments supplied.
	 * Prints error message and exits.
	 */
	public static void invalidUsageExit() {
		System.err.println("USAGE: java Main { first | best | next | worst }");
		System.exit(1);
	}
	
	/**
	 * Simulator ran out of memory.
	 * Prints error message and exits.
	 */
	public static void outOfMemoryExit() {
		System.err.println("OUT OF MEMORY... exiting!");
		System.exit(1);
	}
	
	public static void endOfSimulation() {
		System.err.println("end of simulation.");
		System.exit(1);
	}
}
