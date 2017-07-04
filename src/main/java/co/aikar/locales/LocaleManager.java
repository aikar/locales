package co.aikar.locales;

import org.jetbrains.annotations.NotNull;
/*
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ConfigurationBuilder;
*/

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

@SuppressWarnings({"WeakerAccess", "unused"})
public class LocaleManager <T> {
    private static Pattern SPLIT_PATTERN = Pattern.compile("_");
    //private volatile Reflections resourceScanner;
    private final Class<?> classpathOwner;
    private final Function<T, Locale> localeMapper;
    private final Locale defaultLocale;
    private final Map<Locale, LanguageTable> tables = new HashMap<>();

    LocaleManager(Class<?> classpathOwner, Function<T, Locale> localeMapper, Locale defaultLocale) {
        this.classpathOwner = classpathOwner;
        this.localeMapper = localeMapper;
        this.defaultLocale = defaultLocale;
    }

    /**
     *
     * @param localeMapper Mapper to map a context to Locale
     * @param <T> Context Class Type
     */
    public static <T> LocaleManager<T> create(@NotNull Function<T, Locale> localeMapper) {
        return create(localeMapper, Locale.ENGLISH);
    }

    /**
     *
     * @param localeMapper Mapper to map a context to Locale
     * @param defaultLocale Default Locale
     * @param <T> Context Class Type
     */
    public static <T> LocaleManager<T> create(@NotNull Function<T, Locale> localeMapper, Locale defaultLocale) {
        return create(localeMapper.getClass(), localeMapper, defaultLocale);
    }

    /**
     *
     * @param classpathOwner A class that owns this LocaleManager for ClassPath lookup
     * @param localeMapper Mapper to map a context to Locale
     * @param <T> Context Class Type
     */
    public static <T> LocaleManager<T> create(Class<?> classpathOwner, @NotNull Function<T, Locale> localeMapper) {
        return new LocaleManager<>(classpathOwner, localeMapper, Locale.ENGLISH);
    }

    /**
     *
     * @param classpathOwner A class that owns this LocaleManager for ClassPath lookup
     * @param localeMapper Mapper to map a context to Locale
     * @param defaultLocale Default Locale
     * @param <T> Context Class Type
     */
    public static <T> LocaleManager<T> create(Class<?> classpathOwner, @NotNull Function<T, Locale> localeMapper, Locale defaultLocale) {
        return new LocaleManager<>(classpathOwner, localeMapper, defaultLocale);
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }
/*
    public List<Locale> getResourceBundleLocales(@NotNull String patternPrefix) {
        final ArrayList<Locale> locales = new ArrayList<>();

        try {
            synchronized (this) {
                if (resourceScanner == null) {
                    URL location = this.classpathOwner.getProtectionDomain().getCodeSource().getLocation();
                    String jarPath = URLDecoder.decode(location.getFile(), "UTF-8");
                    //System.out.println("Location: " + location + " - " + jarPath);
                    resourceScanner = new Reflections(new ConfigurationBuilder()
                            //.addUrls(ClasspathHelper.forClassLoader(LocaleManager.class.getClassLoader()))
                            .addUrls(new URL("file://" + jarPath + "/"))
                            .addScanners(new ResourcesScanner()));
                }
            }
            String pattern = patternPrefix + "_.+\\.properties";
            Set<String> list = resourceScanner.getResources(Pattern.compile(pattern));

            for (String s : list) {
                //System.out.println("Processing " + patternPrefix + " : " + s);
                String substring = s.substring(patternPrefix.length()+1, s.indexOf('.'));
                String[] split = SPLIT_PATTERN.split(substring);
                if (split.length <= 1) {
                    locales.add(new Locale(substring));
                } else if (split.length == 2) {
                    locales.add(new Locale(split[0], split[1]));
                } else {
                    locales.add(new Locale(split[0], split[1], split[2]));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        locales.trimToSize();
        return locales;
    }

    *//**
     * Scans for every locale available and loads the matching message bundle
     *//*
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

    /**
     * If a list of locales is supplied, loads the matching message bundle for each locale.
     * If none are supplied, just the default locale is loaded.
     */
    public boolean addMessageBundle(@NotNull String bundleName, @NotNull Locale... locales) {
        if (locales.length == 0) {
            locales = new Locale[] {defaultLocale};
        }
        boolean found = false;
        for (Locale locale : locales) {
            if (getTable(locale).addMessageBundle(bundleName)) {
                //System.out.println("Loaded " + bundleName+":" + locale);
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

    public @NotNull LanguageTable getTable(@NotNull Locale locale) {
        return tables.computeIfAbsent(locale, LanguageTable::new);
    }

}
