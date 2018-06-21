package com.tcredit.engine.util;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-16 下午2:57
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-16 下午2:57
 * @updatedRemark:
 * @version:
 */
public final class DeepCopy {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DeepCopy.class);
    /**
     * Returns a copy of the object, or null if the object cannot
     * be serialized.
     */
    public static <T extends Serializable> T copy(T orig) {
        T obj = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            // Write the object out to a byte array
            FastByteArrayOutputStream fbos =
                    new FastByteArrayOutputStream();
            out = new ObjectOutputStream(fbos);
            out.writeObject(orig);
            out.flush();

            // Retrieve an input stream from the byte array and read
            // a copy of the object back in.
            in = new ObjectInputStream(fbos.getInputStream());
            obj = (T) in.readObject();
        } catch (IOException e) {
            LOGGER.error("", e);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

}
