package memorysimulator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Memory strategy that puts a process in memory at the worst-fitting location
 */
public class WorstFitSlotAlgorithm extends SlotAlgorithmBase {
	
	public WorstFitSlotAlgorithm(MemorySimulator insim) {
		super(insim);
	}

	/**
	 * Return the index of the first position of the next available slot
	 * in memory
	 * @param slotSize The size of the requested slot
	 * @return The index of the first position of an available requested block
	 */
	@Override
	protected int getNextSlot(int slotSize) {
		//Go through and find the index of the biggest gap
		int best_start = -1;
		int current_start = -1;
		int biggest_size = 0;
		int found_size = 0;
		
		for (int i = 0; i < sim.mainMemory.length; i++) {
			if (sim.isMemoryFreeAt(i)) {
				if (found_size == 0) {
					current_start = i;
				}
				found_size++;
			} else {
				//Just hit non-free memory
				if (found_size > biggest_size) {
					biggest_size = found_size;
					best_start = current_start;
				}
				found_size = 0;				
			}
		}
		
		//If the last slot is free, we take care of that here
		if (found_size > biggest_size) {
			biggest_size = found_size;
			best_start = current_start;
		}
		
		if (slotSize > biggest_size) { //No slot available
			return -1;
		} else {
			return best_start;
		}
	}

	

}
