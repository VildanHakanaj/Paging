public class Job {

    private int jobNum;
    private int jobPageRef;

    public Job(int jobNum, int jobPageRef) {
        this.jobNum = jobNum;
        this.jobPageRef = jobPageRef;
    }

    public int getJobNum() {
        return jobNum;
    }

    public void setJobNum(int jobNum) {
        this.jobNum = jobNum;
    }

    public int getJobPageRef() {
        return jobPageRef;
    }

    public void setJobPageRef(int jobPageRef) {
        this.jobPageRef = jobPageRef;
    }
}
