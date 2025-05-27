package com.example.mchat;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptoManager {

    private static String KEY_ALIAS = "MyKey1";
    private static String ANDROID_KEYSTORE = "AndroidKeyStore";

    private static SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);

        if(!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey();
        }

        return (SecretKey) keyStore.getKey(KEY_ALIAS, null);
    }

    public static void generateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);

        if(keyStore.containsAlias(KEY_ALIAS)) {
            return;
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);

        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                //PKCS7 - n more bytes of value n (until block is 16 bytes long) - 0x04 for n = 4
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setRandomizedEncryptionRequired(true)  //secures random IV every time
                .build();

        keyGenerator.init(keyGenParameterSpec);
        keyGenerator.generateKey();
    }

    public static String encrypt(String plainText) throws Exception {
        SecretKey secretKey = getSecretKey();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] iv = cipher.getIV();
        byte[] encryptedMessage = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        String ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP);
        String encryptedMessageBase64 = Base64.encodeToString(encryptedMessage, Base64.NO_WRAP);

        return ivBase64 + ":" + encryptedMessageBase64;
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKey secretKey = getSecretKey();

        String[] encryptedParts = encryptedData.split(":");
        if(encryptedParts.length != 2) {
            throw new IllegalArgumentException("Invalid encryptedData format.");
        }

        byte[] iv = Base64.decode(encryptedParts[0], Base64.NO_WRAP);
        byte[] encryptedMessage = Base64.decode(encryptedParts[1], Base64.NO_WRAP);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] decryptedMessage = cipher.doFinal(encryptedMessage);

        return new String(decryptedMessage, StandardCharsets.UTF_8);

    }
}
