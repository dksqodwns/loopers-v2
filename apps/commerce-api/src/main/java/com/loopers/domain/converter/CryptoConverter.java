package com.loopers.domain.converter;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    private String secretKey;

    public static final String ALGORITHM = "AES";

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || secretKey == null) {
            return null;
        }
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "데이터 암호화에 실패 했습니다. ERROR: " + e.getMessage());
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || secretKey == null) {
            return null;
        }

        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "데이터 복호화에 실패 했습니다. ERROR: " + e.getMessage());
        }
    }
}
