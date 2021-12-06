package memorysimulator;

import java.util.List;

public class MultiSimulator {
	MemorySimulator[] sims;
	protected ProcessGeneratorInfinity procgen;
	protected int currentTime = -1;
	
	public MultiSimulator(int numSims) {
		sims = new MemorySimulator[numSims];
		for (int i = 0; i < numSims; i++) {
			sims[i] = new MemorySimulator();
		}
		procgen = new ProcessGeneratorInfinity();
	}
	
	public void timeStep() {
		currentTime++;
		
		//debugPrintln("=========== Time IS NOW " + currentTime + " ============");
		
		addNextProcess();
		
		for (MemorySimulator simp : sims) {
			simp.timeStep();
		}
		
		//printMemory();
	}
	
	private void addNextProcess() {
		addProcessToAll(procgen.getNextProcess());
	}
	
	public void addProcessToAll(Process p) {
		for (MemorySimulator simp : sims) {
			simp.addNewProcess(new Process(p));
		}
	}

	public void setSlotAlgorithms(String[] types) {
		if (types.length != sims.length) {
			Externals.invalidUsageExit();
		}
		for (int i = 0; i < types.length; i++) {
			sims[i].setSlotAlgorithm(types[i].trim());
		}
	}
	
	public void setSlotAlgorithms(List<String> types) {
		if (types.size() != sims.length) {
			Externals.invalidUsageExit();
		}
		for (int i = 0; i < types.size(); i++) {
			sims[i].setSlotAlgorithm(types.get(i).trim());
		}
	}
	
	public MemorySimulator getSim(int index) {
		return sims[index];
	}
	
	public void printStuff() {
		System.out.println("The time is now "+currentTime);
		for (MemorySimulator simp : sims) {
			simp.printMemory();
		}
	}
	
	public void updateUI() {
		
	}
}
