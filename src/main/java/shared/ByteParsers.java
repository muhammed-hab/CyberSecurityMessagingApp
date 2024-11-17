package shared;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class ByteParsers {

    public static int bytesToInt(byte[] data) {
        if (data.length != 4) throw new RuntimeException("Int byte array must be of length 4!");
        return (((int) data[0]) & 0xFF) | ((((int) data[1]) & 0xFF) << 8) | ((((int) data[2]) & 0xFF) << 16) | ((((int) data[3]) & 0xFF) << 24);
    }

    public static byte[] intToBytes(int num) {
        return new byte[] {
                (byte) (num & 0xFF),
                (byte) ((num >>> 8) & 0xFF),
                (byte) ((num >>> 16) & 0xFF),
                (byte) ((num >>> 24) & 0xFF),
        };
    }

    public static int getIntAt(byte[] array, int offset) {
        byte[] output = new byte[4];
        System.arraycopy(array, offset, output, 0, 4);
        return bytesToInt(output);
    }

    public record ExtractedStringsResult(int bytesRead, String[] strings) {}

    public static ExtractedStringsResult extractStringsWithCount(byte[] message, int offset, int numStrings) {
        var outputs = new String[numStrings];
        int loc = offset;
        for (int i = 0; i < numStrings; i++) {
//            if (message.length < loc + 4) throw new ByteParsingException();

            var strLen = getIntAt(message, loc);
//            if (message.length < loc + 4 + strLen) throw new ByteParsingException();
            outputs[i] = new String(message, loc + 4, strLen, StandardCharsets.UTF_8);

            loc = loc + 4 + strLen;
        }
        return new ExtractedStringsResult(loc - offset, outputs);
    }

    public static String[] extractStrings(byte[] message, int offset, int numStrings) {
        return extractStringsWithCount(message, offset, numStrings).strings();
    }

    public static byte[] stringsToBytes(String[] strings) {
        var output = new ByteArrayOutputStream();
        for (var str : strings) {
            output.writeBytes(ByteParsers.intToBytes(str.length()));
            output.writeBytes(str.getBytes(StandardCharsets.UTF_8));
        }
        return output.toByteArray();
    }
}
