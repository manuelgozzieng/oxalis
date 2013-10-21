package eu.peppol.as2;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author steinar
 *         Date: 17.10.13
 *         Time: 21:36
 */
public class As2DispositionNotificationOptionsTest {

    @Test
    public void createFromString() throws Exception {

        As2DispositionNotificationOptions options = As2DispositionNotificationOptions.valueOf("signed-receipt-protocol=required, pkcs7-signature; signed-receipt-micalg=required,sha1");
        assertEquals(options.getParameterList().size(), 2);

        As2DispositionNotificationOptions.Parameter  parameter = options.getSignedReceiptProtocol();
        assertNotNull(parameter);

        As2DispositionNotificationOptions.Parameter micAlg = options.getSignedReceiptMicalg();
        assertNotNull(micAlg);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createFromInvalidString() throws Exception {
        As2DispositionNotificationOptions options = As2DispositionNotificationOptions.valueOf("signed-receipt-protocol=reXXuired, pkcs7-signature");

    }
}
