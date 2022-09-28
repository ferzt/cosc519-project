package memorysimulator;


/**
 * Main class sets up and runs the simulation,
 *
 */
public class Main {
	public static void main( String[] args ) {		
		try {
			UIStarter.main(args);
		} catch(Exception re) {
			Externals.uiLibraryUnavailable();
		}
	}
}
