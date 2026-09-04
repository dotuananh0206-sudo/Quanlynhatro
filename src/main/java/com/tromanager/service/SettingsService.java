package com.tromanager.service;

import java.util.prefs.Preferences;

public final class SettingsService {
    private static final SettingsService INSTANCE = new SettingsService();
    private final Preferences preferences = Preferences.userNodeForPackage(SettingsService.class);

    private SettingsService() { }

    public static SettingsService getInstance() {
        return INSTANCE;
    }

    public String get(String key, String defaultValue) {
        return preferences.get(key, defaultValue);
    }

    public void put(String key, String value) {
        preferences.put(key, value == null ? "" : value.trim());
    }
}