import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
	
	protected int CURRENT_TIME = -1;
	protected char[] main_memory;

	protected CopyOnWriteArrayList<Process> processes = new CopyOnWriteArrayList<Process>();

	// must leave MEMSIM_DEBUG to true
	protected static final boolean MEMSIM_DEBUG = true;
	
	private int timeAdded;
	
	protected CopyOnWriteArrayList<Process> copy = new CopyOnWriteArrayList<Process>();
	
	public SlotAlgorithmBase slotAlgorithm;
	
	public MemorySimulator() {
		
		Random rand = new Random();

		main_memory = new char[MemSize];
		
		// change to either 7 or 26
		for( int i = 0; i < 10 ; i++) {
			
			// this picks a char to use as the pid, 
			//since the memory is represented as a char [] we can't have too many processes with unique id's our choice of id's is limited
			final String idChoices = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	        char id = idChoices.charAt(i);
	        
	        // the start times and end times are randomly generated
			int startTime = rand.nextInt(100) + 1;
			int endTime = rand.nextInt(100)+1;
			
			// the end time must be after the start time
			while(startTime >= endTime )
				endTime = rand.nextInt(100)+1;

			int size;
			do {
			size = rand.nextInt(150)+ 1;
			}while(size % 2 !=0);
			Process p = new Process(id, size, startTime, endTime );

			processes.addIfAbsent(p);
			copy.addIfAbsent(p);
		}
		
		initializeMainMemory();

		for (Process p : processes) {
			System.out.println("Process " + p.getPid() + " (size " + p.getSize() + ")");
			System.out.println("Intended Start Time: " + p.getStartTime());
			System.out.println("Time needed by process: " + p.getTimeInMemory() );
		}
	}
	
	public MemorySimulator(char [] mem, CopyOnWriteArrayList<Process> processes ) {
		
		for(Process p : processes) {
			this.processes.add(p);
		}
		
		this.main_memory = mem.clone();	
		initializeMainMemory();	
		for (Process p : processes) {
			System.out.println("Process " + p.getPid() + " (size " + p.getSize() + ")");
			System.out.println("Intended Start Time: " + p.getStartTime());
			System.out.println("Time needed by process: " + p.getTimeInMemory() );
		}
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
	 * This is the wait functionality that someone added, apparently
	 * @param t
	 */
	public void wait(int t) {
		
		boolean remove = false;
		
		while (CURRENT_TIME < t) {
			CURRENT_TIME++;		
			while (!eventOccursAt(CURRENT_TIME) && CURRENT_TIME < t) {
				debugPrintln("Fast-forwarding past boring time " + CURRENT_TIME);
				CURRENT_TIME++;
			}
			
			debugPrintln("=========== WAIT IS NOW " + CURRENT_TIME + " ============");
			
			//Processes exit the system
			ArrayList<Process> toRemove = new ArrayList<Process>();

			for (Process p : processes) {
				// made <= to deal with processes that have their end time pass due to waiting to enter into memory
				int timeInMemory = CURRENT_TIME - p.getTimeAdded();
					for(int k = 0; k < main_memory.length; k++ ) {
						if (timeInMemory >= p.getTimeInMemory() && main_memory[k] == p.getPid()) {
							debugPrintln("Removing process " + p.getPid());
							removeFromMemory(p);
							remove = true;
							toRemove.add(p);
						}
					}
					if(remove)
						defragment();
					remove = false;
			}	 
			for (Process p : toRemove) {
				processes.remove(p);
			}
			
			for (Process p : processes) {
				if (p.getStartTime() == CURRENT_TIME) {
					debugPrintln("Adding process " + p.getPid());
					putInMemory(p);
			}
		}
			
			if(eventOccursAt(CURRENT_TIME))
				printMemory();
		}
	}

	/**
	 * Move the simulator into the future
	 * @param t The time to which to move the simulator
	 */
	public void timeStepUntil(int t) {
		
		
		
		while (CURRENT_TIME < t) {
			CURRENT_TIME++;		
			while (!eventOccursAt(CURRENT_TIME) && CURRENT_TIME < t) {
				debugPrintln("Fast-forwarding past boring time " + CURRENT_TIME);
				CURRENT_TIME++;
			}
			
			debugPrintln("=========== Time IS NOW " + CURRENT_TIME + " ============");
			
			//Processes exit the system
			ArrayList<Process> toRemove = new ArrayList<Process>();

			for (Process p : processes) {
				// made <= to deal with processes that have their end time pass, due to waiting to enter into memory
				int timeInMemory = CURRENT_TIME - p.getTimeAdded();
					for(int k = 0; k < main_memory.length; k++ ) {
						if (timeInMemory >= p.getTimeInMemory() && main_memory[k] == p.getPid()) {
							debugPrintln("Removing process " + p.getPid());
							removeFromMemory(p);
							toRemove.add(p);
						}
					}
			}	 
			for (Process p : toRemove) {
				processes.remove(p);
			}
			
			//Processes enter the system
			for (Process p : processes) {
				if (p.getStartTime() == CURRENT_TIME) {
					debugPrintln("Adding process " + p.getPid());
					putInMemory(p);
			}
		}
			
			if(eventOccursAt(CURRENT_TIME))
				printMemory();
		}
		
	}
	
	/**
	 * Find whether an event occurs at a specific time
	 * Useful for ascertaining if we can skip a time in the simulator
	 * @param time The time we should check to see if an event occurs
	 * @return True if an event occurs at time, else false
	 */
	private boolean eventOccursAt(int time) {

		for (Process p : processes) {
			if (p.getStartTime() == time || (p.getTimeAdded() + p.getTimeInMemory()) == time) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Put a process into memory
	 * @param p The process to put into memory
	 */
	protected void putInMemory(Process p) {

		int targetSlot = slotAlgorithm.getNextSlot(p.getSize());

		if (targetSlot == -1) {
			
			defragment();
			printMemory();
			targetSlot = slotAlgorithm.getNextSlot(p.getSize());
			int count = CURRENT_TIME ;
			
			while( targetSlot == -1) {
				wait(count++);
				
				targetSlot = slotAlgorithm.getNextSlot(p.getSize());
				if(CURRENT_TIME > 50000 ) {
					Externals.endOfSimulation();
				}
			}
		}
		
		//If we get here, we know that there's an open chunk
		p.setTimeAdded(CURRENT_TIME);
		debugPrintln("Got a target slot of " + targetSlot + " for pid " + p.getPid());

		
		for (int i = 0; i < p.getSize(); i++) {
			main_memory[i+targetSlot] = p.getPid();
		}
	}
	
	/**
	 * Take a process out of memory
	 * @param p The process to remove from memory
	 */
	protected void removeFromMemory(Process p) {
		
		for (int i = 0; i < main_memory.length; i++) {
			if (main_memory[i] == p.getPid()) {
				main_memory[i] = FREE_MEMORY;
			}
		}
	}

	/**
	 * Initialize our main memory with the predetermined amount of reserved and
	 * free memory 
	 */
	private void initializeMainMemory() {

		for (int i = 0; i < 80 && i < main_memory.length; i++) {
			main_memory[i] = RESERVED_MEMORY;
		}
		
		for (int i = 80; i < main_memory.length; i++) {
			main_memory[i] = FREE_MEMORY;
		}
	}

	/**
	 * Print the current contents of memory
	 */
	public void printMemory() {
		System.out.print("Memory at time " + CURRENT_TIME + ":");
		
		for (int i = 0; i < main_memory.length; i++) {
			if (i % 80 == 0) {
				System.out.println("");
			}
			System.out.print( main_memory[i] + "" );
		}
		System.out.println("");
	}
	
	/**
	 * Attempt to defragment main memory
	 */
	private void defragment() {
		HashMap<Character, Integer> processesMoved = new HashMap<Character, Integer>();

		DecimalFormat f = new DecimalFormat("##.00");
		
		printMemory();

		System.out.println("Performing defragmentation...");
		int destination = 80;
		
		for (int i = 0; i < main_memory.length; i++) {
			if (main_memory[i] != FREE_MEMORY 
					&& main_memory[i] != RESERVED_MEMORY
					&& i != destination ) {
				main_memory[destination] = main_memory[i];
				main_memory[i] = FREE_MEMORY; 
				destination++;
				processesMoved.put(main_memory[i], null);
			}
		}

		int numMoved = processesMoved.size();
		int freeBlockSize = main_memory.length - destination;
		double percentage = (double)freeBlockSize / (double)main_memory.length;
		
		System.out.println("Defragmentation completed.");
		System.out.println("Relocated " + numMoved + " processes " +
				"to create a free memory block of " + freeBlockSize + " units " +
				"(" + f.format(percentage * 100) + "% of total memory).");
	}

	/**
	 * Print a string if a debug flag is set.
	 * Do not include a newline.
	 * @param toPrint The string to print
	 */
	private static void debugPrint(String toPrint) {
		if (MEMSIM_DEBUG == true) {
			System.out.print(toPrint);
		}
	}
	
	/**
	 * Print a string if a debug flag is set.
	 * Include a newline.
	 * @param toPrint The string to print
	 */
	private static void debugPrintln(String toPrint) {
		if (MEMSIM_DEBUG == true) {
			System.out.println(toPrint);
		}
	}

	/**
	 * Get the number of processes with events remaining in the simulator
	 * @return The number of processes with events remaining in the simulator
	 */
	public int processesRemaining() {
		return processes.size();
	}
	
	
	
	
}
