public interface IContainer {
  int find(int reference, int jobNumber);
  boolean isFull();     //Check if its full
  int getEmptySpot();   //Find empty spot
  Job get(int index);
}
