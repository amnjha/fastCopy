package code.project.fastCopy.processor;

import java.util.concurrent.Callable;

/**
 * @author aman.jha
 * @param <V> The Return Type of thread
 *
 */
public abstract class Job<V> implements Callable<V>
{
    private final String jobName;

    /**
     * @param jobName String
     */
    public Job(String jobName)
    {
        this.jobName = jobName;
    }

    /**
     * @return the jobName
     */
    public String getJobName()
    {
        return jobName;
    }

    @Override
    public final V call() throws Exception
    {
        return doJob();
    }

    /**
     * @return Return Value for the Job Done
     * @throws Exception - When the Implementation of Job causes an exception
     *
     */
    public abstract V doJob() throws Exception;

}
