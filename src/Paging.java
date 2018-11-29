import java.io.*;
import java.util.*;

public class Paging {
  //region Variable declaration.
  //region CSV file
  private String filePath = "data/job_data_5.csv";    //The path to the file
  private ArrayList<Job> jobs = new ArrayList<>();    //Contains the jobs we get from the csv file

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
  private ArrayList<Integer> badJobs = new ArrayList<>(); //Contains all the jobs that have been terminated.
  private int choice;
  //endregion
  //endregion

  //region Constant Declarations

  //To check if the job is finished
  private final int TERMINATE = -999;

  //The size of the array
  private final int PHYSICAL_MEMORY_SIZE = 10;

  //The size of the array
  private final int SWAP_MEMORY_SIZE = 15;

  //Divide the string with the ,
  private String DELIMITER = ",";

  private final int RANDOM = 2;
  //endregion

  public Paging(int choice) {
    resetVar();
    this.choice = choice;
    this.swap = new Memory(SWAP_MEMORY_SIZE);
    this.physical = new Memory(PHYSICAL_MEMORY_SIZE);
  }

  //region Main Methods
  public void runSimulation(){
    retriveJobs();
    System.out.println("");
    pageAlgorithm();
    physical.printMemory();
    System.out.println("\n");
    swap.printMemory();
  }

  private void retriveJobs(){
    //Buffer to read the file
    BufferedReader fileReader = null;
    try
    {
      //Contains the line
      String line;
      //Create the file reader
      fileReader = new BufferedReader(new FileReader(filePath));
      //Read the file line by line
      while ((line = fileReader.readLine()) != null)
      {
        //Get all tokens available in line
        String[] tokens = line.split(DELIMITER);
        //get the job number
        int jobNum = Integer.parseInt(tokens[0]);
        //get the job reference.
        int jobPage = Integer.parseInt(tokens[1]);
        //Add a new point with the attributes from the file
        jobs.add(new Job(jobNum, jobPage));
      }
      System.out.println();
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
   * */
  private void pageAlgorithm(){
    resetVar();                                           //Restore the variables for the algorithm

    while(jobs.size() > 0) {                              //Loop through the jobs retrieved from the csv file

      int physicalIndex;

      currentJob = jobs.remove(0);      //Get the job from the array


      //Check if the job has been deleted already.
      if (isBadJob(currentJob.getJobNum())) {

        clock++;
        System.out.println("Job: " + currentJob.getJobNum() + " is a bad job so it will not be processed");

        //Check if the page is terminated
      } else if (currentJob.getJobPageRef() == TERMINATE) {

        System.out.println("Just deleted the job with Number: " + currentJob.getJobNum());

        //Also adds the number to the bad jobs array

        delete(currentJob.getJobNum());

        clock++;

        //Check if the page already exists
      } else if (pageHit()) {

        pageHits++;

        System.out.println("Page hit Job: " + currentJob.toString());

      } else {                                                            //Else check if its in swap

        int swapIndex = swap.find(currentJob.getJobPageRef());            //Find if the job is in swap memory already

        if (swapIndex >= 0) {                                             //The job is in swap memory

          System.out.println("The job " + currentJob.toString() + " is in swap memory");

          pageFaults++;

          swapAlgorithm(swapIndex);                                       //Execute the appropriate algorithm
        } else {


          //Check if there is any spot
          if (!physical.isFull()) {

            //Get the empty spot of the physical
            physicalIndex = physical.getEmptySpot();

            clock++;

            //The job is loaded for the first time
            firstLoad++;

            currentJob.setTimeStamp(clock);

            //Add the job in the physical memory
            physical.insert(currentJob, physicalIndex);

            System.out.println("Just placed Job " + currentJob.toString() + " into physical memory");

            //Else check the swap memory
          } else {



            //There is an empty spot in swap memory.
            if (!swap.isFull()) {
              //Find a spot in swap memory
              swapIndex = swap.getEmptySpot();

              firstLoad++;

              clock++;

              currentJob.setTimeStamp(clock); //Update the time before inserting

              swap.insert(currentJob, swapIndex);

              System.out.println("Just inserted the job " + currentJob.toString() + " into swap memory");

              //There is no room for the job so insufficient memory
            } else {

              clock++;

              //Delete the job because there is no more room
              delete(currentJob.getJobNum());

              System.out.println("Error: Memory insufficient for Job: " + currentJob.toString());
            }
          }
        }
      }
    }
    //Print the results after the simulation is over
    System.out.println();
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

  private void swapAlgorithm(int swapIndex){

    int emptyIndex, physicalIndex;
    if(!physical.isFull()){                                          //Check if the swap has room for swapping to happen

      System.out.println("Just moved " + swap.get(swapIndex).toString() + "from swap to physical memory");      //Print what job moved
      emptyIndex = physical.getEmptySpot();                         //Get the empty spot
      swap(emptyIndex, swapIndex);                                  //Insert the job into physical

    }else if(!swap.isFull()){

      emptyIndex = swap.getEmptySpot();                             //Get the first empty index
      clock++;                                                      //Update the clock
      if(choice != RANDOM){                                         //Check which method to run LRU or

      physicalIndex = lru();                                        //Get the index of the least recent one

      }else{

        physicalIndex = RandomSwap();                               //Get the index of the random physical
      }
      //Print what jobs was switched
      System.out.println("Just swapped " + physical.get(physicalIndex).toString() + " With " + swap.get(swapIndex).toString());

      swap(emptyIndex, physicalIndex, swapIndex);                   //Swap the jobs.

    }else{
      clock++;

      //kill the job if no room for swapp
      System.out.println("There is no empty spots in the swap memory for the swap to happen!!!");
      delete(currentJob.getJobNum());
      System.out.println("Deleting job: " + currentJob.toString());
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
    this.pageHits = 0;
    this.clock = 0;
    this.currentJob = null;
    this.firstLoad = 0;
    this.pageFaults = 0;
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
   * @param swapIndex      ==> The index where the job is in swap
   * */
  private void swap(int emptyIndex, int swapIndex){
    physical.insert(swap.get(swapIndex), emptyIndex);
    swap.remove(swapIndex);
  }


  /*
   * Scans the physical array
   * and find if the page exists already.
   *
   * @return true  ==> if the page already is there
   * @return false ==> if the page doesn't exits;
   * */
  private boolean pageHit(){
    int index = physical.find(currentJob.getJobPageRef());        //Check if the there exists in the physical alrady
    if (index >= 0) {
      clock++;                                                    //Update the clock
      physical.updateTime(index, clock);                          //Update the job
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
    badJobs.add(jobNumber);
  }

  /*
  * Print the stats.
  * */
  private String printStats(){
    return "\n\nThe simulation results for file " + filePath + " are as follows: \n" +
            "Algorithm Type: " + (choice == 1 ? "Least Recent Used" : "Random") + "\n" +
            "Page Hit: " + pageHits + "\n" +
            "Page Faults: " + pageFaults + "\n" +
            "Page first loaded: " + firstLoad + "\n" +
            "Failed Jobs: " + badJobs.size();
  }
  //endregion
}
