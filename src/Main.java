import java.util.Scanner;

public class Main{

  // TODO: 2018-11-28 [ ] Add a menu so the user can input what algorithm to use
  // TODO: 2018-11-28 [ ] Add a repetitive loop so the alogrithm runs until user kills it 
  public static void main(String[] args) {
    Scanner scn = new Scanner(System.in);
    int choice;

    System.out.println("Enter the line number for the algorithm you wish to execute");
    System.out.println("Algorithms:\n1.Least Recent Used\n2. Random swap");

    choice = scn.nextInt();
    Paging paging = new Paging(choice);
    paging.runSimulation();
  }
}
