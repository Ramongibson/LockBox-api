package com.ramongibson.lockboxapi.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {

    @Value("${encryption.secret-key-algorithm:PBKDF2WithHmacSHA256}")
    private String secretKeyAlgorithm;

    @Value("${encryption.algorithm:AES/CBC/PKCS5Padding}")
    private String encryptionAlgorithm;

    @Value("${encryption.salt-length:16}")
    private int saltLength;

    @Value("${encryption.iv-length:16}")
    private int ivLength;

    @Value("${encryption.iterations:65536}")
    private int iterations;

    @Value("${encryption.key-length:256}")
    private int keyLength;

    private SecureRandom secureRandom;

    @PostConstruct
    public void init() {
        secureRandom = new SecureRandom();
    }

    public SecretKey deriveKey(String masterPassword, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyAlgorithm);
        KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), salt, iterations, keyLength);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public byte[] generateSalt() {
        byte[] salt = new byte[saltLength];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public byte[] generateIV() {
        byte[] iv = new byte[ivLength];
        secureRandom.nextBytes(iv);
        return iv;
    }

    public String encryptPassword(String plainTextPassword, SecretKey secretKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedPassword = cipher.doFinal(plainTextPassword.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedPassword);
    }

    public String decryptPassword(String encryptedPassword, SecretKey secretKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}