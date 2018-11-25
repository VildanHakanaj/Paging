import java.io.*;
import java.util.*;

public class Paging {
  //region Variable declaration.
  //region CSV file
  private String filePath = "data/job_data_1.csv";    //The path to the file
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
  //endregion
  //endregion

  //region Constant Declarations
  private final int TERMINATE = -999;                 //To check if the job is finished
  private final int PHYSICAL_MEMORY_SIZE = 10;        //The size of the array
  private final int SWAP_MEMORY_SIZE = 15;            //The size of the array
  private String DELIMITER = ",";                     //Divide the string with the ,
  private int chooice;
  //endregion

  public Paging(int chooice) {
    resetVar();
    this.chooice = chooice;
    this.swap = new Memory(SWAP_MEMORY_SIZE);
    this.physical = new Memory(PHYSICAL_MEMORY_SIZE);
  }

  //region Main Methods
  public void runSimulation(){
    retriveJobs();
    System.out.println("");
    pageAlgorithm();
    physical.printMemory();
    System.out.println(" \n ");
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
   * and runs each job through the algorithm
   * */
  private void pageAlgorithm(){
    //Restore the variables for the algorithm
    resetVar();

    //Loop through the jobs retrieved from the csv file
    while(jobs.size() > 0) {

      //Get the job from the array
      currentJob = jobs.remove(0);

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

        //Else check if its in swap
      } else {

        //Find if job is in swap memory already
        int swapIndex = swap.find(currentJob.getJobPageRef());

        //The job is in swap memory
        if (swapIndex >= 0) {

          System.out.println("The job " + currentJob.toString() + " is in swap memory");
          pageFaults++;
          //Exectue the appropriate alogrithm
          swapAlgorithm(swapIndex, 0);
          //Its not in swap memory so try and swap the least recent out.
        } else {

          //Find an empty spot in the physical memory
          int physicalIndex = physical.getEmptySpot();

          //Check if there is any spot
          if (physicalIndex >= 0) {

            clock++;

            //The job is loaded for the first time
            firstLoad++;

            currentJob.setTimeStamp(clock);

            //Add the job in the physical memory
            physical.insert(currentJob, physicalIndex);

            System.out.println("Just placed Job " + currentJob.toString() + " into physical memory");

            //Else check the swap memory
          } else {

            firstLoad++;

            //Find a spot in swap memory
            swapIndex = swap.getEmptySpot();

            //There is an empty spot in swap memory.
            if (swapIndex >= 0) {

              clock++;

              currentJob.setTimeStamp(clock);

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

    System.out.println("\n\n The simulation results for file " + filePath + " are as follows: \n" +
            "Page Hit: " + pageHits + "\n" +
            "Page Faults: " + pageFaults + "\n" +
            "Page first loaded: "  + firstLoad + "\n" +
            "Failed Jobs: " + badJobs.size());
  }
  //endregion

  //region Algorithms
  /*
   *This method will first check if there is
   * empty spots in the swap memory to swap the
   * physical job into it
   * if there it it places the job from physical to swap memory
   * and moves the swap job to the physical one
   *
   * if there is no room in swap than it kills
   * the jobs
   *
   * @param int swapIndex ==> the index to indicate where the job was found.
   * @param int type      ==> Decides which type of algorithm to use.
   * @return void
   * */


  private void swapAlgorithm(int swapIndex, int type){

    //The index of the lru job
    int physicalIndex = 0;

    //Get the empty position in the swap space if the is one
    int empty = swap.getEmptySpot();
    // FIXME: 2018-11-23 [ ] Check if the physical is full before using the algorithm.
    //Check if the swap has room
    if(empty >= 0){
      //Update the clock
      clock++;

      System.out.println("Just swapped " + physical.get(physicalIndex).toString() + " With " + swap.get(swapIndex).toString());
      //Swap the jobs
      lru();
      //Insert the job into the empty spot
      swap.insert(physical.get(physicalIndex), empty);

      //Insert the swap job into the physical
      physical.insert(swap.get(swapIndex), physicalIndex);

      //Else there is no room in the swap memory to change the jobs
    }else{
      clock++;

      //kill the job if no room for swapp
      System.out.println("There is no empty spots in the swap memory for the swap to happen!!!");
      delete(currentJob.getJobNum());
      System.out.println("Deleting job: " + currentJob.toString());
    }
  }


  private int lru(){
    //Get the first job
    Job leastRecent = physical.get(0);

    //Scan the physical memory to find the least recent one
    for (int i = 1; i < PHYSICAL_MEMORY_SIZE; i++) {

      //Find the least recent used one
      if (physical.get(i) != null && physical.get(i).getTimeStamp() < leastRecent.getTimeStamp()) {

        //Store it in a variable
        leastRecent = physical.get(i);

        //Get the position of that job
        return i;

      }
    }

    return -1;

  }



  /*
   * This method will first check if there is
   * empty spots in the swap memory to swap the
   * physical job into it
   * if there it it places the job from physical to swap memory
   * and moves the swap job to the physical one
   *
   * if there is no room in swap than it kills
   * the jobs
   *
   * @param int swapIndex ==> the index to indicate where the job was found.
   * @return void
   * */
//  private int RandomSwap(){
//
//    Random rnd = new Random();
//    if(!physical.isEmpty()){
//      int index = rnd.nextInt();
//      do{
//        physical.get(index);
//      }while(physical.get(index) == null);
//      return index;
//    }
//  }
  //endregion

  //region Helper Methods
  /*
   * Reinitialize the variables
   * for each algorithm
   * */


  private void resetVar(){
    this.pageHits = 0;
    this.clock = 0;
    this.currentJob = null;
    this.firstLoad = 0;
    this.pageFaults = 0;
  }

  /*
   * Scans the physical array
   * and find if the page exists already.
   *
   * @return true  ==> if the page already is there
   * @return false ==> if the page doesn't exits;
   * */
  private boolean pageHit(){
    int index = physical.find(currentJob.getJobPageRef());
    if (index >= 0) {
      clock++;                                                    //Update the clock
      physical.updateTime(index, clock);
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

  private void delete(int jobNumber){
    swap.deleteAll(jobNumber);
    physical.deleteAll(jobNumber);
    badJobs.add(jobNumber);
  }
  //endregion
}
