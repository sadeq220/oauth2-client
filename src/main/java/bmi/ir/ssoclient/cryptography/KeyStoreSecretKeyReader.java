package bmi.ir.ssoclient.cryptography;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Component
public class KeyStoreSecretKeyReader implements SecretKeyReader{
    private final String keyStorePassword;
    private final Resource keyStore;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public KeyStoreSecretKeyReader(@Value("${keystore.password}")String keystorePassword,
                                   @Value("${keystore.name}") String keyStoreName){
        this.keyStorePassword = keystorePassword;
        this.keyStore = new ClassPathResource("keys/"+keyStoreName);

    }
    @Override
    public byte[] getOAuth2AccessTokenSecretKey() {
        try {
            KeyStore pkcs12Keystore = KeyStore.getInstance("PKCS12");
            pkcs12Keystore.load(keyStore.getInputStream(),keyStorePassword.toCharArray());
            Key baam_secret_key = pkcs12Keystore.getKey("baam_secret_key", keyStorePassword.toCharArray());
            return baam_secret_key.getEncoded();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
            logger.error("keystore reading exception!",e);
            throw this.secretKeyException(e);
        } catch (IOException e) {
            logger.error("keystore finding exception",e);
            throw this.secretKeyException(e);
        } catch (UnrecoverableKeyException e) {
            logger.error("specified key does not exist in keystore!",e);
            throw this.secretKeyException(e);
        }
    }

    @Override
    public byte[] getOAuth2ClientSecretKey() {
        return new byte[0];
    }

    private RuntimeException secretKeyException(Exception cause){
        return new RuntimeException("secret key reading exception",cause);
    }
}
