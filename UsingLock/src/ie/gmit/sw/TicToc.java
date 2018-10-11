package ie.gmit.sw;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TicToc {

    private static final String TIC_THREAD_NAME = "Tic";
    private static final String TOC_THREAD_NAME = "Toc";

    private static boolean runTicToc = true;
    private static int counter = 20;

    private static Lock lock = new ReentrantLock();
    private static Condition ticker = lock.newCondition();
    private static Condition tocker = lock.newCondition();

    private static final Runnable runner = () -> { while(runTicToc) say(); };

    /**
     * Main.
     *  Runs program until run time ends or counter becomes <= 0.
     * @param args no.
     */
    public static void main(String... args) {
        runTicToc = true;
        // set end time
        endIn(3);
        // create threads and give them names
        Thread t1 = new Thread(runner); t1.setName(TIC_THREAD_NAME);
        Thread t2 = new Thread(runner); t2.setName(TOC_THREAD_NAME);
        // fire up threads
        t1.start();
        t2.start();
    }

    /**
     * Prints out "Tic" and "Toc".
     *
     * Logic:
     *      - lock method,
     *      - check what should be printed ("Tic" or "Toc" printed by individual thread)
     *          - if counter even number then "Tic"
     *      - if needed make thread wait for another thread to arrive.
     *      -  do "Tic" or "Toc"
     *      - signal waiting thread if any
     *      - unlock method.
     */
    private static void say() {
        lock.lock();

        try {
            if( counter %2 == 0 && isTocker() ){ ticker.await(); }
            if( counter %2 != 0 && isTicker() ){ tocker.await(); }
        }
        catch (InterruptedException e) { e.printStackTrace(); }

        System.out.println(counter +": "+ Thread.currentThread().getName());
        if(--counter <= 0 ){ System.exit(-1); }

        if( isTicker() ){ ticker.signalAll(); }
        if( isTocker() ){ tocker.signalAll(); }

        lock.unlock();
    }

    /**
     * Checks if current thread is "Tocker"
     * @return true if thread is same as TOC_THREAD_NAME
     */
    private static boolean isTocker(){
        return Thread.currentThread().getName().equals(TOC_THREAD_NAME);
    }

    /**
     * Checks if current thread is "Ticker"
     * @return true if thread is same as TIC_THREAD_NAME
     */
    private static boolean isTicker(){
        return Thread.currentThread().getName().equals(TIC_THREAD_NAME);
    }

    /**
     * Sets run time.
     * @param sec how long "TicToc" should run in sec.
     */
    private static void endIn(int sec){
        new Thread( () -> {

            try { Thread.sleep(sec *1_000L); }
            catch (InterruptedException e) { e.printStackTrace(); }
            finally { runTicToc = false; }

        }).start();
    }

}


