package app.indiana.helpers;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

/**
 * Created by chris on 24.05.2015.
 */
public class UserHelper {

    public static String createUserHash(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId != null && !androidId.equals("") && !androidId.equalsIgnoreCase("9774d56d682e549c")) {
            return md5(androidId);
        } else if (Build.SERIAL != null && !Build.SERIAL.equals("")) {
            return md5(Build.SERIAL);
        } else {
            SecureRandom sr = new SecureRandom();
            byte[] output = new byte[16];
            sr.nextBytes(output);
            return md5(output.toString());
        }

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
