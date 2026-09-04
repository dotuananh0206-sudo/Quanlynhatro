package com.tromanager.service;

import java.util.prefs.Preferences;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class AccountService {
    private static final String DEFAULT_PASSWORD = "admin123";
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(AccountService.class);
    private static final AccountService INSTANCE = new AccountService();

    private String username = "admin";
    private String passwordHash;
    private String passwordSalt;
    private String displayName;
    private boolean firstLogin;

    private AccountService() {
        passwordSalt = PREFERENCES.get("passwordSalt", "");
        passwordHash = PREFERENCES.get("passwordHash", "");
        if (passwordHash.isEmpty() || passwordSalt.isEmpty()) {
            passwordSalt = createSalt();
            passwordHash = hash(DEFAULT_PASSWORD, passwordSalt);
            PREFERENCES.put("passwordSalt", passwordSalt);
            PREFERENCES.put("passwordHash", passwordHash);
        }
        displayName = PREFERENCES.get("displayName", "Nguyễn Minh Tuấn");
        firstLogin = PREFERENCES.getBoolean("firstLogin", true);
    }

    public static AccountService getInstance() {
        return INSTANCE;
    }

    public boolean authenticate(String username, String password) {
        return this.username.equals(username == null ? "" : username.trim())
            && verify(password == null ? "" : password);
    }

    public boolean updatePassword(String currentPassword, String newPassword, String confirmation) {
        if (!verify(currentPassword) || newPassword == null || newPassword.length() < 8
                || !newPassword.equals(confirmation)) {
            return false;
        }
        passwordSalt = createSalt();
        passwordHash = hash(newPassword, passwordSalt);
        firstLogin = false;
        PREFERENCES.put("passwordSalt", passwordSalt);
        PREFERENCES.put("passwordHash", passwordHash);
        PREFERENCES.putBoolean("firstLogin", false);
        return true;
    }

    public String resetPassword(String username) {
        if (!this.username.equals(username == null ? "" : username.trim())) {
            return null;
        }
        String temporaryPassword = generateTemporaryPassword();
        passwordSalt = createSalt();
        passwordHash = hash(temporaryPassword, passwordSalt);
        firstLogin = true;
        PREFERENCES.put("passwordSalt", passwordSalt);
        PREFERENCES.put("passwordHash", passwordHash);
        PREFERENCES.putBoolean("firstLogin", true);
        return temporaryPassword;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        if (displayName != null && !displayName.trim().isEmpty()) {
            this.displayName = displayName.trim();
            PREFERENCES.put("displayName", this.displayName);
        }
    }

    private boolean verify(String candidate) {
        return constantTimeEquals(passwordHash, hash(candidate == null ? "" : candidate, passwordSalt));
    }

    private static String createSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private static String hash(String value, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(value.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_LENGTH);
            byte[] encoded = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword();
            return Base64.getEncoder().encodeToString(encoded);
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể tạo hash mật khẩu", ex);
        }
    }

    private static boolean constantTimeEquals(String left, String right) {
        return java.security.MessageDigest.isEqual(left.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                right.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private static String generateTemporaryPassword() {
        byte[] bytes = new byte[12];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, 12);
    }
}
