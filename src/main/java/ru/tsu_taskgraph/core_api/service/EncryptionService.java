package ru.tsu_taskgraph.core_api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.tsu_taskgraph.core_api.exception.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
public class EncryptionService {

    private final String secretKey;

    public EncryptionService(@Value("${app.encryption.key}") String secretKey) {
        // Ключ должен быть ровно 32 байта для AES-256
        if (secretKey.length() < 32) {
            this.secretKey = String.format("%-32s", secretKey).substring(0, 32);
        } else if (secretKey.length() > 32) {
            this.secretKey = secretKey.substring(0, 32);
        } else {
            this.secretKey = secretKey;
        }
    }

    public String encrypt(String raw) {
        if (raw == null) return null;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Error encrypting string", e);
            throw new CryptoException("Ошибка шифрования", e);
        }
    }

    public String decrypt(String encrypted) {
        if (encrypted == null) return null;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encrypted);
            byte[] raw = cipher.doFinal(decoded);
            return new String(raw, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error decrypting string", e);
            throw new CryptoException("Ошибка расшифровки", e);
        }
    }
}