package app.indiana.helpers;

import android.content.Context;
import android.provider.Settings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

/**
 * Created by chris on 24.05.2015.
 */
public class UserHelper {

    public static String createUserHash() {
        Date now = new Date();
        SecureRandom sr = new SecureRandom();
        byte[] output = new byte[16];
        sr.nextBytes(output);
        String hash = md5(now.toString() + output.toString());
        return hash;
    }

    private static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
