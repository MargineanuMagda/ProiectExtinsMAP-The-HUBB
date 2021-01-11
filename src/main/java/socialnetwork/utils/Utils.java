package socialnetwork.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static final  DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static String hash(String pass)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pass.getBytes(),0,pass.length());
            String z = new BigInteger(1,md.digest()).toString(16);
            System.out.println(z);
            return z;
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
    }
}
