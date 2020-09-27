
public class PQNode {

	public PCB data;
	public int priority;
	public PQNode next;

	public PQNode() {
		next = null;
	}

	public PQNode(PCB e, int p) {
		data = e;
		priority = p;
	}

}