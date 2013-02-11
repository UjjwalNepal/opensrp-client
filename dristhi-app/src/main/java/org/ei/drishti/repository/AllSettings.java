package org.ei.drishti.repository;

import android.content.SharedPreferences;

public class AllSettings {
    public static final String APPLIED_VILLAGE_FILTER_SETTING_KEY = "appliedVillageFilter";
    public static final String PREVIOUS_FETCH_INDEX_SETTING_KEY = "previousFetchIndex";
    public static final String ANM_IDENTIFIER_PREFERENCE_KEY = "anmIdentifier";
    private static final String COMMCARE_KEY_ID = "commCareKeyID";
    private static final String COMMCARE_PUBLIC_KEY = "commCarePublicKey";
    private static final String ANM_PASSWORD_PREFERENCE_KEY = "anmPassword";
    private SharedPreferences preferences;
    private SettingsRepository settingsRepository;

    public AllSettings(SharedPreferences preferences, SettingsRepository settingsRepository) {
        this.preferences = preferences;
        this.settingsRepository = settingsRepository;
    }

    public void registerANM(String userName, String password) {
        preferences.edit().putString(ANM_IDENTIFIER_PREFERENCE_KEY, userName).commit();
        settingsRepository.updateSetting(ANM_PASSWORD_PREFERENCE_KEY, password.trim());
    }

    public String fetchRegisteredANM() {
        return preferences.getString(ANM_IDENTIFIER_PREFERENCE_KEY, "").trim();
    }

    public void savePreviousFetchIndex(String value) {
        settingsRepository.updateSetting(PREVIOUS_FETCH_INDEX_SETTING_KEY, value);
    }

    public String fetchPreviousFetchIndex() {
        return settingsRepository.querySetting(PREVIOUS_FETCH_INDEX_SETTING_KEY, "0");
    }

    public void saveAppliedVillageFilter(String village) {
        settingsRepository.updateSetting(APPLIED_VILLAGE_FILTER_SETTING_KEY, village);
    }

    public String appliedVillageFilter(String defaultFilterValue) {
        return settingsRepository.querySetting(APPLIED_VILLAGE_FILTER_SETTING_KEY, defaultFilterValue);
    }

    public void saveCommCareKeyID(String keyId) {
        settingsRepository.updateSetting(COMMCARE_KEY_ID, keyId);
    }

    public void saveCommCarePublicKey(byte[] publicKey) {
        settingsRepository.updateBLOB(COMMCARE_PUBLIC_KEY, publicKey);
    }

    public String fetchCommCareKeyID() {
        return settingsRepository.querySetting(COMMCARE_KEY_ID, null);
    }

    public byte[] fetchCommCarePublicKey() {
        return settingsRepository.queryBLOB(COMMCARE_PUBLIC_KEY);
    }

    public String fetchANMPassword() {
        return settingsRepository.querySetting(ANM_PASSWORD_PREFERENCE_KEY, "");
    }
}
