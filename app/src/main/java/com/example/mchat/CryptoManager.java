package com.example.mchat;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoManager {

    public static String encrypt(String plainText, String sender, String recipient) throws Exception {
        String myKey = sender.compareTo(recipient) < 0 ? sender + "-" + recipient : recipient + "-" + sender;

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashedKey = messageDigest.digest(myKey.getBytes(StandardCharsets.UTF_8));

        SecretKeySpec myKeySpec = new SecretKeySpec(hashedKey, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, myKeySpec);

        byte[] iv = cipher.getIV();
        byte[] encryptedMessage = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        String ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP);
        String encryptedMessageBase64 = Base64.encodeToString(encryptedMessage, Base64.NO_WRAP);

        return ivBase64 + ":" + encryptedMessageBase64 + ":" + sender + ":" + recipient;
    }

    public static String decrypt(String encryptedData) throws Exception {
        String[] encryptedParts = encryptedData.split(":");
        String sender = encryptedParts[2];
        String recipient = encryptedParts[3];
        String myKey = sender.compareTo(recipient) < 0 ? sender + "-" + recipient : recipient + "-" + sender;

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashedKey = messageDigest.digest(myKey.getBytes(StandardCharsets.UTF_8));

        SecretKeySpec myKeySpec = new SecretKeySpec(hashedKey, "AES");

        byte[] iv = Base64.decode(encryptedParts[0], Base64.NO_WRAP);
        byte[] encryptedMessage = Base64.decode(encryptedParts[1], Base64.NO_WRAP);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, myKeySpec, new IvParameterSpec(iv));

        byte[] decryptedMessage = cipher.doFinal(encryptedMessage);

        return new String(decryptedMessage, StandardCharsets.UTF_8);
    }
}
