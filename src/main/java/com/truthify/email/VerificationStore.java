package com.truthify.email;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class VerificationStore {

    private final Map<String, CodeEntry> store = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public VerificationStore() {
        // 주기적으로 만료된 항목 정리 (선택적)
        scheduler.scheduleAtFixedRate(this::evictExpired, 1, 1, TimeUnit.MINUTES);
    }

    public void put(String email, String code, long ttlSeconds) {
        Instant expiry = Instant.now().plusSeconds(ttlSeconds);
        store.put(email, new CodeEntry(code, expiry));
    }

    public boolean verify(String email, String code) {
        CodeEntry e = store.get(email);
        if (e == null) return false;
        if (Instant.now().isAfter(e.expiry)) {
            store.remove(email);
            return false;
        }
        boolean ok = e.code.equals(code);
        if (ok) store.remove(email); // 일회성 코드면 성공 시 제거
        return ok;
    }

    private void evictExpired() {
        Instant now = Instant.now();
        store.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiry));
    }

    private static class CodeEntry {
        final String code;
        final Instant expiry;
        CodeEntry(String code, Instant expiry) { this.code = code; this.expiry = expiry; }
    }
}
