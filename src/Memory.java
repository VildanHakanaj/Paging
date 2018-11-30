/*======================================================================================================================
|   Simulation of a paging system and the LRU and RANDOM algorithm for swaping mechanism
|
|   Name:           Memory --> Class
|
|   Written by:     Vildan Hakanaj - November 2018
|
|   Written for:    COIS 3320 (Prof. Jacques Beland)Lab 3 Trent University Fall 2018.
|
|   Purpose:        To represent the memory used to store the jobs.
|
|   usage:          Used by paging
|
|   Subroutines/libraries required:
|       Uses the IMemory Interface that i have create to contain all the subroutines used from this class
|
======================================================================================================================*/
import java.util.Arrays;

public class Memory implements IMemory{

  // TODO: 2018-11-28 [ ] Change the memory to abstract and make the physical and the
  private Job[] array;
  private int count;

  public Memory(int size){
    //Start the array to size
    array = new Job[size];
  }

  /*
   * This method will retrive the object out of the array
   *
   * @param int index ==> the position where to grab it from
   * @return Job ==> return the object in the array
   * */
  @Override
  public Job get(int index){
    return array[index];
  }

  /*
   * This method will go and find
   * the job on specified index and will
   * update the timestamp of it with the given clock
   *
   * @param int index ==> The position where to look in the array
   * @param int clock ==> The current clock
   *
   * @return void;
   * */
  @Override
  public void updateTime(int index, int clock) {
    array[index].setTimeStamp(clock);
  }

  /*
   * Scans the array and finds if the page exists.
   *
   * @return i  ==> The index of the page
   * @return -1 ==> if the page doesn't exits;
   * */
  @Override
  public int find(int reference, int jobNumber) {
    for (int i = 0; i < array.length; i++) {
      if(array[i] != null && array[i].getJobPageRef() == reference && array[i].getJobNum() == jobNumber){
        return i;
      }
    }
    return -1;
  }

  /*
   * Scans the array and finds an empty spot.
   *
   * @return i    ==> the index where the spot is free
   * @return -1   ==> If there is no space in the array;
   * */

  @Override
  public int getEmptySpot() {
    for (int i = 0; i < array.length; i++) {
      if(array[i] == null){
        return i;
      }
    }
    return -1;
  }

  /*
   * Scans the array and will find if any
   * references of the jobs exist and will delete them from it.
   *
   * @param id ==> The job number
   * @return void
   * */
  @Override
  public void deleteAll(int id) {
    for (int i = 0; i < array.length; i++) {
      if(array[i] != null && array[i].getJobNum() == id){
        array[i] = null;
      }
    }
  }

  /*
   * Prints the array showing if they are full or null
   * */
  @Override
  public void printMemory() {
    System.out.println(Arrays.toString(array));
  }

  /*
   * Insert the Job into the given position
   * increments the count of the array
   *
   * @param Job job   ==> The job to be inserted
   * @param int index ==> The position we want to insert it
   *
   * @return void
   * */
  @Override
  public void insert(Job job, int index) {
    array[index] = job;
    count++;
  }

  /*
   * Removes the give job from the index;
   * @param index ==> The index of the job in the array
   * */
  @Override
  public void remove(int index) { array[index] = null; }

  /*
   * Scans the array and find if the array is full or not
   *
   * @return true  ==> if the array is full
   * @return false ==> if the array is not full
   * */
  public boolean isFull(){
    for (int i = 0; i < array.length; i++) {
      if(array[i] == null){
        return false;
      }
    }
    return true;
  }

  /*
  * This method will empty the array
  * */
  public void emptyAll(){
    for (int i = 0; i < array.length; i++) {
      if(array[i] != null){
        array[i] = null;
      }
    }
  }
}
