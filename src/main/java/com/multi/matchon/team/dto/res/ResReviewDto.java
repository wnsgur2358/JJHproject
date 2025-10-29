package com.multi.matchon.team.dto.res;

import com.multi.matchon.common.domain.BoardType;
import com.multi.matchon.common.repository.AttachmentRepository;
import com.multi.matchon.common.util.AwsS3Utils;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.team.domain.Response;
import com.multi.matchon.team.domain.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResReviewDto {
    private Long id; // ✅ THIS is required!
    private String reviewerName;
    private int rating;
    private String content;
    private LocalDateTime createdDate;
    private String response;
    private LocalDateTime respondedAt;

    private Long responseId;
    private String profileImageUrl;



    public static ResReviewDto from(Review review, Response response, AttachmentRepository attachmentRepository, AwsS3Utils awsS3Utils) {
        Member reviewer = review.getMember();

        String profileImageUrl = attachmentRepository.findLatestAttachment(BoardType.MEMBER, reviewer.getId())
                .map(attachment -> awsS3Utils.createPresignedGetUrl("attachments/profile/", attachment.getSavedName()))
                .orElse("/img/default-user.png");

        return ResReviewDto.builder()
                .id(review.getId()) // ✅ THIS too!
                .reviewerName(review.getMember().getMemberName())
                .rating(review.getReviewRating())
                .content(review.getContent())
                .createdDate(review.getCreatedDate())
                .response(response != null ? response.getReviewResponse() : null)
                .respondedAt(response != null ? response.getCreatedDate() : null)

                .responseId(response != null ? response.getId() : null) // ✅ ADD THIS

                .profileImageUrl(profileImageUrl) // ✅ Set it here
                .build();
    }
}