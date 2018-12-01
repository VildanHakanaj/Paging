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
public interface IMemory extends IContainer {
  void deleteAll(int id);
  void insert(Job job, int index);
  void emptyAll();
  void updateTime(int index, int clock);
  void remove(int index);
  void printMemory();   //Print the layout
}
