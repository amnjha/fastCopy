package code.project.fastCopy.processor;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author aman.jha
 *
 */
public interface JobProcessor
{

    /**
     * Executes a job as a background task
     * @param job {@link Job}
     */
    public default void executeJob(Job<?> job)
    {
        executeJob(job, null);
    }

    /**
     * Executes a job as a background task
     * @param job {@link Job}
     * @param callBack Callback function that is executed when job is completed
     */
    public void executeJob(Job<?> job, Function<Object, ?> callBack);

    /**
     * Executes a {@link Job}, as a background process with a specified timeout.
     * If this {@link Job} is not completed within the given timeout.
     * It might be possible that the {@link Job} may not be started due to already
     * running background task.
     *
     * This {@link Job} will be cancelled/ Interrupted after specified timeout
     * @param job {@link Job}
     * @param timeout timeout in milliseconds
     * @param timeUnit {@link TimeUnit}
     * @param callBack Callback function that is executed when job is completed
     */
    public void executeJob(Job<?> job, long timeout, TimeUnit timeUnit, Function<Object, ?> callBack);

}
