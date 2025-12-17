package com.truthify.service;

import com.truthify.dao.BannedWordMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BanWordMatchService {

    private final BannedWordMapper bannedWordMapper;

    public MatchResult match(String content) {
        if (content == null || content.isBlank()) {
            return new MatchResult(0, Collections.emptyList());
        }

        String normalized = normalize(content);
        List<String> keywords = bannedWordMapper.findActiveKeywords();

        if (keywords == null || keywords.isEmpty()) {
            return new MatchResult(0, Collections.emptyList());
        }

        Set<String> matched = new LinkedHashSet<>();
        for (String kw : keywords) {
            if (kw == null) continue;
            String k = kw.trim();
            if (k.isEmpty()) continue;

            if (normalized.contains(normalize(k))) {
                matched.add(k);
            }
        }

        return new MatchResult(matched.size(), new ArrayList<>(matched));
    }

    private String normalize(String s) {
        return s.toLowerCase().replace(" ", "");
    }

    @Getter
    @AllArgsConstructor
    public static class MatchResult {
        private final int count;
        private final List<String> matchedKeywords;
    }
}
