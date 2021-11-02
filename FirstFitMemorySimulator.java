import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Memory strategy that puts a process in memory at the
 * first location available in memory
 */
public class FirstFitMemorySimulator extends MemorySimulatorBase {

	/**
	 * Default constructor that initializes the sim 
	 * 
	 */
	public FirstFitMemorySimulator() {
		super();
	}
	
	public FirstFitMemorySimulator(char [] mem, CopyOnWriteArrayList<Process> processes ) {
		super( mem, processes );
	}

	/**
	 * Return the index of the first position of the next available slot
	 * in memory
	 * @param slotSize The size of the requested slot
	 * @return The index of the first position of an available requested block
	 */
	@Override
	public int getNextSlot(int slotSize) {
		int blocksize = 0;
		
		int i;
		for(i = 0; i < main_memory.length - slotSize; i++)
		{
			if(main_memory[i] == FREE_MEMORY && blocksize < slotSize)
				blocksize++;
			else
			{
				if(blocksize >= slotSize)
					return i - blocksize;
				blocksize = 0;
			}
		}
		
		return -1;
	}



}
