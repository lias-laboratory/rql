package fr.ensma.lias.rql.dfg;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is a specialized extension of the ThreadPoolExecutor class.
 *
 * Two functionalities had been added to this subclass. 1) The execute method of
 * the ThreadPoolExecutor will block in case the queue is full and only unblock
 * when the queue is dequeued - that is a task that is currently in the queue is
 * removed and handled by the ThreadPoolExecutor. 2) Client code can await for
 * the event of all tasks beeing run to conclusion. Client code which actively
 * chose to wait for this occurrence should call await on the instance of his
 * ThreadPoolExecutor. This differs from awaitTermination as it does not require
 * any call to shutdown.
 *
 * Code example:
 *
 * NotifyingBlockingThreadPoolExecutor threadPoolExecutor = new
 * NotifyingBlockingThreadPoolExecutor(5, ,10, 15, TimeUnit.SECONDS);
 *
 * for (int i = 0; i < 5000; i++) { threadPoolExecutor.execute(...) }
 *
 * try { threadPoolExecutor.await(); } catch (InterruptedException e) { //
 * Handle error }
 *
 * System.out.println("Done!");
 *
 * The example above shows how 5000 tasks are run within 5 threads. The line
 * with 'System.out.println("Done!");' will not run until such a time when all
 * the tasks given to the thread pool have concluded. their run.
 *
 * This subclass of ThreadPoolExecutor also takes away the max threads
 * capabilities of the ThreadPoolExecutor superclass and internally sets the
 * amount of maximum threads to be the size of the core threads. This is done
 * since threads over the core size and under the max are instantiated only once
 * the queue is full, but the NotifyingBlockingThreadPoolExecutor will block
 * once the queue is full.
 *
 * @author Yaneeve Shekel & Amir Kirsh
 */
public class NotifyingBlockingThreadPoolExecutor extends ThreadPoolExecutor {

	/**
	 * Counts the number of current tasks in process
	 */
	private AtomicInteger tasksInProcess = new AtomicInteger();

	/**
	 * This is the Synchronizer instance that is used in order to notify all
	 * interested code of when all the tasks that have been submitted to the
	 * execute() method have run to conclusion. This notification can occur a
	 * numerous amount of times. It is all up to the client code. Whenever the
	 * ThreadPoolExecutor concludes to run all the tasks the Synchronizer object
	 * will be notified and will in turn notify the code which is waiting on it.
	 */
	private Synchronizer synchronizer = new Synchronizer();

	private final Semaphore semaphore;

	/**
	 * This constructor is used in order to maintain the first functionality
	 * specified above. It does so by using an ArrayBlockingQueue and the
	 * BlockThenRunPolicy that is defined in this class. Using this constructor,
	 * waiting time on new task insertion is unlimited.
	 * 
	 * @param poolSize      is the amount of threads that this pool may have alive
	 *                      at any given time.
	 * @param queueSize     is the size of the queue. This number should be at least
	 *                      as the pool size to make sense (otherwise there are
	 *                      unused threads), thus if the number sent is smaller, the
	 *                      poolSize is used for the size of the queue. Recommended
	 *                      value is twice the poolSize.
	 * @param keepAliveTime is the amount of time after which an inactive thread is
	 *                      terminated.
	 * @param unit          is the unit of time to use with the previous parameter.
	 */
	public NotifyingBlockingThreadPoolExecutor(int poolSize, int queueSize, long keepAliveTime, TimeUnit unit) {

		super(poolSize, // Core size
				poolSize, // Max size
				keepAliveTime, unit, new ArrayBlockingQueue<Runnable>(Math.max(poolSize, queueSize)) // not
		// smaller
		// than
		// the
		// poolSize
		// (to
		// avoid
		// redundant
		// threads)
		);
		this.semaphore = new Semaphore(Math.max(poolSize, queueSize), true);
		super.allowCoreThreadTimeOut(true); // Time out the core threads.
	}

	/**
	 * Before calling super's version of this method, the amount of tasks which are
	 * currently in process is first incremented.
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor#execute(Runnable)
	 */
	@Override
	public void execute(Runnable task) {
		// count a new task in process
		tasksInProcess.incrementAndGet();
		boolean acquired = false;
		do {
			try {
				semaphore.acquire();
				acquired = true;
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}
		} while (!acquired);

		try {
			super.execute(task);
		} catch (RuntimeException e) { // specifically handle
			// RejectedExecutionException
			tasksInProcess.decrementAndGet();
			e.printStackTrace();
			semaphore.release();
			throw e;
		} catch (Error e) {
			tasksInProcess.decrementAndGet();
			e.printStackTrace();
			semaphore.release();
			throw e;
		}
	}

	/**
	 * After calling super's implementation of this method, the amount of tasks
	 * which are currently in process is decremented. Finally, if the amount of
	 * tasks currently running is zero the synchronizer's signallAll() method is
	 * invoked, thus anyone awaiting on this instance of ThreadPoolExecutor is
	 * released.
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(Runnable,
	 *      Throwable)
	 */
	@Override
	protected void afterExecute(Runnable r, Throwable t) {

		super.afterExecute(r, t);

		// synchronizing on the pool (and actually all its threads)
		// the synchronization is needed to avoid more than one signal if two or
		// more
		// threads decrement almost together and come to the if with 0 tasks
		// together
		synchronized (this) {
			tasksInProcess.decrementAndGet();
			if (tasksInProcess.intValue() == 0) {
				synchronizer.signalAll();
			}
		}
		semaphore.release();
	}

	/**
	 * Internally calls on super's setCorePoolSize and setMaximumPoolSize methods
	 * with the given method argument.
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor#setCorePoolSize(int)
	 */
	@Override
	public void setCorePoolSize(int corePoolSize) {
		super.setCorePoolSize(corePoolSize);
		super.setMaximumPoolSize(corePoolSize);
	}

	/**
	 * Does Nothing!
	 * 
	 * @throws UnsupportedOperationException in any event
	 * @see java.util.concurrent.ThreadPoolExecutor#setMaximumPoolSize(int)
	 */
	@Override
	public void setMaximumPoolSize(int maximumPoolSize) {
		throw new UnsupportedOperationException("setMaximumPoolSize is not supported.");
	}

	/**
	 * Does Nothing! MUST NOT CHANGE OUR BUILT IN RejectedExecutionHandler
	 * 
	 * @throws UnsupportedOperationException in any event
	 * @see java.util.concurrent.ThreadPoolExecutor#setRejectedExecutionHandler(RejectedExecutionHandler)
	 */
	@Override
	public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
		throw new UnsupportedOperationException("setRejectedExecutionHandler is not allowed on this class.");
	}

	/**
	 * A blocking wait for this ThreadPool to be in idle state, which means that
	 * there are no more tasks in the Queue or currently executed by one of the
	 * threads. BE AWARE that this method may get out from blocking state when a
	 * task is currently sent to the ThreadPool not from this thread context. Thus
	 * it is not safe to call this method in case there are several threads feeding
	 * the TreadPool with tasks (calling execute). The safe way to call this method
	 * is from the thread that is calling execute and when there is only one such
	 * thread. Note that this method differs from awaitTemination, as it can be
	 * called without shutting down the ThreadPoolExecuter.
	 * 
	 * @throws InterruptedException when the internal condition throws it.
	 */
	public void await() throws InterruptedException {
		synchronizer.await();
	}

	/**
	 * A blocking wait for this ThreadPool to be in idle state or a certain timeout
	 * to elapse. Works the same as the await() method, except for adding the
	 * timeout condition.
	 * 
	 * @see NotifyingBlockingThreadPoolExecutor#await() for more details.
	 * @return false if the timeout elapsed, true if the synch event we are waiting
	 *         for had happened.
	 * @throws InterruptedException when the internal condition throws it.
	 */
	public boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException {
		return synchronizer.await(timeout, timeUnit);
	}

	// ====================================================================
	// start of inner private class Synchronizer
	// ====================================================================

	/**
	 * This inner class serves to notify all interested parties that the
	 * ThreadPoolExecutor has finished running all the tasks given to its execute
	 * method.
	 */
	private class Synchronizer {

		private final Lock lock = new ReentrantLock();
		private final Condition done = lock.newCondition();
		private boolean isDone = false;

		/**
		 * This PRIVATE method allows the ThreadPoolExecutor to notify all interested
		 * parties that all tasks given to the execute method have run to conclusion.
		 */
		private void signalAll() {

			lock.lock(); // MUST lock!
			try {
				isDone = true; // To help the await method ascertain that it has
				// not waken up 'spuriously'
				done.signalAll();
			} finally {
				lock.unlock(); // Make sure to unlock even in case of an
				// exception
			}
		}

		/**
		 * This is the inner implementation for supporting the
		 * NotifyingBlockingThreadPoolExecutor.await().
		 * 
		 * @see NotifyingBlockingThreadPoolExecutor#await() for details.
		 * @throws InterruptedException when the internal condition throws it.
		 */
		public void await() throws InterruptedException {

			lock.lock(); // MUST lock!
			try {
				while (!isDone) { // Ascertain that this is not a 'spurious
					// wake-up'
					done.await();
				}
			} finally {
				isDone = false; // for next time
				lock.unlock(); // Make sure to unlock even in case of an
				// exception
			}
		}

		/**
		 * This is the inner implementation for supporting the
		 * NotifyingBlockingThreadPoolExecutor.await(timeout, timeUnit).
		 * 
		 * @see NotifyingBlockingThreadPoolExecutor#await(long, TimeUnit) for details.
		 * @throws InterruptedException when the internal condition throws it.
		 */
		public boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException {

			boolean await_result = false;
			lock.lock(); // MUST lock!
			boolean localIsDone;
			try {
				await_result = done.await(timeout, timeUnit);
			} finally {
				localIsDone = isDone;
				isDone = false; // for next time
				lock.unlock(); // Make sure to unlock even in case of an
				// exception
			}
			// make sure we return true only if done!
			return await_result && localIsDone;
		}
	}

	// ====================================================================
	// end of inner class Synchronizer
	// ====================================================================

}