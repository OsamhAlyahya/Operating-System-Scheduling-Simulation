
public class OS {

	public static void main(String[] args) throws Exception {

		Hardware h1 = new Hardware();
		shortTerm cp2 = new shortTerm();
		Thread t1 = new Thread(h1);
		Thread t2 = new Thread(cp2);

		h1.readfile();
		t1.start();
		//t1.sleep(200); // go for sleep for 200ms
		t2.start();
	}
}
