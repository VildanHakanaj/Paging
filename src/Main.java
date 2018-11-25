import java.util.Scanner;

public class Main{

    public static void main(String[] args) {
      Scanner scn = new Scanner(System.in);
      System.out.println("Enter the line number for the algorithm you wish to execute");
      System.out.println("Algorithms:\n1.Least Recent Used\n2. Random swap");
      int chooice = scn.nextInt();
      Paging paging = new Paging(chooice);
      paging.runSimulation();
    }
}
