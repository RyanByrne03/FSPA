/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fspa;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Properties;
import java.io.InputStream;
/**
 *
 * @author RyanByrne
 */
public class CryptoUtil {
    private static final String SECRET_KEY;
//Typically, I would have preferred to use an env file for my secret key, but netbeans does not make use of env file, so I resorted to using a config.properties file.
    static {
        SECRET_KEY = loadKeyFromConfig();
        if (SECRET_KEY == null || SECRET_KEY.length() != 16) {
            throw new IllegalStateException("AES key must be 16 characters long and defined in config.properties");
        }
    }

    private static String loadKeyFromConfig() {
        try (InputStream input = CryptoUtil.class.getClassLoader().getResourceAsStream("fspa/config.properties")) {
            if (input == null) {
                throw new IllegalStateException("config.properties file not found in classpath.");
            }
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty("aes.key");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String encrypt(String strToEncrypt) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
    }

    public static String decrypt(String strToDecrypt) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }    
}
