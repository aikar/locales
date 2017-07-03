package co.aikar.locales;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Resource;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

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
/*
    public List<Locale> getResourceBundleLocales(@NotNull String bundleName) {
        return getResourceBundleLocales(bundleName, "/");
    }

    public List<Locale> getResourceBundleLocales(@NotNull String bundleName, @NotNull @SuppressWarnings("SameParameterValue") String basePath) {
        final ArrayList<Locale> locales = new ArrayList<>();
        Pattern compile = Pattern.compile("_");
        try {
            URL resource = LocaleManager.class.getResource(basePath);
            if (resource == null) {
                return locales;
            }
            File f = new File(resource.toURI());
            final String bundle = bundleName + "_";
            FilenameFilter filenameFilter = (dir, name) -> name.startsWith(bundle);
            String[] list = f.list(filenameFilter);
            if (list == null) {
                return locales;
            }
            for (String s : list) {
                String substring = s.substring(basePath.length() + bundle.length(), s.indexOf('.'));
                String[] split = compile.split(substring);
                if (split.length <= 1) {
                    locales.add(new Locale(substring));
                } else if (split.length == 2) {
                    locales.add(new Locale(split[0], split[1]));
                } else {
                    locales.add(new Locale(split[0], split[1], split[2]));
                }
            }
        } catch (URISyntaxException x) {
            throw new RuntimeException(x);
        }
        locales.trimToSize();
        return locales;
    }

    public boolean addMessageBundles(@NotNull String... bundles) {
        boolean loaded = false;
        for (String bundle : bundles) {
            for (Locale locale : getResourceBundleLocales(bundle)) {
                if (addMessageBundle(bundle, locale)) {
                    loaded = true;
                }
            }
        }

        return loaded;
    }*/

    public boolean addMessageBundle(@NotNull String bundleName, @NotNull Locale... locales) {
        boolean found = false;
        for (Locale locale : locales) {
            if (getTable(locale).addMessageBundle(bundleName)) {
                found = true;
            }
        }
        return found;
    }

    public void addMessages(@NotNull Locale locale, @NotNull Map<MessageKey, String> messages) {
        getTable(locale).addMessages(messages);
    }

    public String addMessage(@NotNull Locale locale, @NotNull MessageKey key, @NotNull String message) {
        return getTable(locale).addMessage(key, message);
    }

    public String getMessage(T context, @NotNull MessageKey key) {
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

    public LanguageTable getTable(@NotNull Locale locale) {
        return tables.computeIfAbsent(locale, LanguageTable::new);
    }

}
