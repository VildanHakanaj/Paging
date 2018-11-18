import java.io.*;
import java.util.*;

public class Paging {

    //region Variable declaration.
    private int clock;
    private Job currentJob;
    private String filePath = "data/job_data_1.csv";
    private ArrayList<Job> jobs = new ArrayList<>();
    private int firstPageLoad;
    private Job[] swapMem;
    private int pageHits;
    private Job[] physicalMemory;
    //endregion

    //region Constant Declarations
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
                int jobNum = Integer.parseInt(tokens[0]);
                //get the job reference.
                int jobPage = Integer.parseInt(tokens[1]);
                //Add a new point with the attributes from the file
                jobs.add(new Job(jobNum, jobPage));
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

    private void leastRecentUsed(){
      resetVar();                                               //Restore the variables

      for (int i = 1; i < jobs.size(); i++) {
        currentJob = jobs.remove(0);                     //Get the jobs
        for (int j = 0; j < physicalMemory.length; j++) {

          if(pageHit()){                                        //If the page already exists

            pageHits++;
            // TODO: Should I update the old job with the new job reference?
            // TODO: Should I update the job time here
            currentJob = null;
            break;
          }else{                                                //Else find if there is room to fit it
            int physicalIndex = findEmptySpot(physicalMemory);  //Find a spot in the physical Memory
            /*if(currentJob.getJobPageRef() == -999){

              deleteAll(currentJob.getJobNum());

            }else*/ if(physicalIndex >= 0){

              physicalMemory[physicalIndex] = currentJob;       //If there is room store the job
              break;

            }else{

              int swapIndex = findEmptySpot(swapMem);           //Find a spot in the swap memory

              if(swapIndex == -1){

                currentJob = null;                              //If there is no room then delete the job
                System.out.println("Error: Insufficent memory for job: " + currentJob.getJobNum());

              }else{
                //TODO: find the least accessed page and put it in swap memory
                /*
                * IDEA 1: Keep the job saved into a temp variable called the least access one.
                *         update it every time a swap happens or a job finishes.
                * IDEA 2: Have the job contain a time when they first were put in the physical memory.
                * 
                * */
                swapMem[i] = currentJob;                        //insert the job into the swap memory
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
        this.firstPageLoad = 0;
    }

    /*
    * Loops through the physical array
    * and find if the page exists already.
    *
    * @return true ==> if the page already is there
    * @return flase ==> if the page doesn't exits;
    * */
    private boolean pageHit(){
      for (int i = 0; i < physicalMemory.length; i++)
        if(physicalMemory[i] != null && currentJob.getJobNum() == physicalMemory[i].getJobNum()) {
          physicalMemory[i] = currentJob;                                         //Change the job with the new one
          return true;
        }
      return false;
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

    private void deleteAll(int jobRef){
      int i = 0;
      for (i = 0; i < physicalMemory.length; i++) {
        if(physicalMemory[i].getJobNum() == jobRef){
          physicalMemory[i] = null;
        }
        if(swapMem[i].getJobNum() == jobRef){
          physicalMemory[i] = null;
        }
      }

      for(i = i; i < swapMem.length; i++){
        if(swapMem[i].getJobNum() == jobRef){
          swapMem[i] = null;
        }
      }
    }

    //endregion
}
