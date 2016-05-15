package arhangel.dim.core.service;

import arhangel.dim.core.User;
import arhangel.dim.core.store.UserStoreImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by tatiana on 19.04.16.
 */
public class AuthorizationService {
    private UserStoreImpl store;
    private static MessageDigest md;
    private boolean success = false;

    User user = null;

    public AuthorizationService(UserStoreImpl store, InputStream is, OutputStream os) {
        this.store = store;
    }

    public static byte[] calculateHash(final String str) {

        try {
            if (md == null) {
                md = MessageDigest.getInstance("SHA-256");
            }
            md.update(str.getBytes("UTF-8"));
        } catch (Exception exc) {
            System.err.println("can't get instance of hashing algo SHA-256");
        }
        return md.digest();
    }

    public static boolean isCorrect(User user, String word) {

        if (word == null) {
            return false;
        }

        String hash = user.getHash();
        String newHash = (new BigInteger(AuthorizationService.calculateHash(word))).toString();

        return hash.equals(newHash);
    }
}
