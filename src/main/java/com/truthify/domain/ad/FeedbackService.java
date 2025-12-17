package com.truthify.domain.ad;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.truthify.domain.UserFeedback;
import com.truthify.domain.user.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackMapper feedbackMapper;

    @Transactional
    public FeedbackAction vote(Member member, Long adTextId, int trustScore) {

        if (trustScore != 0 && trustScore != 1) {
            throw new IllegalArgumentException("trustScore는 0 또는 1만 가능합니다");
        }

        try {
            // 1) 일단 insert 시도
            UserFeedback feedback = UserFeedback.builder()
                    .member(member)
                    .adTextId(adTextId)
                    .trustScore(trustScore)
                    .build();

            feedbackMapper.save(feedback);
            return FeedbackAction.CREATED;

        } catch (DuplicateKeyException e) {
            // 2) 이미 존재하면 기존 값 확인 후 duplicate / update
            Integer existing = feedbackMapper.findMyVote(member.getId(), adTextId);

            if (existing != null && existing.intValue() == trustScore) {
                return FeedbackAction.DUPLICATE;
            }

            feedbackMapper.update(member.getId(), adTextId, trustScore);
            return FeedbackAction.UPDATED;
        }
    }



    public FeedbackStats getStats(Long adTextId) {
        FeedbackStats stats = feedbackMapper.findStatsByAdId(adTextId);
        stats.setAdTextId(adTextId);
        return stats;
    }

    public Integer getMyVote(Member member, Long adTextId) {
        return feedbackMapper.findMyVote(member.getId(), adTextId);
    }
}