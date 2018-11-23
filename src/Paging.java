import java.io.*;
import java.util.*;

public class Paging {
  //region Variable declaration.
  //region CSV file
  private String filePath = "data/job_data_1.csv";    //The path to the file
  private ArrayList<Job> jobs = new ArrayList<>();    //Contains the jobs we get from the csv file

  //endregion
  //region MEMORY
  Memory swap;                              //Swap memory
  Memory physical;                       //Physical Memory
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
  private String DELIMITER = ",";
  //endregion

  public Paging() {
    resetVar();
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
    //Input file which needs to be parsed
    BufferedReader fileReader = null;                                         //Buffer to read the file
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

  // TODO: 2018-11-21 [x] Add an array to keep track of the bad jobs
  private void pageAlgorithm(){
    resetVar();                                                             //Restore the variables for the algorithm
    while(jobs.size() > 0) {                                                 //Loop through the jobs retrieved from the csv file

      currentJob = jobs.remove(0);                                   //Get the job from the array

      if (isBadJob(currentJob.getJobNum())) {                                 //Check if the job has been deleted already.

        System.out.println("Job: " + currentJob.getJobNum() + " is a bad job so it will not be processed");

      } else if (currentJob.getJobPageRef() == TERMINATE) {                   //Check if the page is terminated

        System.out.println("Just deleted the job with Number: " + currentJob.getJobNum());

        //Also adds the number to the bad jobs array
        swap.deleteAll(currentJob.getJobNum()); //Delete all the job with the current job number

        physical.deleteAll(currentJob.getJobNum());

        badJobs.add(currentJob.getJobNum());

        clock++;

      } else if (pageHit()) {                                                 //Check if the page already exists

        pageHits++;

        System.out.println("Page hit Job: " + currentJob.toString());

      } else {                                                              //Else check if its in swap

        int swapIndex = swap.find(currentJob.getJobPageRef());             //Find if job is in swap memory already

        if (swapIndex >= 0) {                                               //The job is in swap memory

          System.out.println("The job " + currentJob.toString() + " is in swap memory");
          pageFaults++;
          lru(swapIndex);                                                    //Find the least recent used and swap it with the swap position.
        } else {                                                             //Its not in swap memory so try and swap the least recent out.
          int physicalIndex = physical.getEmptySpot();                       //Find an empty spot in the physical memory
          if (physicalIndex >= 0) {                                          //Check if there is any spot
            clock++;
            firstLoad++;                                                     //The job is loaded for the first time
            currentJob.setTimeStamp(clock);
            physical.insert(currentJob, physicalIndex);                      //Add the job in the physical memory
            System.out.println("Just placed Job " + currentJob.toString() + " into physical memory");
          } else {                                                           //Else check the swap memory
            firstLoad++;
            swapIndex = swap.getEmptySpot();                              //Find a spot in swap memory
            if (swapIndex >= 0) {                                            //There is an empty spot in swap memory.

              clock++;
              currentJob.setTimeStamp(clock);
              swap.insert(currentJob, swapIndex);
              System.out.println("Just inserted the job " + currentJob.toString() + " into swap memory");

            } else {                                                        //There is no room for the job so insufficient memory

              clock++;
              swap.deleteAll(currentJob.getJobNum());
              physical.deleteAll(currentJob.getJobNum());
              badJobs.add(currentJob.getJobNum());
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
   *This method will first check if there is
   * empty spots in the swap memory to swap the
   * physical job into it
   * if there it it places the job from physical to swap memory
   * and moves the swap job to the physical one
   *
   * if there is no room in swap than it kills
   * the jobs
   *
   * @param int swapIndex ==> the index to indicate where the job is.
   * @return void
   * */

  private void lru(int swapIndex){

    Job leastRecent = physical.get(0);                                          //Get the first job in the physical memory;
    int physicalIndex = 0;                                                      //The index of the lru job

    int empty = swap.getEmptySpot();                                           //Get the empty position in the swap space if the is one

    if(empty >= 0){                                                               //Check if the swap has room

      for (int i = 1; i < PHYSICAL_MEMORY_SIZE; i++) {                           //Scan the physical memory to find the least recent one
        //Find the least recent used one
        if(physical.get(i) != null && physical.get(i).getTimeStamp() < leastRecent.getTimeStamp()){

          leastRecent = physical.get(i);      //Store it in a variable
          physicalIndex = i;                    //Get the position of that job

        }
      }
      clock++;
      //Swap the jobs
      swap.insert(physical.get(physicalIndex), empty);              //Insert the job into the empty spot
      physical.insert(swap.get(swapIndex), physicalIndex);          //Insert the swap job into the physical

      System.out.println("Just swapped " + physical.get(physicalIndex).toString() + " With " + swap.get(physicalIndex).toString());

    }else{
      clock++;

      System.out.println("There is no empty spots in the swap memory for the swap to happen!!!");
      physical.deleteAll(swap.get(swapIndex).getJobNum());  //Delete all from physical
      swap.deleteAll(swap.get(swapIndex).getJobNum());      //Delete all from the swap space
      badJobs.add(swap.get(swapIndex).getJobNum());
      System.out.println("Deleting job: " + currentJob.toString());
    }
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

  //endregion
}
