package arhangel.dim;

import org.junit.Test;

import sun.security.x509.AVA;

import static org.junit.Assert.*;

/**
 *
 */
public class AvatarTest {

    @Test
    public void testCapitalize() throws Exception {
        Avatar avatar = new Avatar();
        assertEquals("HELLO", avatar.capitalize("hello"));
    }
}
