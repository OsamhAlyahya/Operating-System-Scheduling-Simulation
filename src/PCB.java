
public class PCB {

	public int PID; // The process Identifier.
	public String ProgramName; // The process name.
	public int TimeReadyQ; // When it was loaded into the ready queue.
	public int CPUEntery; // Number of times it was in the CPU.
	public int CPUTime; // Total time spent in the CPU.
	public int IOEntery; // Number of times it performed an IO.
	public int IOTime; // Total time spent in performing IO.
	public int CountWaitMemory; // Number of times it was waiting for memory.
	public int FinalST; // Time it terminated or was killed.
	public int[] CPUUse; // The use of CPU for the process.
	public int TotalBursts; // To save the CPU use of a process
	public int[] MemoryUse; // The use of memory for the process.
	public int[] IOUse; // The use of IO for the process.
	public int memoCounter;
	State state; // The state of the process.

	public enum State {
		NEW, READY, RUNNING, WAITINGIO, KILLED, TERMINATED, WAITINGMEMO
	}

	public PCB(int PID, String Pname, int[] CPUUse, int TotalBursts, int[] MemoryUse, int[] IOUse) {

		this.PID = PID;
		this.ProgramName = Pname;
		this.TimeReadyQ = 0;
		this.CPUEntery = 0;
		this.CPUTime = 0;
		this.IOEntery = 0;
		this.IOTime = 0;
		this.CountWaitMemory = 0;
		this.FinalST = 0; 
		this.memoCounter = 0;
		this.CPUUse = CPUUse;
		this.MemoryUse = MemoryUse;
		this.IOUse = IOUse;
		this.TotalBursts = TotalBursts;
		state = State.NEW;
	}

}
