public interface IMemory {
  int find(int reference);
  int getEmptySpot();
  void deleteAll(int id);
  void printMemory();
  void insert(Job job, int index);
  Job get(int index);
  void updateTime(int index, int clock);
  void remove(int index);
}
