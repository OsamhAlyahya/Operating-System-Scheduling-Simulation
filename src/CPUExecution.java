
public class CPUExecution {

	public CPUExecution() {

	}
	public static void executeCPU(PCB Process) throws InterruptedException {

		Process.memoCounter++;
		if (Process.CPUUse[Process.CPUEntery] > 0) {
			Process.state = PCB.State.RUNNING;
			while (Process.CPUUse[Process.CPUEntery] > 0) {
				Hardware.clock++;
				Hardware.internalClock++; // to increment the clock for check the long term scheduler.

				Process.CPUTime++;
				Process.CPUUse[Process.CPUEntery]--;
				
			}
			Process.CPUEntery++;
			if (Hardware.internalClock >= 100) {
				Hardware.checkClock();
			}
		}

		if (Process.IOUse[Process.IOEntery] > 0) { // If there is IO request we execute it.

			Process.state = PCB.State.WAITINGIO;
			while (Process.IOUse[Process.IOEntery] > 0) {
				Hardware.clock++;
				Hardware.internalClock++; // to increment the clock for check the long term scheduler.

				Process.IOTime++;
				Process.IOUse[Process.IOEntery]--;
			}
			Process.IOEntery++;
			Process.state = PCB.State.READY; // back to the ready Q.
			if (Hardware.internalClock >= 100) {
				Hardware.checkClock();
			}
		}

		if (Process.CPUUse[Process.CPUEntery] == 0 && Process.IOUse[Process.IOEntery] == 0) { // check that there is no
			// more execution left.

			if (!((Hardware.RAM_Used - TerminatedUsedMemo(Process)) < 0)) {
				Hardware.RAM_Used -= TerminatedUsedMemo(Process);
			} else {
				Hardware.RAM_Used = 0;
			}
			Process.state = PCB.State.TERMINATED;
			Process.FinalST = Hardware.clock;
			Hardware.TKQ.add(Process); // we save the process in Q terminated or killed.

			if (!Hardware.waitQ.isEmpty()) // check wait Q if that waitQ not empty and there is enough memory size.
				if (Hardware.RAM_Used < (0.8 * Hardware.RAM_size) && Hardware.RAM_Used
						+ (Hardware.waitQ.peek().MemoryUse[Hardware.waitQ.peek().memoCounter]) < (0.8
								* Hardware.RAM_size)) {
					PCB temp = Hardware.waitQ.poll();

					temp.state = PCB.State.READY;
					Hardware.RAM_Used += temp.MemoryUse[temp.memoCounter];

					if (temp.memoCounter < 4) { // our condition for method to Demote.
						Hardware.readyQ.add(temp);

					} else {  // our condition for method to upgrade.
						Hardware.readyPQ.enqueue(temp, temp.CPUUse[temp.memoCounter]);

					}
				}
		}
		
		else {
			if (Hardware.RAM_Used < (0.8 * Hardware.RAM_size) // check memory for other process cycle
					&& Hardware.RAM_Used + (Process.MemoryUse[Process.memoCounter]) < (0.8 * Hardware.RAM_size)) {

				Hardware.RAM_Used += Process.MemoryUse[Process.memoCounter];
				if (Process.memoCounter < 4) { // our condition for method to Demote.

					Hardware.readyQ.add(Process);

				} else { // our condition for method to upgrade.

					Hardware.readyPQ.enqueue(Process, Process.CPUUse[Process.memoCounter]);
				}
			} else {
				Process.CountWaitMemory++;
				Process.state = PCB.State.WAITINGMEMO;
				Hardware.waitQ.add(Process);
				Hardware.deadLock();
			}

		}
	}

	public static int TerminatedUsedMemo(PCB Process) {
		int sum = 0;
		for (int i = 0; i < Process.MemoryUse.length; i++) {
			sum += Process.MemoryUse[i];
		}
		return sum;
	}

	public static int killedUsedMemo(PCB Process) {
		int sum = 0;
		for (int i = 0; i < Process.memoCounter; i++) {
			sum += Process.MemoryUse[i];
		}
		return sum;
	}

}
