package in.batur.eksiclone.security.util;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.batur.eksiclone.security.config.KeyStoreProperties;
import jakarta.annotation.PostConstruct;

@Component
public class KeyPairManager {
    private static final Logger logger = LoggerFactory.getLogger(KeyPairManager.class);

    private KeyPair keyPair;
    
    @Autowired
    private KeyStoreProperties keyStoreProperties;

    @PostConstruct
    public void init() {
        try {
            File keyStoreDir = new File(keyStoreProperties.getPath());
            if (!keyStoreDir.exists()) {
                logger.error("Key store directory does not exist: {}", keyStoreDir.getAbsolutePath());
                throw new RuntimeException("Key store directory does not exist. Please create it manually.");
            }
            
            File privateKeyFile = new File(keyStoreDir, keyStoreProperties.getPrivateKeyFile());
            File publicKeyFile = new File(keyStoreDir, keyStoreProperties.getPublicKeyFile());
            
            // Anahtarların var olup olmadığını kontrol edelim
            if (privateKeyFile.exists() && publicKeyFile.exists()) {
                this.keyPair = loadKeysFromFiles(privateKeyFile, publicKeyFile);
                logger.info("Loaded existing key pair from: {}", keyStoreDir.getAbsolutePath());
            } else {
                // Anahtarlar yoksa hata fırlatalım - artık otomatik oluşturmayacak
                logger.error("Key files not found at: {}", keyStoreDir.getAbsolutePath());
                throw new RuntimeException("Key files not found. Please create them manually.");
            }
            
            // Anahtar çiftinin yüklenip yüklenmediğini kontrol edelim
            if (this.keyPair == null) {
                throw new RuntimeException("Key pair is null after initialization");
            }
            
        } catch (Exception e) {
            logger.error("Error initializing JWT key pair", e);
            throw new RuntimeException("JWT key pair initialization error", e);
        }
    }
    
    private KeyPair loadKeysFromFiles(File privateKeyFile, File publicKeyFile) throws Exception {
        try {
            // PEM formatındaki özel anahtarı oku
            String privateKeyContent = Files.readString(privateKeyFile.toPath());
            privateKeyContent = privateKeyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
            
            // PEM formatındaki genel anahtarı oku
            String publicKeyContent = Files.readString(publicKeyFile.toPath());
            publicKeyContent = publicKeyContent
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);
            
            // Anahtar baytlarını gerçek anahtar nesnelerine dönüştür
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            logger.error("Error loading keys from files", e);
            throw e;
        }
    }
    
    // generateAndSaveNewKeyPair metodunu tamamen kaldırabilirsiniz

    public RSAPublicKey getPublicKey() {
        if (keyPair == null) {
            logger.error("Key pair is null when getPublicKey() was called");
            throw new IllegalStateException("Key pair not initialized");
        }
        return (RSAPublicKey) keyPair.getPublic();
    }

    public RSAPrivateKey getPrivateKey() {
        if (keyPair == null) {
            logger.error("Key pair is null when getPrivateKey() was called");
            throw new IllegalStateException("Key pair not initialized");
        }
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    public String getPublicKeyAsBase64() {
        return Base64.getEncoder().encodeToString(getPublicKey().getEncoded());
    }
}