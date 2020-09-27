import java.util.Queue;
import java.util.LinkedList;
import java.io.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Hardware implements Runnable {

	public static int RAM_size;
	public static int RAM_Used;
	public static PQueue readyPQ; // The queue that follows SJF algorithm.
	public static Queue<PCB> readyQ; // The queue That follows FCFS algorithm.
	public static Queue<PCB> jobQ; // The job queue for the processes.
	public static Queue<PCB> waitQ; // The wait queue for the processes.
	public static Queue<PCB> TKQ; // Terminated or Killed process queue.
	public static int NOP; // Number of processes
	public static int clock;
	public static int internalClock;

	public Hardware() {

		this.RAM_size = 256000 - 16000; // The ram size is 256MB and there is 16MB used for the operating system.
		this.RAM_Used = 0;
		this.NOP = 0;
		this.clock = 0;
		this.internalClock = 0;

		readyPQ = new PQueue();
		readyQ = new LinkedList<PCB>();
		jobQ = new LinkedList<PCB>();
		waitQ = new LinkedList<PCB>();
		TKQ = new LinkedList<PCB>();
	}

	public void readfile() throws Exception {

		String PCB;
		BufferedReader br = new BufferedReader(new FileReader("/Users/dmc/Desktop/ProcessesFile/input.txt"));
		try {
			PCB = br.readLine();
			while (PCB != null) {
				int counter = 2;
				int arrCounter = 0;
				String[] temp = PCB.split(",");
				int[] cUse = new int[temp.length];
				int[] mUse = new int[temp.length];
				int[] ioUse = new int[temp.length];
				int PID = Integer.parseInt(temp[0]);
				String Pname = temp[1];
				int sum = 0;
				while (!temp[counter].equals("-1")) {
					int CPUUse = Integer.parseInt(temp[counter]);
					cUse[arrCounter] = CPUUse;
					counter++;
					int MemoryUse = Integer.parseInt(temp[counter]);
					mUse[arrCounter] = MemoryUse;
					counter++;
					int IOUse = Integer.parseInt(temp[counter]);
					ioUse[arrCounter] = IOUse;
					counter++;
					arrCounter++;
				}

				for (int i = 0; i < cUse.length; i++) {
					sum += cUse[i];
				}

				PCB Process = new PCB(PID, Pname, cUse, sum, mUse, ioUse);
				jobQ.add(Process);
				NOP++;
				PCB = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			br.close();
			System.out.println("Error" + e);
		}
	}

	public static void LongTerm() throws InterruptedException {

		if (!jobQ.isEmpty()) {
			while (RAM_Used < (0.8 * RAM_size) && !(jobQ.isEmpty())
					&& RAM_Used + (jobQ.peek().MemoryUse[0]) < (0.8 * RAM_size)) {
				clock++;
				PCB temp = jobQ.poll();
				temp.state = PCB.State.READY;
				temp.TimeReadyQ = clock;
				readyPQ.enqueue(temp, temp.CPUUse[0]);
				RAM_Used += temp.MemoryUse[0];
			}

		}
	}

	public static void checkClock() throws InterruptedException {
		internalClock = 0;
		LongTerm();
	}

	public static void deadLock() {
		
		if (!waitQ.isEmpty() && readyPQ.empty() && readyQ.isEmpty()) {

			int qCount = 0;
			PCB temp = waitQ.poll();
			int waitQSize = waitQ.size();
			while (!waitQ.isEmpty() && waitQSize > qCount) {
				PCB temp2 = waitQ.poll();
				if (temp.MemoryUse[temp.memoCounter] < temp2.MemoryUse[temp2.memoCounter]) { 
					waitQ.add(temp);
					temp = temp2;
				} else {
					waitQ.add(temp2);
				}
				qCount++;
			}
			if (!((RAM_Used - CPUExecution.killedUsedMemo(temp)) < 0)) {
				RAM_Used -= CPUExecution.killedUsedMemo(temp);
			} else {
				RAM_Used = 0;
			}
			temp.state = PCB.State.KILLED;
			temp.FinalST = clock;
			TKQ.add(temp);
		}
	}

	public static double cpuUtilization(PCB process) {

		return (process.CPUTime / (double) (process.CPUTime + process.IOTime)) * 100;
	}

	public static int waitingTime(PCB process) {

		return turnaroundTime(process) - process.TotalBursts;
	}

	public static int turnaroundTime(PCB process) {

		return (process.FinalST - process.TimeReadyQ);
	}
	
	public static void terminated() {
		if (readyPQ.empty() && readyQ.isEmpty()) {
			try {
				PrintWriter outputStream = new PrintWriter("/Users/dmc/Desktop/OUTp.txt");
				double sumOfTAT = 0; // the sum of terminated time for all processes.
				double sumOfWT = 0; // the sum of waiting time for all processes.
				int counterkilled=0;
				int counterter=0;
				for (int i = 0; i < NOP; i++) {
					PCB temp = TKQ.poll();
					if(temp.state == PCB.State.KILLED) {
						counterkilled++;
					}
						else {
						counterter++;	
						}
					
					System.out.println("Process ID: " + temp.PID);
					System.out.println("Process Name: " + temp.ProgramName);
					System.out.println("When it was loaded into the ready queue: " + temp.TimeReadyQ + " Millisecond");
					System.out.println("Number of times it was in the CPU: " + temp.CPUEntery);
					System.out.println("Total time spent in the CPU: " + temp.CPUTime + " Millisecond");
					System.out.println("Number of times it performed an IO: " + temp.IOEntery);
					System.out.println("Total time spent in performing IO: " + temp.IOTime + " Millisecond");
					System.out.println("Number of times it was waiting for memory: " + temp.CountWaitMemory);
					System.out.println("Time it terminated or was killed: " + temp.FinalST + " Millisecond");
					System.out.println("Final state: " + temp.state + "");
					System.out.println("CpuUtilization: " + cpuUtilization(temp) + "%");
					System.out.println("Waiting time: " + waitingTime(temp) + "");
					System.out.println("Turnaround time: " + turnaroundTime(temp) + "");
					System.out.println("======================================================================================");

					outputStream.println("Process ID: " + temp.PID);
					outputStream.println("Process Name: " + temp.ProgramName);
					outputStream.println("When it was loaded into the ready queue: " + temp.TimeReadyQ + " Millisecond");
					outputStream.println("Number of times it was in the CPU: " + temp.CPUEntery);
					outputStream.println("Total time spent in the CPU: " + temp.CPUTime + " Millisecond");
					outputStream.println("Number of times it performed an IO: " + temp.IOEntery);
					outputStream.println("Total time spent in performing IO: " + temp.IOTime + " Millisecond");
					outputStream.println("Number of times it was waiting for memory: " + temp.CountWaitMemory);
					outputStream.println("Time it terminated or was killed: " + temp.FinalST + " Millisecond");
					outputStream.println("Final state: " + temp.state + "");
					outputStream.println("CpuUtilization: " + cpuUtilization(temp) + "%");
					outputStream.println("Waiting time: " + waitingTime(temp) + "");
					outputStream.println("Turnaround time: " + turnaroundTime(temp) + "");
					outputStream.println("======================================================================================");

					
					sumOfTAT += turnaroundTime(temp);
					sumOfWT += waitingTime(temp);
				}
				System.out.println("The avrage waiting time = " + sumOfWT / NOP);
				System.out.println("The avrage trunaround time = " + sumOfTAT / NOP);
				System.out.print("Number of the processes = " + NOP);
				System.out.print("Number of the Killed processes = " + counterkilled);
				System.out.print("Number of the terminated processes = " + counterter);

				outputStream.println("The avrage waiting time = " + sumOfWT / NOP + "");
				outputStream.println("The avrage trunaround time = " + sumOfTAT / NOP + "");
				outputStream.println("Number of the processes = " + NOP);
				outputStream.println("Number of the Killed processes = " + counterkilled);
				outputStream.println("Number of the terminated processes = " + counterter);
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		try {
			LongTerm();
		} catch (InterruptedException e) {
			e.getMessage();
		}
	}

}
