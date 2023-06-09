package az.unibank.authserver.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Log4j2
public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {

        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash;
            md.update(String.valueOf(rawPassword).getBytes(StandardCharsets.ISO_8859_1), 0, rawPassword.length());
            sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }

    private static String convertToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte datum : data) {
            int halfByte = (datum >>> 4) & 0x0F;
            int twoHalfs = 0;
            do {
                if (halfByte <= 9) {
                    sb.append((char) ('0' + halfByte));
                } else {
                    sb.append((char) ('a' + (halfByte - 10)));
                }
                halfByte = datum & 0x0F;
            } while (twoHalfs++ < 1);
        }
        return sb.toString();
    }
}
