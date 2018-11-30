/*======================================================================================================================
|   Simulation of a paging system and the LRU and RANDOM algorithm for swaping mechanism
|
|   Name:           Main Class
|
|   Written by:     Vildan Hakanaj - November 2018
|
|   Written for:    COIS 3320 (Prof. Jacques Beland)Lab 3 Trent University Fall 2018.
|
|   Purpose:        To simulate how memory management system works. Given files to load in memory.
|                   It present two different algorithm for decision making on what job to swap and see
|                   the different output results
|
|
|   usage:          Runs in any java IDE
|
|   Subroutines/libraries required:
|       No Subroutines/libraries utilized outside of Java.util.*
|
======================================================================================================================*/

public class Main{

  public static void main(String[] args) {
    Paging paging = new Paging();
    paging.runSimulation();
  }
}
