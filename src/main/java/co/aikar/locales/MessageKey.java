package co.aikar.locales;

import java.util.Map;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageKey implements MessageKeyProvider {
    private static final AtomicInteger counter = new AtomicInteger(1);
    private static final Map<String, MessageKey> keyMap = new ConcurrentHashMap<>();
    private final int id = counter.getAndIncrement();
    private final String key;

    private MessageKey(String key) {
        this.key = key;
    }

    public static MessageKey of(String key) {
        return keyMap.computeIfAbsent(key.toLowerCase(Locale.ENGLISH).intern(), MessageKey::new);
    }

    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return (this == o);
    }

    public String getKey() {
        return key;
    }

    @Override
    public MessageKey getMessageKey() {
        return this;
    }
}
