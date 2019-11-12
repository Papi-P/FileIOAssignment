package handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    Was utility class to verify register information. Replaced with anonymous members and now just reads the bad passwords from the badpasswords.txt file.
    @author Daniel Allen
    9-Nov-2019
 */
public class Verify {
    protected final static ArrayList<String> badPasswords = new ArrayList<>();

    static {
        File f = new File("src\\fileioassignment\\badpasswords.txt");
        //create the file if it doesn't exist
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException ex) {

            }
        }
        if (f.exists()) {
            try {
                //read each line from the file and add it to the ArrayList
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line = "";
                while ((line = br.readLine()) != null) {
                    badPasswords.add(line);
                }
            } catch (FileNotFoundException ex) {

            } catch (IOException ex) {
                Logger.getLogger(RegisterHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
