package memorysimulator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Memory strategy that puts a process in memory at the next fitting location
 * in memory.
 */
public class NextFitSlotAlgorithm extends SlotAlgorithmBase {
	
	public NextFitSlotAlgorithm(MemorySimulator insim) {
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
		int spaceAvailable = 0;
		for (int i = sim.mainMemory.length-1; i >=0; i--) {
			if (sim.isMemoryFreeAt(i)) {
				spaceAvailable++;
			} else {
				if (spaceAvailable < slotSize) {
					return -1;
				} else {
					return i+1;
				}
			}
		}
		return -1;
	}

}
