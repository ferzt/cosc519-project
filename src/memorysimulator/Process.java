package memorysimulator;


/**
 * Represent a process that uses memory
 */
public class Process implements Comparable<Process> {
	public static final String PROC_NAMES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private int pid;
	protected char pname;
	private int size;
	private int duration;
	private int durationLeft;
	
	private int timeAdded = -1;
	private int memoryLocation = -1;
	
	public Process(int pid, int size, int duration) {
		this.pid = pid;
		if (pid >= 0)
			this.pname = PROC_NAMES.charAt(pid % PROC_NAMES.length());
		this.size = size;
		this.duration = duration;
		this.durationLeft = duration;
	}
	
	/**
	 * Duplicates most of the attributes of the given Process.
	 * @param o
	 */
	public Process(Process o) {
		pid = o.pid;
		pname = o.pname;
		size = o.size;
		duration = o.duration;
		durationLeft = o.durationLeft;
	}
	
	/**
	 * Works on the process, moving it one step closer to completion.
	 */
	public void doWork() {
		durationLeft--;
	}
	
	/**
	 * Get the PID of the process
	 * @return The PID of the process
	 */
	public int getPid() {
		return pid;
	}
	
	/**
	 * Get the single-character name of the process
	 * @return The single-character name of the process
	 */
	public char getPname() {
		return pname;
	}

	/**
	 * Get the amount of memory the process needs
	 * @return The amount of memory the process takes up
	 */
	public int getSize() {
		return size;
	}
	
	public int getTimeAdded() {
		return timeAdded;
	}
	
	/**
	 * Compare this process with another process by start time
	 * @param o The other process
	 */
	@Override
	public int compareTo(Process o) {
		return Integer.compare(this.timeAdded, o.timeAdded);
	}
	
	public boolean samePid(Process o) {
		return this.pid == o.pid;
	}

	public void placeIn(int time, int slot) {
		timeAdded = time;
		memoryLocation = slot;
	}

	public void setLocation(int destination) {
		memoryLocation = destination;
	}

	public boolean isItTimeToGo(int simTime) {
		return durationLeft <= 0;
		//System.out.println(pname + ": is " + simTime + ", should remove at " + (timeAdded + duration));
		//return simTime >= timeAdded + duration;
	}
	
	public int getLocation() {
		return memoryLocation;
	}
}
