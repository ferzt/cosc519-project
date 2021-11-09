

/**
 * Represent a process that uses memory
 */
public class Process implements Comparable<Process> {
	public static final String PROC_NAMES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private int pid;
	protected char pname;
	private int size;
	private int duration;
	
	private int timeAdded = -1;
	private int memoryLocation = -1;

	/**
	 * Default constructor
	 * @param pid The ASCII identifier of the new process
	 * @param size The amount of memory the process uses
	 * @param start_time The virtual time the process enters memory
	 * @param end_time The virtual time the process leaves memory
	 */
	public Process(int pid, int size, int duration) {
		this.pid = pid;
		if (pid >= 0)
			this.pname = PROC_NAMES.charAt(pid % PROC_NAMES.length());
		this.size = size;
		this.duration = duration;
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
		System.out.println(pname + ": is " + simTime + ", should remove at " + (timeAdded + duration));
		return simTime >= timeAdded + duration;
	}
	
}
