import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RunProcess {
	public static void main(String[] args) throws RemoteException, AlreadyBoundException, MalformedURLException,
			InterruptedException, NotBoundException {
		if (args.length != 3) {
			System.out.println("Usage: RunProcess name total initial");
			System.exit(-1);
		}

		int i = Integer.parseInt(args[0]);
		int n = Integer.parseInt(args[1]);
		int b = Integer.parseInt(args[2]);

		Process process = new Process(i, n, b);

		Naming.bind("rmi://localhost:1099/p" + i, process);

		log(i, "Process bound");

		// Wait 2 sec. so all registers can register themselves with the rmiregistry
		Thread.sleep(2000);

		// Link other processes
		for (int j = 0; j < n; j++) {
			process.setActor(j, (Actor) Naming.lookup("rmi://localhost:1099/p" + j));
		}

		log(i, "Start coordinating");

		int iterations = 0;

		// Run the coordinate function in a loop until it returns 0
		while (process.coordinate() != 0) {
			// Deliberately empty. Coordinate until you're done..
			iterations++;
		}

		log(i, "Done");
		System.out.println(iterations);
	}

	private static void log(int i, String m) {
		System.err.println(i + ": " + m);
	}
}
