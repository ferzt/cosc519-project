import java.util.Random;

/**
 * A super swagalicious nifty entity that creates a random stream of processessesses
 * @author pweems1
 *
 */
public class ProcessGeneratorInfinity {
	private int index;
	private Random rand;
	
	
	public ProcessGeneratorInfinity() {
		index = 0;
		rand = new Random();
	}
	
	public Process getNextProcess() {
		Process proc = new Process(index, rand.nextInt(32)+1, rand.nextInt(8)+1);
		index++;
		return proc;
	}
}
