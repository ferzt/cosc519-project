
public abstract class SlotAlgorithmBase {
	
	public MemorySimulator sim;
	
	public SlotAlgorithmBase(MemorySimulator insim) {
		sim = insim;
	}

	
	/**
	 * Return the index of the first position of the next available slot
	 * in memory
	 * 
	 * Different memory strategy classes must override this abstract method.
	 * @param slotSize The size of the requested slot
	 * @return The index of the first position of an available requested block
	 */
	protected abstract int getNextSlot(int slotSize);
}
