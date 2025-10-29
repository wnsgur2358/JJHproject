package com.multi.matchon.team.dto.res;


import com.multi.matchon.common.domain.PositionName;
import com.multi.matchon.team.domain.RegionType;
import com.multi.matchon.team.domain.Team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResTeamDto {

    private Long teamId;

    private String teamName;

    private String teamRegion;

    private Boolean RecruitmentStatus;


    private String teamIntroduction;

    private String imageUrl;

    private List<String> recruitingPositions;

    private Double teamRatingAverage;

    private String createdBy;


    private String starRatingVisual; // ✅ Add this field


    private Long leaderId;

    private List<String> recruitingPositionsKorean;

    private String translatedRegion;

    private static String translatePosition(PositionName positionName) {
        if (positionName == null) return "미정";
        return switch (positionName) {
            case GOALKEEPER -> "골키퍼";
            case CENTER_BACK -> "센터백";
            case LEFT_RIGHT_BACK -> "좌/우 풀백";
            case LEFT_RIGHT_WING_BACK -> "좌/우 윙백";
            case CENTRAL_DEFENSIVE_MIDFIELDER -> "수비형 미드필더";
            case CENTRAL_MIDFIELDER -> "중앙 미드필더";
            case CENTRAL_ATTACKING_MIDFIELDER -> "공격형 미드필더";
            case LEFT_RIGHT_WING -> "좌/우 윙";
            case STRIKER_CENTER_FORWARD -> "스트라이커";
            case SECOND_STRIKER -> "세컨드 스트라이커";
            case LEFT_RIGHT_WINGER -> "좌/우 윙어";
        };
    }

    private static String translateRegion(RegionType regionType) {
        if (regionType == null) return "미정";
        return switch (regionType) {
            case CAPITAL_REGION -> "수도권";
            case YEONGNAM_REGION -> "영남권";
            case HONAM_REGION -> "호남권";
            case CHUNGCHEONG_REGION -> "충청권";
            case GANGWON_REGION -> "강원권";
            case JEJU -> "제주";
        };
    }

    public static ResTeamDto from(Team team, String imageUrl, double averageRating) {
        return ResTeamDto.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .teamRegion(team.getTeamRegion().name())
                .translatedRegion(translateRegion(team.getTeamRegion()))
                .teamRatingAverage(averageRating) // ← Inject the calculated average
                .starRatingVisual(generateStarRatingVisual(averageRating))  // ⬅️ Add this
                .RecruitmentStatus(team.getRecruitmentStatus())
                .imageUrl(imageUrl != null ? imageUrl : "/img/default-team.png") // ✅ fallback
                .teamIntroduction(team.getTeamIntroduction())
                .createdBy(team.getCreatedPerson())
                .recruitingPositions(
                        team.getRecruitingPositions().stream()
                                .map(rp -> rp.getPositions().getPositionName().name())
                                .collect(Collectors.toList())
                )
                .recruitingPositionsKorean(team.getRecruitingPositions().stream()
                        .map(rp -> translatePosition(rp.getPositions().getPositionName()))
                        .collect(Collectors.toList()))
                .build();
    }

    private static String generateStarRatingVisual(double rating) {
        if (rating == 0.0) return "N/A";
        int fullStars = (int) Math.floor(rating);
        boolean halfStar = rating - fullStars >= 0.5;
        int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
        return "⭐".repeat(fullStars) + (halfStar ? "✩" : "") + "☆".repeat(emptyStars);
    }


}


