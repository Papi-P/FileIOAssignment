package handlers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;

/*
    @author Daniel Allen
    9-Nov-2019
 */
public final class Encrypt {

    private static MessageDigest md;
    //store a table of characters for a hex lookup
    private static final char[] hexTable = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F'
    };

    static {
        try {
            //define the algorithm the digester should use
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {

        }
    }

    public static String sha256(String input) {
        //return null if the input is null
        if (input == null || input.isEmpty()) {
            return null;
        }
        try {
            //digest the String into a byte array
            byte[] digested = md.digest(input.getBytes("UTF-8"));

            String hash = bytesToHex(digested);
            return hash;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Encrypt.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            md.reset();
        }
        return null;
    }

    public static String bytesToHex(byte[] bytes) {
        //create a StringBuilder for longer inputs. This should speed it up.
        StringBuilder sb = new StringBuilder(2 * bytes.length);
        //loop through the byte array
        for (int i = 0; i < bytes.length; i++) {
            //convert to unsigned value
            int b = bytes[i] & 0xFF;
            //append the result divided by 16. Bitwise right shift is faster.
            sb.append(hexTable[b >>> 4]);
            //append the result modulus 16. Bitwise AND is faster.
            sb.append(hexTable[b & 0x0F]);
        }
        //return the value of the StringBuilder
        return sb.toString();
    }
}
