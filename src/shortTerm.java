
public class shortTerm implements Runnable {

	public static void shortTermCaller() throws InterruptedException {

		int count = 0;
		while (!Hardware.jobQ.isEmpty() || !Hardware.readyPQ.empty() || !Hardware.readyQ.isEmpty() || !Hardware.waitQ.isEmpty()) {
			if (!Hardware.jobQ.isEmpty() && count == 100) {
				Hardware.LongTerm();
				count = 0;
			}

			int counter = 1;
			while (!Hardware.readyPQ.empty() || !Hardware.readyQ.isEmpty()) {
				if (!Hardware.readyPQ.empty() && counter < 7) {
					CPUExecution.executeCPU(Hardware.readyPQ.dequeue().data);

				} else if (!Hardware.readyQ.isEmpty() && counter >= 7 && counter <= 10) {
					CPUExecution.executeCPU(Hardware.readyQ.poll());

				} else if (counter > 10) {
					counter = 0;

				}

				if (!Hardware.jobQ.isEmpty() && count == 100) {
					Hardware.LongTerm();
					count = 0;
				}
				count++;
				counter++;
			}
			count++;
			if (!Hardware.waitQ.isEmpty() && Hardware.readyQ.isEmpty() && Hardware.readyPQ.empty()) {
				Hardware.deadLock();
			}

		}
		Hardware.terminated();
		
	}

	@Override
	public void run() {
		try {
			shortTermCaller();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
