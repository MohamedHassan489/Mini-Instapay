package com.example.national_bank_of_egypt.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES-256 Encryption Service for sensitive data protection.
 * 
 * <p>This service provides symmetric encryption and decryption capabilities
 * using the Advanced Encryption Standard (AES) algorithm with 256-bit keys.</p>
 * 
 * <h2>Design Pattern: Singleton Pattern</h2>
 * <p>Ensures only one instance exists application-wide, maintaining a single
 * encryption key for consistent encrypt/decrypt operations.</p>
 * 
 * <h2>Security Features:</h2>
 * <ul>
 *   <li><b>Algorithm:</b> AES (Advanced Encryption Standard)</li>
 *   <li><b>Key Size:</b> 256 bits</li>
 *   <li><b>Encoding:</b> Base64 for encrypted output</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * EncryptionService service = EncryptionService.getInstance();
 * String encrypted = service.encrypt("sensitive data");
 * String decrypted = service.decrypt(encrypted);
 * }</pre>
 * 
 * <h2>Important Notes:</h2>
 * <ul>
 *   <li>The key is generated at runtime and stored in memory</li>
 *   <li>For production, keys should be persisted securely (e.g., Key Vault)</li>
 *   <li>Keys should be rotated periodically for security compliance</li>
 * </ul>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 */
public class EncryptionService {
    
    /** Singleton instance of the service */
    private static EncryptionService instance;
    
    /** Encryption algorithm identifier */
    private static final String ALGORITHM = "AES";
    
    /** Cipher transformation string */
    private static final String TRANSFORMATION = "AES";
    
    /** The secret key used for encryption/decryption */
    private SecretKey secretKey;

    /**
     * Private constructor to enforce Singleton pattern.
     * 
     * <p>Generates a random 256-bit AES key on instantiation.(symmetric encryption algorithm)
     * "Advanced Encryption Standard." This key will be used for all encryption/decryption operations.</p>
     */
    private EncryptionService() {
        try {
            // Initialize the key generator for AES
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            // Set key size to 256 bits for strong encryption
            keyGenerator.init(256);
            // Generate and store the secret key
            this.secretKey = keyGenerator.generateKey();
        } catch (Exception e) {
            // Log error - in production, this should use proper logging
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance of EncryptionService.
     * 
     * <p>Thread-safe implementation using synchronized keyword to prevent
     * race conditions during instance creation.</p>
     * 
     * @return The singleton instance of EncryptionService
     */
    public static synchronized EncryptionService getInstance() {
        if (instance == null) {
            instance = new EncryptionService();
        }
        return instance;
    }

    /**
     * Encrypts plaintext data using AES-256.
     * 
     * <p>Encryption process:
     * <ol>
     *   <li>Convert plaintext to UTF-8 bytes</li>
     *   <li>Encrypt using AES cipher with the secret key</li>
     *   <li>Encode encrypted bytes to Base64 string</li>
     * </ol>
     * </p>
     * 
     * @param data The plaintext string to encrypt
     * @return Base64-encoded ciphertext, or {@code null} if encryption fails
     */
    public String encrypt(String data) {
        try {
            // Initialize cipher in encryption mode
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // Encrypt the data
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // Encode to Base64 for safe string storage/transmission
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            // Log error and return null to indicate failure
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypts Base64-encoded ciphertext back to plaintext.
     * 
     * <p>Decryption process:
     * <ol>
     *   <li>Decode Base64 string to encrypted bytes</li>
     *   <li>Decrypt using AES cipher with the secret key</li>
     *   <li>Convert decrypted bytes to UTF-8 string</li>
     * </ol>
     * </p>
     * 
     * @param encryptedData The Base64-encoded ciphertext to decrypt
     * @return The decrypted plaintext string, or {@code null} if decryption fails
     */
    public String decrypt(String encryptedData) {
        try {
            // Initialize cipher in decryption mode
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // Decode Base64 and decrypt
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            // Convert bytes back to string
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Log error and return null to indicate failure
            e.printStackTrace();
            return null;
        }
    }
}

