/*======================================================================================================================
|   Simulation of a paging system and the LRU and RANDOM algorithm for swaping mechanism
|
|   Name:           Paging
|
|   Written by:     Vildan Hakanaj - November 2018
|
|   Written for:    COIS 3320 (Prof. Jacques Beland)Lab 3 Trent University Fall 2018.
|
|   Purpose:        To simulate how memory management system works. Given files to load in memory.
|                   It present two different algorithm for decision making on what job to swap and see
|                   the different output results
|
|   assumptions:    The assumptions i made for this Lab were:
|                     1. After a job is terminated it will not show up again so i don't place it in the bad job array
|                     2. When using the random algo to find the index of a job i assume that the physical will always be
|                        full
|
|   usage:          Used by Main.main()
|
|   Subroutines/libraries required:
|       No Subroutines/libraries utilized outside of Java.util.*
|
======================================================================================================================*/

import java.io.*;
import java.util.*;

public class Paging {
  //region Variable declaration.
  //region CSV file
  private String filePath;    //The path to the file
  private ArrayList<Job> jobs;    //Contains the jobs we get from the csv file
  //endregion
  //region MEMORY
  Memory swap;
  Memory physical;
  //endregion
  //region Simulation variables
  private int clock;                                  //The simulation clock
  private Job currentJob;                             //Contains the current job we are working on
  private int pageHits;                               //Number of page hits
  private int firstLoad;                              //Counter for the first loaded pages
  private int pageFaults;                             //Counter for the job that are found in swap.
  private int completed;
  private int failed;
  private ArrayList<Integer> badJobs = new ArrayList<>(); //Contains all the jobs that have been terminated.
  private int choice;
  //endregion
  //region Constant Declarations

  private final int TERMINATE = -999;           //To check if the job is finished
  private final int PHYSICAL_MEMORY_SIZE = 10;  //The size of the array
  private final int SWAP_MEMORY_SIZE = 15;      //The size of the array
  private String DELIMITER = ",";               //Divide the string with the ,
  //This two constants choose between two algorithms
  private final int LRU =  1;
  private final int RANDOM = 2;
  private final int FILE_COUNT = 6;             //Number of files to run
  //endregion
  //endregion

  public Paging() {
    resetVar();
    this.swap = new Memory(SWAP_MEMORY_SIZE);
    this.physical = new Memory(PHYSICAL_MEMORY_SIZE);
  }

  //region Main Methods
  public void runSimulation(){

    for (int i = 0; i < FILE_COUNT; i++) {                        //Run all the files through

      jobs = new ArrayList<>();
      filePath = String.format("data/job_data_%s.csv", i + 1);    //Get all the file names given
      retriveJobs(filePath);                                      //Get the jobs out of the file

      //LRU
      pageAlgorithm(memberwiseCloneJobList(jobs), LRU);           //Run the algorithm
      printMemory();
      emptyMemory();

      //Random
      pageAlgorithm(memberwiseCloneJobList(jobs), RANDOM);
      printMemory();
      emptyMemory();
    }
  }
  private void retriveJobs(String filePath){
    //Buffer to read the file
    BufferedReader fileReader = null;
    try
    {
      String line;      //Contains the line

      fileReader = new BufferedReader(new FileReader(filePath));      //Create the file reader

      while ((line = fileReader.readLine()) != null)                  //Read the file line by line
      {
        String[] tokens = line.split(DELIMITER);                      //Get all tokens available in line

        int jobNum = Integer.parseInt(tokens[0]);                     //get the job number

        int jobPage = Integer.parseInt(tokens[1]);                    //get the job reference.

        jobs.add(new Job(jobNum, jobPage));                           //Add a new point with the attributes from the file
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally
    {
      try {
        fileReader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * This is the core of the simulation.
   * Loops through the jobs array
   * and runs each job through each check
   * i have placed
   *
   * @param ArrayList<Job> jobs ==> All the jobs from the csv file
   * @param int type            ==> Defines what type of algorithm to be used
   * */
  private void pageAlgorithm(ArrayList<Job> data, int type){

    resetVar();                                                  //Restore the variables for the algorithm

    int physicalIndex, emptySpot, swapIndex, jobNumber, jobReference;
    String dash = "-";
    for (int i = 0; i < 200; i++){ System.out.print(dash); }

    while(data.size() > 0) {                                     //Loop through the jobs retrieved from the csv file

      this.currentJob = data.remove(0);                  //Get the job from the array

      jobNumber = this.currentJob.getJobNum();                  //Get the job number
      jobReference = this.currentJob.getJobPageRef();           //Get the job reference

      if (isBadJob(jobNumber)) {                                //Check if the job has been deleted already.

        clock++;

      } else if (jobReference == TERMINATE) {                   //Check if the job is completed

        completed++;

        delete(jobNumber);                                      //Delete the job from the memory

        clock++;

      } else if (pageHit()) {                                  //Check if the page already exists

        pageHits++;

      } else {                                                  //Else check if its in swap

        swapIndex = swap.find(jobReference, jobNumber);         //Find if the job is in swap memory already and get the index

        if (swapIndex >= 0) {                                   //If index is higher than 0 means it exist already

          swapAlgorithm(swapIndex, type);                       //Execute the appropriate algorithm

        } else {

          if (!physical.isFull()) {                              //Check if physical is not full to insert the job

            physicalIndex = physical.getEmptySpot();             //Get the empty spot of the physical

            clock++;

            firstLoad++;                                         //The job is loaded for the first time

            this.currentJob.setTimeStamp(clock);                 //Update timestamp

            physical.insert(this.currentJob, physicalIndex);     //Add the job in the physical memory

          } else {

            if (!swap.isFull()) {                                //Check if there is an empty spot in swap.

              clock++;

              firstLoad++;

              physicalIndex = lru();                              //Get the index of the least recent used

              emptySpot = swap.getEmptySpot();                    //Get the empty spot in the swap

              swap(emptySpot,physicalIndex, swap, physical);      //Swap the job

              this.currentJob.setTimeStamp(clock);                //Update the timestamp

              physical.insert(this.currentJob, physicalIndex);    //Insert it in the physical

            } else {

              clock++;
              failed++;

              delete(this.currentJob.getJobNum());                //Delete the job because there is no more room
            }
          }
        }
      }
    }
    System.out.println(printStats(type) + "\n");                   //Print the results after the simulation is over
  }
  //endregion

  //region Algorithms
  /*
   * This method will first check if there is
   * empty spots in the swap memory to swap the
   * physical job into it
   * if there is it places the job from physical to swap memory
   * and moves the swap job to the physical one
   *
   * if there is room in the physical it then just moves the swap job into the physical
   *
   * if there is no room in swap and physical than it kills
   * the jobs
   *
   * @param int swapIndex ==> the index to indicate where the job was found.
   * @return void
   * */

  private void swapAlgorithm(int swapIndex, int type){

    int emptyIndex, physicalIndex;

    if(!physical.isFull()){                                          //Check if the swap has room for swapping to happen
      pageFaults++;

      swap.updateTime(swapIndex, clock);
      emptyIndex = physical.getEmptySpot();                         //Get the empty spot
      swap(emptyIndex, swapIndex, physical, swap);                  //Insert the job into physical

    }else if(!swap.isFull()){

      pageFaults++;

      emptyIndex = swap.getEmptySpot();                             //Get the first empty index

      clock++;                                                      //Update the clock

      if(type == LRU){                                              //Check which method to run LRU or

        physicalIndex = lru();                                      //Get the index of the least recent one

      }else{

        physicalIndex = RandomSwap();                                //Get the index of the random physical

      }

      clock++;
      swap.get(swapIndex).setTimeStamp(clock);
      swap(emptyIndex, physicalIndex, swapIndex);                    //Swap the jobs.

    }else{                                                           //kill the job if no room for swap

      clock++;

      failed++;

      badJobs.add(this.currentJob.getJobNum());

      delete(this.currentJob.getJobNum());
    }
  }

  /*
   *
   * Assumptions: Im assuming that the array of physical is always going to be
   *              full so i don't need to worry about null index
   *
   * LRU method will find the least recent used
   * and return the index of it.
   *
   * @return i   ==> The index of the lru
   * @return -1  ==> if something went wrong;
   * */
  private int lru(){
    Job leastRecent = physical.get(0);                                        //Get the first job
    int pos = 0;                                                              //Start from the beginning
    for (int i = 0; i < PHYSICAL_MEMORY_SIZE; i++) {                          //Scan the physical memory to find the least recent one

      if (physical.get(i) != null && physical.get(i).getTimeStamp() <= leastRecent.getTimeStamp()) {      //Find the least recent used one
        leastRecent = physical.get(i);                                        //Store it in a variable

        pos = i;                                                              //Get the position of that job
      }
    }
    return pos;
  }

  /*
   * Assumptions: Im assuming that the array of physical is always going to be
   *              full so i don't need to worry about null index
   *
   * This method will pick a random job from the
   * physical to swap it.
   *
   * This method will happen only if the
   *
   * @param int swapIndex ==> the index to indicate where the job was found.
   * @return void
   * */
  private int RandomSwap(){
    Random rnd = new Random();
    return rnd.nextInt(PHYSICAL_MEMORY_SIZE);
  }
  //endregion

  //region Helper Methods
  /*
   * Reinitialize the variables
   * */
  private void resetVar() {
    this.badJobs    = new ArrayList<>();
    this.pageHits   = 0;
    this.pageFaults = 0;
    this.completed  = 0;
    this.firstLoad  = 0;
    this.failed     = 0;
    this.clock      = 0;
    this.currentJob = null;
  }

  /*
   * This method is used to swap the job from physical and swap
   * I use this when there is no room in the physical
   *
   * @param int physicalIndex  ==> The index where the job is in physical
   * @param int swapIndex      ==> The index where the job is in swap
   * @param int emptyIndex     ==> The spot in the swap memory
   * */
  private void swap(int emptyIndex, int physicalIndex, int swapIndex){
    swap.insert(physical.get(physicalIndex), emptyIndex);    //Insert the physical job in swap

    physical.insert(swap.get(swapIndex), physicalIndex);    //Insert the swap job into physical
    swap.remove(swapIndex);                                 //Delete the instance of the swap job from swap

  }

  /*
   * This method will swap if there is room in the physical memory;
   *
   * @param int emptyIndex ==> The empty spot in the physical memory
   * @param Memory memory1 ==> Is the memory i want to insert into
   * @param Memory memory2 ==> Is the memory i want to get from
   * @param swapIndex      ==> The index where the job is in swap
   * */
  private void swap(int emptyIndex,  int jobIndex, Memory memory1, Memory memory2){
    memory1.insert(memory2.get(jobIndex), emptyIndex);
    memory2.remove(jobIndex);
  }

  /*
   * Scans the physical array
   * and find if the page exists already.
   *
   * @return true  ==> if the page already is there
   * @return false ==> if the page doesn't exits;
   * */
  private boolean pageHit(){
    int index = physical.find(this.currentJob.getJobPageRef(), this.currentJob.getJobNum());//Check if the there exists in the physical alrady

    if (index >= 0) {

      clock++;                                 //Update the clock

      physical.updateTime(index, clock);       //Update the job timestamp

      return true;
    }
    return false;
  }

  /*
   * This method scans the bad job array and
   * checks if the job number passed already exists
   * in the array.
   *
   * @param int jobNum==> the job number we are searching for
   * @return true  ==> If the job number exists in the array
   * @return false ==> If the job number doesn't exists in the array
   * */
  private boolean isBadJob(int jobRefNum){
    if(badJobs.contains(jobRefNum)) return true;
    return false;
  }

  /*
   * This method wil make sure that the jobs with
   * job number will be deleted from both the memories.
   *
   * @param int jobNumber ==> The job number to be deleted
   * @return void
   * */
  private void delete(int jobNumber){
    swap.deleteAll(jobNumber);
    physical.deleteAll(jobNumber);
  }

  private ArrayList<Job> memberwiseCloneJobList(ArrayList<Job> jobs) {
    ArrayList<Job> clones = new ArrayList<>();
    for (int i = 0; i < jobs.size(); i++) {
      clones.add(jobs.get(i).memberwiseClone());
    }
    return clones;
  }

  /*
   * Print the stats.
   * */
  private String printStats(int type){
    return "\n\nThe simulation results for file " + filePath + " are as follows: \n" +
            "Algorithm Type: " + (type == 1 ? "Least Recent Used" : "Random") + "\n" +
            "Page Hit: " + pageHits + "\n" +
            "Page Faults: " + pageFaults + "\n" +
            "Page first loaded: " + firstLoad + "\n" +
            "Page Failed: " + failed + "\n" +
            "Page completed: " + completed + "\n";
  }

  private void emptyMemory(){
    swap.emptyAll();
    physical.emptyAll();
  }
  private void printMemory(){
    physical.printMemory();
    System.out.println("\n");
    swap.printMemory();
  }
  //endregion
}
