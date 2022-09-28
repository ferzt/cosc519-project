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
	private static final int memorySize = 400;
	
	protected static final char FREE_MEMORY = '.';
	protected static final char RESERVED_MEMORY = '#';
	
	public String algorithmName;
	
	protected int currentTime = -1;
	protected Process[] mainMemory;
	
	/** Processes that are currently in memory. */
	protected ArrayList<Process> processes = new ArrayList<Process>();
	/** Processes that are waiting on the disk. */
	protected ArrayList<Process> diskQueue = new ArrayList<Process>();
	
	protected ArrayList<Process> processesDone = new ArrayList<Process>();

	// must leave MEMSIM_DEBUG to true
	protected static final boolean MEMSIM_DEBUG = true;
	
	public long cumulativeStarvation = 0;
	
	//private int timeAdded;
	
	public SlotAlgorithmBase slotAlgorithm;
	
	public MemorySimulator() {
		mainMemory = new Process[memorySize];
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
		algorithmName = type;
	}
	
	/**
	 * Move the simulator into the future
	 */
	public void timeStep() {
		currentTime++;
		
		//debugPrintln("=========== Time IS NOW " + currentTime + " ============");
		
		addQueuedProcesses();
		
		workOnProcesses();
		
		removeDoneProcesses();
		
		cumulativeStarvation += diskQueue.size();
		
		//addNextProcess();
		
		//printMemory();
	}
	
	/**
	 * Inserts a new process into the simulation.
	 * @param p
	 */
	public void addNewProcess(Process p) {
		diskQueue.add(p);
	}
	
	/**
	 * Works on all processes that are in memory.
	 */
	protected void workOnProcesses() {
		for (Process p : processes) {
			p.doWork();
		}
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
	
	/**
	 * Adds processes from diskQueue until there are none left, or one is too large to insert.
	 */
	protected void addQueuedProcesses() {
		boolean got = true;
		while (!diskQueue.isEmpty() && got) {
			got = putInMemory(diskQueue.get(0));
			if (got)
				diskQueue.remove(0);
		}
		/*Process p;
		if (starved != null)
			p = starved;
		else
			p = procgen.getNextProcess();
		if (!putInMemory(p))
			starved = p;*/
	}
	
	/**
	 * Put a process into memory
	 * @param p The process to put into memory
	 */
	protected boolean putInMemory(Process p) {
		int targetSlot = slotAlgorithm.getNextSlot(p.getSize());
		if (targetSlot == -1) {//no appropriate space found
			return false;
			/* we're not doing that defragmenting stuff anymore
			defragment();
			printMemory();
			targetSlot = slotAlgorithm.getNextSlot(p.getSize());
			if (targetSlot == -1) {//even after defragmenting, there is simply not enough space
				return false;
			}*/
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
		System.out.print(algorithmName+" albolizzem at time " + currentTime + ":");
		if (howManyWaiting() > 0) {
			System.out.print(" (" + howManyWaiting() + " waiting)");
		}
		
		for (int i = 0; i < mainMemory.length; i++) {
			if (i % 80 == 0) {
				System.out.println("");
			}
			System.out.print((mainMemory[i] == null ? '.' : mainMemory[i].getPname()) + "");
		}
		System.out.println("");
		System.out.println("Cumulative starvation: "+cumulativeStarvation);
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
	
	/**
	 * 
	 * @return The number of processes currently waiting on the disk due to lack of an available slot.
	 */
	public int howManyWaiting() {
		return diskQueue.size();
	}
}