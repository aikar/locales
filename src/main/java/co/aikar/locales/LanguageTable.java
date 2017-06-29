package co.aikar.locales;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class LanguageTable {

    private final Locale locale;
    private final Map<MessageKey, String> messages = new HashMap<>();

    LanguageTable(Locale locale) {
        this.locale = locale;
    }

    public String addMessage(MessageKey key, String message) {
        return messages.put(key, message);
    }

    public String getMessage(MessageKey key) {
        return messages.get(key);
    }

    public void addMessages(@NotNull Map<MessageKey, String> messages) {
        this.messages.putAll(messages);
    }

    public Locale getLocale() {
        return locale;
    }
}
