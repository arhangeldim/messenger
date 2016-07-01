package arhangel.dim.core.service;

import arhangel.dim.core.User;
import arhangel.dim.core.store.UserStoreImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class AuthorizationService {
    private UserStoreImpl store;
    private static MessageDigest md;
    private boolean success = false;

    User user = null;
    private static Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    public AuthorizationService(UserStoreImpl store, InputStream is, OutputStream os) {
        this.store = store;
    }

    public static String calculateHash(final String str) {

      /*  try {
            if (md == null) {
                md = MessageDigest.getInstance("SHA-256");
            }
            md.update(str.getBytes("UTF-8"));
        } catch (Exception exc) {
            System.err.println("can't get instance of hashing algo SHA-256");
        }
        return md.digest();
        */
        return str;
    }

    public static boolean isCorrect(User user, String word) {

        if (word == null) {
            return false;
        }

        String hash = user.getHash();
        String newHash = AuthorizationService.calculateHash(word);
        log.info(hash);
        log.info(newHash);
        return hash.equals(newHash);
    }
}
