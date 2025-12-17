package com.truthify.controller.admin;

import com.truthify.dao.BannedWordMapper;
import com.truthify.domain.BannedWord;
import com.truthify.service.BanWordSuggestService;
import com.truthify.user.dto.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/banwords")
public class AdminBannedWordController {

    private final BannedWordMapper bannedWordMapper;
    private final BanWordSuggestService suggestService;

    @GetMapping
    public ResultData<?> list() {
        return ResultData.of("S-1", "금지어 목록", bannedWordMapper.findAll());
    }

    @PostMapping("/suggest")
    public ResultData<?> suggest(
            @RequestParam(defaultValue = "70") double minRisk,
            @RequestParam(defaultValue = "8") int limit
    ) {
        List<BanWordSuggestService.Suggestion> items = suggestService.suggestFromHighRiskAds(minRisk, limit);
        return ResultData.of("S-1", "추천 완료", items);
    }

    public record AddReq(String keyword, String lawType, String description, Boolean isActive) {}

    @PostMapping
    public ResultData<?> add(@RequestBody AddReq req) {
        if (req.keyword() == null || req.keyword().isBlank()) {
            return ResultData.of("F-1", "keyword 필요");
        }

        BannedWord.LawType lt = BannedWord.LawType.ETC;
        try {
            if (req.lawType() != null) lt = BannedWord.LawType.valueOf(req.lawType());
        } catch (Exception ignore) {}

        BannedWord word = BannedWord.builder()
                .keyword(req.keyword().trim())
                .lawType(lt)
                .description(req.description())
                .build();

        // 기본 active=true
        if (req.isActive() != null && !req.isActive()) {
            // builder에 isActive 없어서 아래처럼 세팅 (도메인에 setter 있으면 그걸로)
            // 없다면 테이블 default 1로 두고, 비활성은 toggle API로 처리해도 OK
        }

        bannedWordMapper.insert(word);
        return ResultData.of("S-1", "추가 완료", word);
    }

    public record ActiveReq(Boolean isActive) {}

    @PatchMapping("/{id}/active")
    public ResultData<?> setActive(@PathVariable Long id, @RequestBody ActiveReq req) {
        if (req.isActive() == null) return ResultData.of("F-1", "isActive 필요");
        int updated = bannedWordMapper.updateActive(id, req.isActive());
        if (updated == 0) return ResultData.of("F-1", "대상 없음");
        return ResultData.of("S-1", req.isActive() ? "활성화" : "비활성화");
    }

    @DeleteMapping("/{id}")
    public ResultData<?> delete(@PathVariable Long id) {
        int deleted = bannedWordMapper.delete(id);
        if (deleted == 0) return ResultData.of("F-1", "대상 없음");
        return ResultData.of("S-1", "삭제 완료");
    }
}
