/*======================================================================================================================
|   Simulation of a paging system and the LRU and RANDOM algorithm for swaping mechanism
|
|   Name:           IMemory --> Interface
|
|   Written by:     Vildan Hakanaj - November 2018
|
|   Written for:    COIS 3320 (Prof. Jacques Beland)Lab 3 Trent University Fall 2018.
|
|   Purpose:        Contains all the memory subroutines to be used
|
|   usage:          Used by Memory
|
|   Subroutines/libraries required:
|       No Subroutines/libraries utilized outside of Java.util.*
|
======================================================================================================================*/
public interface IMemory {
  int find(int reference, int jobNumber);
  int getEmptySpot();
  void deleteAll(int id);
  void printMemory();
  void insert(Job job, int index);
  Job get(int index);
  void updateTime(int index, int clock);
  void remove(int index);
}
