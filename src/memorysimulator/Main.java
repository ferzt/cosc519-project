package memorysimulator;


/**
 * Main class sets up and runs the simulation,
 *
 */
public class Main {
	public static void main( String[] args ) {
		if (args.length < 1 || args.length > 2) {
			Externals.invalidUsageExit();
		}
		
		try {
			UIStarter.main(args);
		} catch(Exception re) {
			Externals.uiLibraryUnavailable();
		}
	}
}
