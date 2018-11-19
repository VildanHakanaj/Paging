import java.io.*;
import java.util.*;

public class Paging {

  //region Variable declaration.

  //region CSV file
  private String filePath = "data/job_data_1.csv";
  private ArrayList<Job> jobs = new ArrayList<>();
  //endregion

  //region MEMORY
  private Job[] swapMem;                              //Swap memory
  private Job[] physicalMemory;                       //Physical Memory
  //endregion

  //region Simulation variables
  private int clock;
  private Job currentJob;
  private int pageHits;                               //Number of page hits
  private int firstLoad;                              //Counter for the first loaded pages
  //endregion
  //endregion

  //region Constant Declarations
  private final int TERMINATE = -999;
  private final int PHYSICAL_MEMORY_SIZE = 10;
  private final int SWAP_MEMORY_SIZE = 15;
  //endregion

  public Paging() {
    this.swapMem = new Job[SWAP_MEMORY_SIZE];
    this.physicalMemory = new Job[PHYSICAL_MEMORY_SIZE];
  }
  //region Main Methods
  public void runSimulation(){
    retriveJobs();
    leastRecentUsed();
  }

  private void retriveJobs(){
    //Input file which needs to be parsed
    BufferedReader fileReader = null;                                         //Buffer to read the file

    //Delimiter used in CSV file
    final String DELIMITER = ",";
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
        int jobPage = Integer.parseInt(tokens[0]);
        //get the job reference.
        int jobNum = Integer.parseInt(tokens[1]);
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

  private void leastRecentUsed(){
    resetVar();                                                           //Restore the variables

    for (int i = 1; i < jobs.size(); i++) {                               //Loop through the jobs retrieved from the csv file
      currentJob = jobs.remove(0);                                //Get the job from the array

      if(currentJob.getJobPageRef() == TERMINATE){                        //Check if the page is terminated
        deleteAll(currentJob.getJobNum());                                //Delete all the job with the current job number
      }


      /*TODO: Should i update the job or just keep the old one and add the new reference job ???? */

      //Check if there is a page hit
      if(pageHit()){                                                      //If the page already exists
        pageHits++;
      }else{                                                              //Else check if its in swap
        int swapIndex = findInSwap(currentJob.getJobPageRef());
        if(swapIndex >= 0){                                               //The job is in swap memory
          /*Find the least recent used job*/
          System.out.println("The job is in swap memory");
          /*Here is where i pretend to do the swapping with the least recent one*/
          System.out.println("Swapping the job with the least recent one");
        }else{                                                             //Its not in swap memory so try and swap the least recent out.
          int physicalIndex = findEmptySpot(physicalMemory);               //Find an empty spot in the swap
          if (physicalIndex >= 0) {                                        //Check if there is any spot
            physicalMemory[physicalIndex] = currentJob;                    //Add the job in the physical memory
          }else{                                                           //Else check the swap memory
            swapIndex = findEmptySpot(swapMem);                            //Find a spot in swap memory
            if(swapIndex >= 0){                                            //There is an empty spot in swap memory.
              swapMem[swapIndex] = currentJob;
            }else{
              /*Don't know what to do*/
            }
          }
        }
      }
    }
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

  }

  /*
   * Scans the physical array
   * and find if the page exists already.
   *
   * @return true  ==> if the page already is there
   * @return false ==> if the page doesn't exits;
   * */
  private boolean pageHit(){
    for (int i = 0; i < physicalMemory.length; i++)                 //Scan the array to find the job
      if(physicalMemory[i] != null && currentJob.getJobPageRef() == physicalMemory[i].getJobPageRef()) {
        clock++;
        currentJob.setTimeStamp(clock);


        //        physicalMemory[i] = currentJob;                              //Change the job with the new one

        return true;
      }
    return false;
  }

  /*
  * Scans the swap memory array
  * tries to find the job that contains
  * the given job reference number
  *
  * @param int jobNumReference  ==> The reference number we are looking for in swap memory
  * @return i   ==> the index where the job is. If found.
  * @return -1  ==> If the job doesn't exist in swap memory.
  * */
  private int findInSwap(int jobNumReference){
    for (int i = 0; i < swapMem.length; i++) {
      if(swapMem[i] != null && swapMem[i].getJobPageRef() == jobNumReference){
        return i    ;
      }
    }
    return -1;
  }
  /*
   * Finds and empty spot on the give array
   *
   * @param array => The array to look in
   * @return i ==> the index where the spot is free
   * @return -1 ==> so we know that the array is full;
   * */
  private int findEmptySpot(Job[] array){
    for (int i = 0; i < array.length; i++)
      if (array[i] == null) return i;
    return -1;
  }

  private void deleteAll(int jobNumber){
    int i = 0;
    //Remove job references from swap space and the physical space;
    for (i = 0; i < physicalMemory.length; i++) {
      if(physicalMemory[i].getJobNum() == jobNumber) physicalMemory[i] = null;
      if(swapMem[i].getJobNum() == jobNumber) physicalMemory[i] = null;
    }
    //Remove all the instances of the job from the swap memory
    for(i = i; i < swapMem.length; i++){
      if(swapMem[i].getJobNum() == jobNumber){
        swapMem[i] = null;
      }
    }
  }
  //endregion
}
