package code.project.fastCopy.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author aman.jha
 *
 */

public class ParallelJobProcessor implements JobProcessor
{

    // Bill Pugh Singleton
    private static class BackGroundJobProcessorInitializer
    {
        private static final ParallelJobProcessor PARALLEL_JOB_PROCESSOR = new ParallelJobProcessor();
    }

    /**
     * @return BackGroundJobProcessor {@link ParallelJobProcessor}
     */
    public static ParallelJobProcessor getInstance()
    {
        return BackGroundJobProcessorInitializer.PARALLEL_JOB_PROCESSOR; // Bill Pugh Singleton
    }

    private final ExecutorService					executor		= Executors.newFixedThreadPool(1,
            new ThreadFactoryBuilder().setNameFormat("copy-worker-%d").build());

    private final BlockingQueue<JobElement>			BLOCKING_QUEUE	= new LinkedBlockingQueue<>();
    private final BlockingQueue<TemporalJobElement>	DELAYED_QUEUE	= new DelayQueue<>();
    private final Consumer							BLOCKING_CONSUMER;
    private final Consumer							DELAYED_CONSUMER;

    private static final int						RETRY_COUNT		= 3;

    private static final Logger						LOGGER			= LoggerFactory.getLogger("PERSISTENCE");

    /**
     *
     */
    private ParallelJobProcessor()
    {
        BLOCKING_CONSUMER = new Consumer(BLOCKING_QUEUE);
        DELAYED_CONSUMER = new Consumer(DELAYED_QUEUE);
    }

    /**
     * Executes a job as a background task
     * @param job {@link Job}
     * @param callBack Callback function that is executed when job is completed
     */
    @Override
    public void executeJob(Job<?> job, Function<Object, ?> callBack)
    {
        Future<?> future = executor.submit(job);
        JobElement element = new JobElement(future, job.getJobName(), callBack);
        pushToQueue(element);
    }

    /**
     * Executes a {@link Job}, as a background process with a specified timeout.
     * If this {@link Job} is not completed within the given timeout.
     * It might be possible that the {@link Job} may not be started due to already
     * running background task.
     *
     * This {@link Job} will be cancelled/ Interrupted after specified timeout
     * @param job {@link Job}
     * @param timeout timeout in milliseconds
     * @param callBack Callback function that is executed when job is completed
     */
    @Override
    public void executeJob(Job<?> job, long timeout, TimeUnit timeUnit, Function<Object, ?> callBack)
    {
        Future<?> future = executor.submit(job);
        TemporalJobElement element = new TemporalJobElement(future, job.getJobName(), timeout, timeUnit, callBack);
        pushToQueue(element);
    }

    /**
     *
     * @param element {@link JobElement}
     */
    private void pushToQueue(JobElement element)
    {
        if (!((element instanceof TemporalJobElement && DELAYED_QUEUE.offer((TemporalJobElement) element))
                || (!(element instanceof TemporalJobElement) && BLOCKING_QUEUE.offer(element))))
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        LOGGER.info("");
                        if (element instanceof TemporalJobElement)
                            DELAYED_QUEUE.put((TemporalJobElement) element);
                        else
                            BLOCKING_QUEUE.put(element);
                    }
                    catch (InterruptedException e)
                    {
                        LOGGER.error("Failed to put Element in batchQueue upon failed offer for batch : ",
                                element.getJobName(), e);
                        retryOnFailure(element);
                    }
                }
            }).start();
        }
    }

    /**
     *
     * @param jobElement {@link JobElement}
     */
    private void retryOnFailure(JobElement jobElement)
    {
        int count = 0;
        while (count < RETRY_COUNT)
        {
            try
            {
                count++;
                if (jobElement instanceof TemporalJobElement)
                    DELAYED_QUEUE.put((TemporalJobElement) jobElement);
                else
                    BLOCKING_QUEUE.put(jobElement);
                break;
            }
            catch (InterruptedException e)
            {
                LOGGER.error("Trial {} failed, to put Element in batchQueue upon failed offer for batch : {}; {}",
                        count, jobElement.getJobName(), (count < RETRY_COUNT) ? "Retrying again" : "", e);
            }
        }
    }

    /**
     *
     * @author aman.jha
     */
    class JobElement
    {
        private final Future<?>				value;
        private final String				jobName;
        private final Function<Object, ?>	callback;

        public JobElement(Future<?> value, String jobName, Function<Object, ?> callback)
        {
            this.value = value;
            this.jobName = jobName;
            this.callback = callback;
        }

        /**
         * @return the value
         */
        public Future<?> getValue()
        {
            return value;
        }

        /**
         * @return the jobName
         */
        public String getJobName()
        {
            return jobName;
        }

        /**
         * @return the callback
         */
        public Function<Object, ?> getCallback()
        {
            return callback;
        }

    }

    /**
     *
     * @author aman.jha
     *
     */
    class TemporalJobElement extends JobElement implements Delayed
    {
        private long expirationTime;

        /**
         * @param value {@link Object}
         * @param jobName String
         * @param delay  The time to expire from now.
         * @param unit {@link TimeUnit}
         * @param callBack Call-Back function
         */
        public TemporalJobElement(Future<?> value, String jobName, long delay, TimeUnit unit,
                                  Function<Object, ?> callBack)
        {
            super(value, jobName, callBack);
            long delayInMillis = unit.convert(delay, TimeUnit.MILLISECONDS);
            expirationTime = System.currentTimeMillis() + delayInMillis;
        }

        @Override
        public int compareTo(Delayed o)
        {
            if (this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS))
                return 1;
            else if (this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS))
                return -1;
            else
                return 0;

        }

        @Override
        public long getDelay(TimeUnit unit)
        {
            long timeInMillis = expirationTime - System.currentTimeMillis();
            return unit.convert(timeInMillis, TimeUnit.MILLISECONDS);
        }

    }

    /**
     * Consumer for  BLOCKING_QUEUE, and DELAYED_QUEUE
     * The Consumer is used to log any exception that might arise during operation execution.
     * Multiple instances of this thread should be spawned for each QUEUE
     *
     * @author aman.jha
     */
    class Consumer extends Thread
    {
        private final AtomicBoolean					continueRunning	= new AtomicBoolean(true);
        private BlockingQueue<? extends JobElement>	queue;

        /**
         *
         * @param queue BlockingQueue<? extends JobElement>
         */
        public Consumer(BlockingQueue<? extends JobElement> queue)
        {
            this.queue = queue;
        }

        /**
         *
         */
        public void stopConsumer()
        {
            continueRunning.set(false);
        }

        @Override
        public void run()
        {
            Future<?> future = null;
            JobElement element = null;
            Function<Object, ?> callback = null;
            while (continueRunning.get())
            {
                element = queue.poll();
                if (element != null)
                {
                    future = element.getValue();
                    callback = element.getCallback();
                    try
                    {
                        if (element instanceof TemporalJobElement)
                            cancelTask(future);

                        if (!future.isCancelled())
                        {
                            future.get();
                            if (callback != null)
                            {
                                callback.apply(future.get());
                                callback = null;
                            }
                            future = null;
                        }
                    }
                    catch (InterruptedException e)
                    {
                        LOGGER.error("[HARMLESS] - Thread Interrupted While waiting to get future result ",
                                e.getMessage(), e);
                    }
                    catch (ExecutionException e)
                    {
                        LOGGER.error("Exception Occured While Executing Background Process with Name : "
                                + element.getJobName(), e.getMessage(), e);
                    }
                }
                element = null;
                try
                {
                    Thread.sleep(500);
                }
                catch (@SuppressWarnings("unused")
                        InterruptedException e)
                {
                    LOGGER.warn("[HARMLESS] - Error while Thread.sleep : Thread Interrupted");
                }
            }
        }

        private boolean cancelTask(Future<?> future)
        {
            if (!future.isDone())
                return future.cancel(true);
            return false;
        }

    }

    /**
     * Initialization method to do the post bean construction steps.
     */
    @PostConstruct
    public void initialize()
    {
        BLOCKING_CONSUMER.setName("BLOCKING-QUEUE-CONSUMER");
        BLOCKING_CONSUMER.setName("DELAYED-QUEUE-CONSUMER");

        BLOCKING_CONSUMER.start();
        DELAYED_CONSUMER.start();
    }

    /**
     * Clean - Up Method before the bean is destroyed.
     * @param interruptThreads {@link Boolean} whether the submitted jobs are to be interrupted or not.
     */
    @PreDestroy
    public void cleanUp(boolean interruptThreads)
    {
        /**
         * Stop The Consumer Threads,
         * and Stop the ExecutorService(Interrupting all running processes)
         */
        BLOCKING_CONSUMER.stopConsumer();
        DELAYED_CONSUMER.stopConsumer();
        if(interruptThreads)
        	executor.shutdownNow();
        else
        	executor.shutdown();
    }

}