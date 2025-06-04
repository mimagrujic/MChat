package com.example.mchat;

import android.util.Base64;
import org.bouncycastle.crypto.generators.SCrypt;
import java.security.SecureRandom;
import java.util.Arrays;

public class ScryptHash {

    private static final int N = 16384;  // CPU/Memory cost
    private static final int r = 8;      // block size
    private static final int p = 1;      // parallelization
    private static final int KEY_LENGTH = 32; // 32 bytes = 256 bit
    private static final int SALT_LENGTH = 16;
    private SecureRandom secureRandom = new SecureRandom();

    public String hashPassword(String password) throws Exception {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        byte[] hash = SCrypt.generate(password.getBytes("UTF-8"), salt, N, r, p, KEY_LENGTH);

        String saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP);
        String hashB64 = Base64.encodeToString(hash, Base64.NO_WRAP);

        return saltB64 + ":" + hashB64;
    }

    public boolean verifyPassword(String storedHash, String password) throws Exception {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) return false;

        byte[] salt = Base64.decode(parts[0], Base64.NO_WRAP);
        byte[] hash = Base64.decode(parts[1], Base64.NO_WRAP);

        byte[] calculatedHash = SCrypt.generate(password.getBytes("UTF-8"), salt, N, r, p, KEY_LENGTH);

        return Arrays.equals(hash, calculatedHash);
    }

    public String hashKey(String key) throws Exception {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        byte[] hash = SCrypt.generate(key.getBytes("UTF-8"), salt, N, r, p, KEY_LENGTH);

        String saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP);
        String hashB64 = Base64.encodeToString(hash, Base64.NO_WRAP);

        return saltB64 + ":" + hashB64;
    }

    public String hashKey(String key, String saltBase64) throws Exception {
        byte[] salt = Base64.decode(saltBase64, Base64.NO_WRAP);
        byte[] hash = SCrypt.generate(key.getBytes("UTF-8"), salt, N, r, p, KEY_LENGTH);
        String hashB64 = Base64.encodeToString(hash, Base64.NO_WRAP);

        return hashB64;
    }

    public boolean verifyKey(String storedHash, String key) throws Exception {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) return false;

        byte[] salt = Base64.decode(parts[0], Base64.NO_WRAP);
        byte[] hash = Base64.decode(parts[1], Base64.NO_WRAP);

        byte[] calculatedHash = SCrypt.generate(key.getBytes("UTF-8"), salt, N, r, p, KEY_LENGTH);

        return Arrays.equals(hash, calculatedHash);
    }
}

