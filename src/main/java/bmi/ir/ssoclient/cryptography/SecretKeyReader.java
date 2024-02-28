package bmi.ir.ssoclient.cryptography;

public interface SecretKeyReader {
    byte[] getOAuth2AccessTokenSecretKey();
    byte[] getOAuth2ClientSecretKey();
}
