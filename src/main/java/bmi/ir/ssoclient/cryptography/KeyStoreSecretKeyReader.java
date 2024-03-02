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
import java.util.Map;

@Component
public class KeyStoreSecretKeyReader implements SecretKeyReader{
    private final String keyStorePassword;
    private final Resource keyStore;
    private final Map<String,byte[]> secretKeyCache;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public KeyStoreSecretKeyReader(@Value("${keystore.password}")String keystorePassword,
                                   @Value("${keystore.name}") String keyStoreName) throws Exception{
        this.keyStorePassword = keystorePassword;
        this.keyStore = new ClassPathResource("keys/"+keyStoreName);
        this.secretKeyCache = this.initCache();
    }
    private Map initCache() throws Exception {
        try {
            KeyStore pkcs12Keystore = KeyStore.getInstance("PKCS12");
            pkcs12Keystore.load(keyStore.getInputStream(),keyStorePassword.toCharArray());
            Key baam_jwt_secretKey = pkcs12Keystore.getKey("baam_jwt_secretkey", keyStorePassword.toCharArray());
            Key baam_client_secretKey = pkcs12Keystore.getKey("baam_client_secretkey",keyStorePassword.toCharArray());
            return Map.ofEntries( // unmodifiable map
                    Map.entry("baam_jwt_secretkey",baam_jwt_secretKey.getEncoded()),
                    Map.entry("baam_client_secretkey", baam_client_secretKey.getEncoded()));
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
            logger.error("keystore reading exception!",e);
            throw e;
        } catch (IOException e) {
            logger.error("keystore finding exception or PKCS12 integrity checking exception",e);
            throw e;
        } catch (UnrecoverableKeyException e) {
            logger.error("specified key does not exist in keystore!",e);
            throw e;
        }
    }
    @Override
    public byte[] getOAuth2AccessTokenSecretKey() {
        return secretKeyCache.get("baam_jwt_secretkey");
    }

    @Override
    public byte[] getOAuth2ClientSecretKey() {
        return secretKeyCache.get("baam_client_secretkey");
    }
}
