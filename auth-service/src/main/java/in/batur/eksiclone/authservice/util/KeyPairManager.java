package in.batur.eksiclone.authservice.util;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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

import in.batur.eksiclone.authservice.config.KeyStoreProperties;
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
                keyStoreDir.mkdirs();
                logger.info("Key store directory created: {}", keyStoreDir.getAbsolutePath());
            }
            
            File privateKeyFile = new File(keyStoreDir, keyStoreProperties.getPrivateKeyFile());
            File publicKeyFile = new File(keyStoreDir, keyStoreProperties.getPublicKeyFile());
            
            // Anahtarların var olup olmadığını kontrol edelim
            if (privateKeyFile.exists() && publicKeyFile.exists()) {
                try {
                    this.keyPair = loadKeysFromFiles(privateKeyFile, publicKeyFile);
                    logger.info("Loaded existing key pair from: {}", keyStoreDir.getAbsolutePath());
                } catch (Exception e) {
                    logger.warn("Failed to load existing keys, generating new ones: {}", e.getMessage());
                    this.keyPair = generateAndSaveNewKeyPair(privateKeyFile, publicKeyFile);
                }
            } else {
                // Anahtarlar yoksa otomatik oluşturalım
                logger.info("Key files not found, generating new keys at: {}", keyStoreDir.getAbsolutePath());
                this.keyPair = generateAndSaveNewKeyPair(privateKeyFile, publicKeyFile);
            }
            
            // Anahtar çiftinin yüklenip yüklenmediğini kontrol edelim
            if (this.keyPair == null) {
                throw new RuntimeException("Key pair is null after initialization");
            }
            
            // Doğrulama yapalım - Public key'in gerçekten public key olduğunu test edelim
            if (!(this.keyPair.getPublic() instanceof RSAPublicKey)) {
                throw new RuntimeException("Public key is not an RSA public key");
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
            
            // Oluşturulan anahtarları doğrula
            if (!(privateKey instanceof RSAPrivateKey) || !(publicKey instanceof RSAPublicKey)) {
                throw new Exception("One or both keys are not RSA keys");
            }
            
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            logger.error("Error loading keys from files", e);
            throw e;
        }
    }
    
    // Yeni bir anahtar çifti oluştur ve dosyalara kaydet
    private KeyPair generateAndSaveNewKeyPair(File privateKeyFile, File publicKeyFile) throws Exception {
        try {
            // RSA anahtar çifti oluştur
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair newKeyPair = keyPairGenerator.generateKeyPair();
            
            // Private key'i PEM formatında kaydet
            try (FileWriter privateKeyWriter = new FileWriter(privateKeyFile)) {
                byte[] privateKeyBytes = newKeyPair.getPrivate().getEncoded();
                String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                                       Base64.getEncoder().encodeToString(privateKeyBytes) +
                                       "\n-----END PRIVATE KEY-----";
                privateKeyWriter.write(privateKeyPem);
            }
            
            // Public key'i PEM formatında kaydet
            try (FileWriter publicKeyWriter = new FileWriter(publicKeyFile)) {
                byte[] publicKeyBytes = newKeyPair.getPublic().getEncoded();
                String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                                      Base64.getEncoder().encodeToString(publicKeyBytes) +
                                      "\n-----END PUBLIC KEY-----";
                publicKeyWriter.write(publicKeyPem);
            }
            
            logger.info("Generated and saved new RSA key pair");
            
            return newKeyPair;
        } catch (Exception e) {
            logger.error("Error generating and saving new key pair", e);
            throw e;
        }
    }

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