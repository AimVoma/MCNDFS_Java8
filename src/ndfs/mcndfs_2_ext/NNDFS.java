package ndfs.mcndfs_2_ext;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import graph.Graph;
import graph.GraphFactory;
import graph.State;
import ndfs.NDFS;

/**
 * Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
 * worker class.
 */
public class NNDFS implements NDFS {

	private final Map<State, Integer> globalCount = new HashMap<>();
	private final Map<State, Boolean> globalRed = new HashMap<>();

	class CycleFoundException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	class Worker implements Callable<Integer> {

		private final Graph graph;
		private final Colors colors = new Colors();

		public Worker(File promelaFile) throws FileNotFoundException {
			this.graph = GraphFactory.createGraph(promelaFile);
		}

		public Integer call() throws InterruptedException {
			try {
				dfsBlue(graph.getInitialState());
			} catch (CycleFoundException CFE) {
				return 1;
			} catch (InterruptedException e) {
				throw e;
			}
			return 0;
		}

		private List<State> getShuffledPost(State s) {
			List<State> l = graph.post(s);
			Collections.shuffle(l);
			return l;
		}

		private void dfsBlue(State s) throws CycleFoundException, InterruptedException {
			boolean allred = true;
			colors.color(s, Color.CYAN);

			List<State> succNodes = getShuffledPost(s);
			for (State t : succNodes) {
				if (colors.hasColor(t, Color.CYAN) && (s.isAccepting() || t.isAccepting())) {
					throw new CycleFoundException();
				}
				
				boolean tempRed;
				synchronized (globalRed) {
					Boolean b = globalRed.get(t);
					tempRed = b==null? false:b.booleanValue();
				}
				
				if (colors.hasColor(t, Color.WHITE) && !tempRed) {
					dfsBlue(t);
				}
				
				synchronized (globalRed) {
					Boolean b = globalRed.get(t);
					tempRed = b==null? false:b.booleanValue();
				}
				
				if (!tempRed) {
					allred = false;
				}
			}
			
			if (allred) {
				synchronized (globalRed) {
					globalRed.put(s, true);
				}
			} else if (s.isAccepting()) {
				synchronized (globalCount) {
					int count;
					Integer t = globalCount.get(s);
					count = t==null? 0:t.intValue();
					globalCount.put(s, count + 1);
				}

				dfsRed(s);
			}

			colors.color(s, Color.BLUE);
		}

		private void dfsRed(State s) throws CycleFoundException, InterruptedException {
			colors.color(s, Color.PINK);

			List<State> succNodes = getShuffledPost(s);
			for (State t : succNodes) {
				if (colors.hasColor(t, Color.CYAN)) {
					throw new CycleFoundException();
				}
				
				boolean tempRed;
				synchronized (globalRed) {
					Boolean b = globalRed.get(t);
					tempRed = b==null? false:b.booleanValue();
				}

				if (!colors.hasColor(t, Color.PINK) && !tempRed) {
					dfsRed(t);
				}
			}

			if (s.isAccepting()) {
				synchronized (globalCount) {
					int count;
					Integer t = globalCount.get(s);
					count = t==null? 0:t.intValue();
					globalCount.put(s, count - 1);
				}

				synchronized (globalCount) {
					while (true) {
						int count;
						Integer t = globalCount.get(s);
						count = t==null? 0:t.intValue();
						if (count > 0)
							globalCount.wait();
						else 
							break;
					}
					globalCount.notifyAll();
				}
			}

			synchronized (globalRed) {
				globalRed.put(s, true);
			}
		}
	}

	private final List<Worker> workers = new ArrayList<>();

	/**
	 * Constructs an NDFS object using the specified Promela file.
	 *
	 * @param promelaFile
	 *            the Promela file.
	 * @throws FileNotFoundException
	 *             is thrown in case the file could not be read.
	 */
	public NNDFS(File promelaFile, int nrWorkers) throws FileNotFoundException {
		for (int i = 0; i < nrWorkers; i++) {
			workers.add(new Worker(promelaFile));
		}
	}

	@Override
	public boolean ndfs() {
		boolean foundCycle = false;
		ExecutorService ex_service = Executors.newFixedThreadPool(workers.size());
        CompletionService<Integer> cm_service = new ExecutorCompletionService<Integer>(ex_service);

		for (int i = 0; i < workers.size(); i++) {
			cm_service.submit(workers.get(i));
		}
		
		/**
         * We expect the first coming thread in a form of <Future> that terminated with a 
         * CycleFoundException(Found Cycle in graph) or execution failure
         * (Interrupted&Execution Exception)
         */
        try {
            Future<Integer> future = cm_service.take();
            int result = future.get();
            foundCycle = (result > 0) ? true:false;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        ex_service.shutdownNow();

        return foundCycle;
	}
}
