import java.util.Random;

/**
 * A super swagalicious nifty entity that creates a random stream of processessesses
 * @author pweems1
 *
 */
public class ProcessGeneratorInfinity {
	private int index;
	private Random rand;
	public static final int MAX_SIZE = 48;
	public static final int MAX_DURATION = 16;
	
	public ProcessGeneratorInfinity() {
		index = 0;
		rand = new Random();
	}
	
	public Process getNextProcess() {
		Process proc = new Process(index, rand.nextInt(MAX_SIZE)+1, rand.nextInt(MAX_DURATION)+1);
		index++;
		return proc;
	}
}
