package com.noble.backend;

import com.googlecode.objectify.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by richie on 3/27/16.
 */

@Entity
public class ErrandBike {

    @Id Long mId;
    boolean mAtStation;

    public ErrandBike() {
        mAtStation = true;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public boolean isAtStation() {
        return mAtStation;
    }

    public void setAtStation(boolean atStation) {
        mAtStation = atStation;
    }

    public String getOTP() {
        String secret = "ErrandBike" + mId.toString();
        byte[] secretBytes = secret.getBytes();
        long movingFactor = (System.currentTimeMillis() / 1000) / 60;
        String otp = null;

        try {
            otp = generateOTP(secretBytes, movingFactor, 6, 0);
        } catch (NoSuchAlgorithmException nsae) { }
        catch (InvalidKeyException ike) { }
        return otp;
    }

    private byte[] hmac_sha1(byte[] keyBytes, byte[] text)
            throws NoSuchAlgorithmException, InvalidKeyException {
        //        try {
        Mac hmacSha1;
        try {
            hmacSha1 = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException nsae) {
            hmacSha1 = Mac.getInstance("HMAC-SHA-1");
        }
        SecretKeySpec macKey =
                new SecretKeySpec(keyBytes, "RAW");
        hmacSha1.init(macKey);
        return hmacSha1.doFinal(text);
    }

    private final int[] DIGITS_POWER // 0 1  2   3    4     5      6       7        8
            = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    private String generateOTP(byte[] secret,
                               long movingFactor,
                               int codeDigits,
                               int truncationOffset)
            throws NoSuchAlgorithmException, InvalidKeyException {
        // put movingFactor value into text byte array
        String result = null;
        byte[] text = new byte[8];
        for (int i = text.length - 1; i >= 0; i--) {
            text[i] = (byte) (movingFactor & 0xff);
            movingFactor >>= 8;
        }

        // compute hmac hash
        byte[] hash = hmac_sha1(secret, text);

        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;
        if ((0 <= truncationOffset) &&
                (truncationOffset < (hash.length - 4))) {
            offset = truncationOffset;
        }
        int binary =
                ((hash[offset] & 0x7f) << 24) |
                        ((hash[offset + 1] & 0xff) << 16) |
                        ((hash[offset + 2] & 0xff) << 8) |
                        (hash[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[codeDigits];

        result = Integer.toString(otp);
        while (result.length() < codeDigits) {
            result = "0" + result;
        }
        return result;
    }
}
