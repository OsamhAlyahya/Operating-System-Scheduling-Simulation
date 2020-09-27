
public class PQueue {

	private int size;
	private PQNode head;

	public PQueue() {
		head = null;
		size = 0;
	}

	public int length() {
		return size;
	}

	public boolean full() {
		return false;
	}

	public boolean empty() {
		return head == null;
	}

	public void enqueue(PCB Process, int CPUUse) {
		PQNode tmp = new PQNode(Process, CPUUse);
		if ((size == 0) || (CPUUse < head.priority)) {
			tmp.next = head;
			head = tmp;
		} else {
			PQNode p = head;
			PQNode q = null;
			while ((p != null) && (CPUUse >= p.priority)) {
				q = p;
				p = p.next;
			}
			tmp.next = p;
			q.next = tmp;
		}
		size++;
	}

	public PQNode dequeue() {
		PQNode node = head;
		head = head.next;
		size--;
		return node;
	}

}
