package co.aikar.locales;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public class LocaleManager <T> {

    private final Function<T, Locale> localeMapper;
    private final Locale defaultLocale;
    private final Map<Locale, LanguageTable> tables = new HashMap<>();

    LocaleManager(Function<T, Locale> localeMapper, Locale defaultLocale) {
        this.localeMapper = localeMapper;
        this.defaultLocale = defaultLocale;
    }

    public static <T> LocaleManager<T> create(@NonNls Function<T, Locale> localeMapper) {
        return create(localeMapper, Locale.ENGLISH);
    }

    public static <T> LocaleManager<T> create(@NonNls Function<T, Locale> localeMapper, Locale defaultLocale) {
        return new LocaleManager<>(localeMapper, defaultLocale);
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void addMessageBundle(String bundleName, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
        for (String key : bundle.keySet()) {
            addMessage(locale, MessageKey.of(key), bundle.getString(key));
        }
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
        if (message == null && !locale.getCountry().isEmpty()) {
            message = getTable(new Locale(locale.getLanguage())).getMessage(key);
        }

        if (message == null && !Objects.equals(locale, defaultLocale)) {
            message = getTable(defaultLocale).getMessage(key);
        }

        return message;
    }

    public LanguageTable getTable(Locale locale) {
        return tables.computeIfAbsent(locale, LanguageTable::new);
    }

}
