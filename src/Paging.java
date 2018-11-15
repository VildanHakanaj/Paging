import java.io.*;

public class Paging {

    private int clock;
    private Job currentJob;
    private String filePath = "data/job_data_1.csv";
    private Job[] jobs = new Job[16];

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
            String line = "";
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(filePath));
            int i = 0;
            //Read the file line by line
            while ((line = fileReader.readLine()) != null)
            {
                //Get all tokens available in line
                String[] tokens = line.split(DELIMITER);
                int jobNum = Integer.parseInt(tokens[0]);
                int jobPage = Integer.parseInt(tokens[1]);
                //Add a new point with the attributes from the file
                jobs[i] = new Job(jobNum, jobPage);
                i++;
            }
            //Return the set back
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
