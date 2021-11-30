package memorysimulator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
/**

 * Starter code for a memory simulator.

 * Simulator strategies extend this abstract class.

 */

public class MemorySimulator {
	
	//change to either 1024 or 400
	private static final int MemSize = 400;
	
	protected static final char FREE_MEMORY = '.';
	protected static final char RESERVED_MEMORY = '#';
	
	protected ProcessGeneratorInfinity procgen;//TODO move this elsewhere
	protected int currentTime = -1;
	protected Process starved = null;
	protected Process[] mainMemory;

	protected ArrayList<Process> processes = new ArrayList<Process>();
	
	protected ArrayList<Process> processesDone = new ArrayList<Process>();

	// must leave MEMSIM_DEBUG to true
	protected static final boolean MEMSIM_DEBUG = true;
	
	//private int timeAdded;
	
	public SlotAlgorithmBase slotAlgorithm;
	
	public MemorySimulator() {
		mainMemory = new Process[MemSize];
		procgen = new ProcessGeneratorInfinity();
		initializeMainMemory();
	}
	
	public void setSlotAlgorithm(String type) {
		switch (type) {
			case "first":
				slotAlgorithm = new FirstFitSlotAlgorithm(this);
				break;
			case "next":
				slotAlgorithm = new NextFitSlotAlgorithm(this);
				break;
			case "best":
				slotAlgorithm = new BestFitSlotAlgorithm(this);
				break;
			case "worst":
				slotAlgorithm = new WorstFitSlotAlgorithm(this);
				break;
			default:
				Externals.invalidUsageExit();
		}
	}
	
	/**
	 * Move the simulator into the future
	 */
	public void timeStep() {
		currentTime++;
		
		//debugPrintln("=========== Time IS NOW " + currentTime + " ============");
		
		int removed = removeDoneProcesses();
		
		addNextProcess();
		
		//printMemory();
	}
	
	protected int removeDoneProcesses() {
		ArrayList<Process> toRemove = new ArrayList<Process>();
		for (Process p : processes) {

			if (p.isItTimeToGo(currentTime)) {
				toRemove.add(p);
			}
		}
		processesDone = toRemove;
		for (Process p : toRemove) {
			removeProcess(p);
		}
		
		return toRemove.size();
	}
	
	protected void removeProcess(Process p) {
		for (int i = 0; i < mainMemory.length; i++) {
			if (mainMemory[i] == p) {
				mainMemory[i] = null;
			}
		}
		processes.remove(p);
	}
	
	protected void addNextProcess() {
		Process p;
		if (starved != null)
			p = starved;
		else
			p = procgen.getNextProcess();
		if (!putInMemory(p))
			starved = p;
	}
	
	/**
	 * Put a process into memory
	 * @param p The process to put into memory
	 */
	protected boolean putInMemory(Process p) {
		int targetSlot = slotAlgorithm.getNextSlot(p.getSize());
		if (targetSlot == -1) {//no appropriate space found
			defragment();
			printMemory();
			targetSlot = slotAlgorithm.getNextSlot(p.getSize());
			if (targetSlot == -1) {//even after defragmenting, there is simply not enough space
				return false;
			}
		}
		//debugPrintln("Got a target slot of " + targetSlot + " for pid " + p.getPid());
		putInMemoryAt(p, targetSlot);
		return true;
	}
	
	/**
	 * Actually inserts the process into memory
	 */
	private void putInMemoryAt(Process p, int location) {
		processes.add(p);
		p.placeIn(currentTime, location);
		for (int i = 0; i < p.getSize(); i++) {
			mainMemory[location + i] = p;
		}
	}
	
	/**
	 * Initialize our main memory with the predetermined amount of reserved and
	 * free memory 
	 */
	private void initializeMainMemory() {
		for (int i = 80; i < mainMemory.length; i++) {
			mainMemory[i] = null;
		}
		putInMemoryAt(new ProcessReserve(80), 0);
	}

	/**
	 * Print the current contents of memory
	 */
	public void printMemory() {
		System.out.print("Memory at time " + currentTime + ":");
		
		for (int i = 0; i < mainMemory.length; i++) {
			if (i % 80 == 0) {
				System.out.println("");
			}
			System.out.print((mainMemory[i] == null ? '.' : mainMemory[i].getPname()) + "");
		}
		System.out.println("");
	}
	
	/**
	 * Attempt to defragment main memory
	 */
	private void defragment() {
		DecimalFormat f = new DecimalFormat("##.00");
		
		printMemory();

		System.out.println("Performing defragmentation...");
		int destination = 0;
		Process lastMoved = null;
		for (int i = 0; i < mainMemory.length; i++) {
			if (!isMemoryFreeAt(i) && i != destination) {
				if (lastMoved != mainMemory[i]) {
					lastMoved = mainMemory[i];
					lastMoved.setLocation(destination);
				}
				mainMemory[destination] = mainMemory[i];
				mainMemory[i] = null;
			}
			System.out.println("i="+i+", destination="+destination);
			printMemory();
			if (!isMemoryFreeAt(destination) && destination <= i) {
				destination++;
			}
		}
		int freeBlockSize = mainMemory.length - destination;
		double percentage = (double)freeBlockSize / (double)mainMemory.length;
		
		System.out.println("Defragmentation completed.");
		System.out.println("Relocated some processes " +
				"to create a free memory block of " + freeBlockSize + " units " +
				"(" + f.format(percentage * 100) + "% of total memory).");
		//Externals.outOfMemoryExit();
	}
	
	public boolean isMemoryFreeAt(int index) {
		return mainMemory[index] == null;
	}
	
	public ArrayList<Process> getProcesses() {
		return processes;
	}
}
