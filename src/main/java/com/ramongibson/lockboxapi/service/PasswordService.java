package com.ramongibson.lockboxapi.service;

import com.ramongibson.lockboxapi.model.Password;
import com.ramongibson.lockboxapi.model.User;
import com.ramongibson.lockboxapi.repository.PasswordRepository;
import com.ramongibson.lockboxapi.repository.UserRepository;
import com.ramongibson.lockboxapi.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

    private final PasswordRepository passwordRepository;
    private final UserRepository userRepository;

    private final EncryptionUtil encryptionUtil;

    public Password addPassword(String username, Password password) {
        try {
            log.info("Attempting to add a password for user: {}", username);

            User user = userRepository.findById(username)
                    .orElseThrow(() -> {
                        log.error("User with username '{}' not found", username);
                        return new RuntimeException("User not found");
                    });

            // Encrypt the password using the user's master password
            byte[] salt = encryptionUtil.generateSalt();
            SecretKey secretKey = encryptionUtil.deriveKey(user.getPassword(), salt);
            byte[] iv = encryptionUtil.generateIV();
            String encryptedPassword = encryptionUtil.encryptPassword(password.getValue(), secretKey, iv);

            // Set the encrypted password and other fields
            password.setValue(encryptedPassword);
            password.setUser(user);
            Password savedPassword = passwordRepository.save(password);
            log.info("Password added successfully for user: {}", username);
            return savedPassword;
        } catch (Exception e) {
            log.error("Error occurred while adding password for user '{}': {}", username, e.getMessage(), e);
            throw new RuntimeException("An error occurred while adding the password");
        }
    }

    public Password updatePassword(String username, Long passwordId, Password updatedPassword) {
        try {
            log.info("Attempting to update password with ID: {} for user: {}", passwordId, username);

            Optional<Password> existingPassword = passwordRepository.findById(passwordId);
            if (existingPassword.isPresent() && existingPassword.get().getUser().getUsername().equals(username)) {
                Password password = existingPassword.get();

                // Encrypt the updated password using the user's master password
                byte[] salt = encryptionUtil.generateSalt();
                SecretKey secretKey = encryptionUtil.deriveKey(password.getUser().getPassword(), salt);
                byte[] iv = encryptionUtil.generateIV();
                String encryptedPassword = encryptionUtil.encryptPassword(updatedPassword.getValue(), secretKey, iv);

                // Update the password fields
                password.setValue(encryptedPassword);
                password.setDescription(updatedPassword.getDescription());
                password.setCategory(updatedPassword.getCategory());

                Password updated = passwordRepository.save(password);
                log.info("Password with ID: {} updated successfully for user: {}", passwordId, username);
                return updated;
            } else {
                log.error("Password with ID: {} not found or access denied for user: {}", passwordId, username);
                throw new RuntimeException("Password not found or access denied");
            }
        } catch (Exception e) {
            log.error("Error occurred while updating password with ID: {} for user '{}': {}", passwordId, username, e.getMessage(), e);
            throw new RuntimeException("An error occurred while updating the password");
        }
    }

    public void deletePassword(String username, Long passwordId) {
        try {
            log.info("Attempting to delete password with ID: {} for user: {}", passwordId, username);

            Password password = passwordRepository.findById(passwordId)
                    .orElseThrow(() -> {
                        log.error("Password with ID: {} not found", passwordId);
                        return new RuntimeException("Password not found");
                    });

            if (password.getUser().getUsername().equals(username)) {
                passwordRepository.delete(password);
                log.info("Password with ID: {} deleted successfully for user: {}", passwordId, username);
            } else {
                log.error("Access denied to delete password with ID: {} for user: {}", passwordId, username);
                throw new RuntimeException("Access denied");
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting password with ID: {} for user '{}': {}", passwordId, username, e.getMessage(), e);
            throw new RuntimeException("An error occurred while deleting the password");
        }
    }

    public List<Password> getAllPasswords(String username) {
        try {
            log.debug("Retrieving all passwords for user: {}", username);
            List<Password> passwords = passwordRepository.findByUserUsername(username);
            log.info("Retrieved {} passwords for user: {}", passwords.size(), username);
            return passwords;
        } catch (Exception e) {
            log.error("Error occurred while retrieving passwords for user '{}': {}", username, e.getMessage(), e);
            throw new RuntimeException("An error occurred while retrieving passwords");
        }
    }
}