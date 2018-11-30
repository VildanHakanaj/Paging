/*======================================================================================================================
|   Simulation of a paging system and the LRU and RANDOM algorithm for swaping mechanism
|
|   Name:           Job
|
|   Written by:     Vildan Hakanaj - November 2018
|
|   Written for:    COIS 3320 (Prof. Jacques Beland)Lab 3 Trent University Fall 2018.
|
|   Purpose:        To represent the jobs/Pages being run from the algorithm
|
|   usage:          Used by Paging Class
|
|   Subroutines/libraries required:
|       No Subroutines/libraries utilized outside of Java.util.*
|
|   References: The memberwise methods used in the Job and Paging classes were taken from the lab2 assignment
|               Joshua Croft: Originally coded the methods.
|
======================================================================================================================*/
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

    public String toString(){
       return "{" + this.jobNum + " : " + this.jobPageRef + "}";
    }

    public Job memberwiseClone() {
        return new Job(this.jobNum, this.jobPageRef);
    }
}
