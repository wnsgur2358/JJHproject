package com.multi.matchon.stadium.domain;

import com.multi.matchon.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "stadium")
public class Stadium extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stadium_id")
    private Long id;

    @Column(name = "stadium_name", nullable = false, length = 100)
    private String stadiumName;

    @Column(name = "stadium_region", nullable = false, length = 100)
    private String stadiumRegion;

    @Column(name = "stadium_address", nullable = false, length = 255)
    private String stadiumAddress;

    @Column(name = "stadium_tel", nullable = false, length = 255)
    private String stadiumTel;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // 예: "1_청계중앙공원축구장.jpg"
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    private Double latitude;
    private Double longitude;

    @Transient
    @Setter
    private double distance;

    public String getFullImageUrl() {
        String regionFolder = switch (stadiumRegion) {
            case "경기도" -> "gyeonggi";
            case "서울특별시" -> "seoul";
            case "부산광역시" -> "busan";
            case "인천광역시" -> "incheon";
            default -> "unknown";
        };

        return "https://matchon-seongeun-bucket.s3.eu-north-1.amazonaws.com/stadium/region/"
                + regionFolder + "/" + imageUrl;
    }


}
