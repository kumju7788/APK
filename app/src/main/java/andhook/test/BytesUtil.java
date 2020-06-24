package andhook.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class BytesUtil implements Serializable {

    public static byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(ObjectOutputStream outputStream = new ObjectOutputStream(out)) {
            outputStream.writeObject(value);
        }
        return out.toByteArray();
    }

    public static <T extends Serializable> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            //noinspection unchecked
            return (T) new ObjectInputStream(bis).readObject();
        }
    }

    public static String toString(byte[] bytes) {
        return new String(bytes);
    }
}
