/**
 * Purpose: Handles file IO (reads and stores chart from txt)
 * Author: Alex Li
 * Created on: 23/12/2020
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileIO {
    protected char[][][] basicChart;
    protected int[][] rowValues;
    protected File myFile;

    // initialize basicChart and rowValues
    public FileIO()
    {
        myFile = new File("BasicStrat.txt");
        basicChart = new char[3][10][10];
        rowValues = new int[3][10];
        readFile();
    }

    // returns the basic strat chart
    public char[][][] getChart()
    {
        return basicChart;
    }

    // returns row values of the basic strat chart
    public int[][] getRowValues()
    {
        return rowValues;
    }

    // reads from txt file
    private void readFile()
    {
        try {
            Scanner sc = new Scanner(myFile);
            int chartNum = 0;
            int i = 0, j = 0;
            // read line if available
            while(sc.hasNextLine())
            {
                String rawLine= sc.nextLine();
                String[] splitLines = rawLine.split(":");
                int rowVal = Integer.parseInt(splitLines[0]);
                if(rowVal != -1)
                {
                    // store char from BlackJackStrat.txt in basicChart
                    for(j = 0; j < 10; j++)
                    {
                        basicChart[chartNum][i][j] = splitLines[1].charAt(j);
                    }
                    // store each row's rowVal in rowValues
                    rowValues[chartNum][i] = rowVal;
                    i++;
                }
                // separates each chart
                else {
                    i = 0;
                    chartNum++;
                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("BasicStrat.txt not found");
            e.printStackTrace();
        }
    }
}
