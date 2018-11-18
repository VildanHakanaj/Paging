public class Job {

    private int jobNum;
    private int jobPageRef;
    private int timeStamp;
    public Job(int jobNum, int jobPageRef) {
        this.jobNum = jobNum;
        this.jobPageRef = jobPageRef;
        this.timeStamp = 0;

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

    public void setTimeStamp (int timeStamp){
        this.timeStamp = timeStamp;
    }

    public int getTimeStamp(){
        return timeStamp;
    }


}
