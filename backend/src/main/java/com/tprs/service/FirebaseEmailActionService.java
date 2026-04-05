package com.tprs.service;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Builds Firebase email action links and converts them to the app's landing handler URL.
 */
public class FirebaseEmailActionService {

    private final String authActionUrl;

    public FirebaseEmailActionService(String authActionUrl) {
        this.authActionUrl = authActionUrl;
    }

    public String buildVerificationActionLink(String email) throws Exception {
        String firebaseLink = FirebaseAuth.getInstance().generateEmailVerificationLink(email, buildActionCodeSettings());
        return toAppActionLink(firebaseLink, "verifyEmail");
    }

    public String buildResetPasswordActionLink(String email) throws Exception {
        String firebaseLink = FirebaseAuth.getInstance().generatePasswordResetLink(email, buildActionCodeSettings());
        return toAppActionLink(firebaseLink, "resetPassword");
    }

    private ActionCodeSettings buildActionCodeSettings() {
        return ActionCodeSettings.builder()
                .setUrl(authActionUrl)
                .setHandleCodeInApp(false)
                .build();
    }

    private String toAppActionLink(String firebaseLink, String fallbackMode) throws Exception {
        URI uri = new URI(firebaseLink);
        Map<String, String> query = parseQuery(uri.getRawQuery());

        String mode = getOrDefault(query.get("mode"), fallbackMode);
        String oobCode = query.get("oobCode");
        String apiKey = query.get("apiKey");

        if (oobCode == null || oobCode.isEmpty() || apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Firebase action link is missing required parameters.");
        }

        StringJoiner joiner = new StringJoiner("&");
        addParam(joiner, "mode", mode);
        addParam(joiner, "oobCode", oobCode);
        addParam(joiner, "apiKey", apiKey);

        if (query.containsKey("continueUrl")) {
            addParam(joiner, "continueUrl", query.get("continueUrl"));
        }
        if (query.containsKey("lang")) {
            addParam(joiner, "lang", query.get("lang"));
        }
        if (query.containsKey("langCode")) {
            addParam(joiner, "langCode", query.get("langCode"));
        }

        String separator = authActionUrl.contains("?") ? "&" : "?";
        return authActionUrl + separator + joiner;
    }

    private static String getOrDefault(String value, String fallback) {
        return (value == null || value.isEmpty()) ? fallback : value;
    }

    private static void addParam(StringJoiner joiner, String key, String value) {
        joiner.add(URLEncoder.encode(key, StandardCharsets.UTF_8)
                + "="
                + URLEncoder.encode(value, StandardCharsets.UTF_8));
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> params = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return params;
        }

        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
            if (pair == null || pair.isEmpty()) {
                continue;
            }
            int idx = pair.indexOf('=');
            String key;
            String value;
            if (idx < 0) {
                key = pair;
                value = "";
            } else {
                key = pair.substring(0, idx);
                value = pair.substring(idx + 1);
            }
            params.put(
                    URLDecoder.decode(key, StandardCharsets.UTF_8),
                    URLDecoder.decode(value, StandardCharsets.UTF_8)
            );
        }
        return params;
    }
}
