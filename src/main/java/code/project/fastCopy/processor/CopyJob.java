package code.project.fastCopy.processor;

public class CopyJob extends Job<Void> {
    /**
     * @param jobName String
     */
    public CopyJob(String jobName) {
        super(jobName);
    }

    /**
     * @return Return Value for the Job Done
     * @throws Exception - When the Implementation of Job causes an exception
     */
    @Override
    public Void doJob() throws Exception {

        return null;
    }
}
