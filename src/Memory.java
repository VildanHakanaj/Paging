import java.util.Arrays;

public class Memory implements IMemory{

  public Job[] array;

  public Memory(int size){
    array = new Job[size];
  }

  @Override
  public Job get(int index){
    return array[index];
  }


  public void updateTime(int index, int clock){
    array[index].setTimeStamp(clock);
  }

  /*
   * Scans the array and finds if the page exists.
   *
   * @return i  ==> The index of the page
   * @return -1 ==> if the page doesn't exits;
   * */
  @Override
  public int find(int reference) {
    for (int i = 0; i < array.length; i++) {
      if(array[i] != null && array[i].getJobPageRef() == reference){
        return i;
      }
    }
    return -1;
  }

  /*
   * Scans the array and finds an empty spot.
   *
   * @param Job[] array ==> The array to look in
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
   * Will loop through the array and will find if any
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

  @Override
  public void printMemory() {
    System.out.println(Arrays.toString(array));
  }

  @Override
  public void insert(Job job, int index) {
    array[index] = job;
  }
}
