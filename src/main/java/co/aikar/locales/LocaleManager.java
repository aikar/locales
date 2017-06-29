package co.aikar.locales;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public class LocaleManager <T> {

    private final Function<T, Locale> localeMapper;
    private Locale defaultLocale = Locale.ENGLISH;
    private final Map<Locale, LanguageTable> tables = new HashMap<>();

    LocaleManager(Function<T, Locale> localeMapper) {
        this.localeMapper = localeMapper;
    }

    public static <T> LocaleManager<T> create(@NonNls Function<T, Locale> localeMapper) {
        return new LocaleManager<>(localeMapper);
    }

    /**
     * Changes the default locale to use if the specified language key is missing for the desired locale
     * @param locale
     * @return Previous default locale
     */
    public Locale setDefaultLocale(Locale locale) {
        Locale prev = this.defaultLocale;
        this.defaultLocale = locale;
        return prev;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void addMessages(Locale locale, @NotNull Map<MessageKey, String> messages) {
        getTable(locale).addMessages(messages);
    }

    public String addMessage(Locale locale, MessageKey key, String message) {
        return getTable(locale).addMessage(key, message);
    }

    public String getMessage(T context, MessageKey key) {
        Locale locale = localeMapper.apply(context);
        String message = getTable(locale).getMessage(key);
        if (message == null && !Objects.equals(locale, defaultLocale)) {
            message = getTable(defaultLocale).getMessage(key);
        }

        return message;
    }

    public LanguageTable getTable(Locale locale) {
        return tables.computeIfAbsent(locale, LanguageTable::new);
    }

}
