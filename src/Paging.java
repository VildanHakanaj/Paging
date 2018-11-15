import java.io.*;
import java.util.*;

public class Paging {

    private int clock;
    private Job currentJob;
    private String filePath = "data/job_data_1.csv";
    private ArrayList<Job> jobs = new ArrayList<>();

    public Paging() {
        this.clock = 0;
        this.currentJob = null;
    }

    public void runSimulation(){
        retriveJobs();
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












}
