/**
 * Utilities for opening a text file. The text file
 * can be opened and read from, or the file can be
 * opened (created) and written to.
 *
 * @author Jeremy Tien
 * @version 1.0
 * @since November 6, 2017
 */
 
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
 
public class OpenFile
{
    public static void main(String[]args)
    {
        Scanner infile = OpenFile.openToRead("g.txt");
        PrintWriter outfile = OpenFile.openToWrite("gcopy.txt");
        String temp = null;
        while(infile.hasNext())
        {
            temp = infile.nextLine();
            System.out.println(temp);
            outfile.println(temp);
        }
        infile.close();
        outfile.close();    
    }
    
    /**
     * Open a file for reading.
     * @param fileString    The name of the file to be opened.
     * @return                 A Scanner instance of the file to be opened.
     */
    public static Scanner openToRead(String fileString)
    {
        Scanner fromfile = null;
        File filename = new File(fileString);
        try
        {
            fromfile = new Scanner(filename);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("\n\nSorry, but " + fileString + " could not be found\n\n");
            System.exit(1);
        }
        return fromfile;
    }
    
    /**
     * Open a file for writing.
     * @param fileString    The name of the file to be opened (created).
     * @return                 A PrintWriter instance of the file to be opened (created).
     */
    public static PrintWriter openToWrite(String fileString)
    {
        PrintWriter tofile = null;
        try
        {
            tofile = new PrintWriter(fileString);
        }
        catch (Exception e)
        {
            System.out.println("\n\nSorry, but " + fileString + " could not be created\n\n");
            System.exit(2);
        }
        return tofile;
    }
}
