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
    @author Daniel Allen
    9-Nov-2019
 */
public class Verify {
    protected static ArrayList<String> badPasswords = new ArrayList<>();

    static {
        File f = new File("src\\fileioassignment\\badpasswords.txt");
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException ex) {

            }
        }
        if (f.exists()) {
            try {
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
