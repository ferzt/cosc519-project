
public class ProcessReserve extends Process {
	ProcessReserve(int size) {
		super(-2, size, 69);
		pname = '#';
	}
	
	public boolean isItTimeToGo(int simTime) {
		return false;
	}
}
