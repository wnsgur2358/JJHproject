package com.multi.matchon.common.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
//@Setter: JPA entity에서 setter사용은 자제, test용
@Table(name="attachment")
public class Attachment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="attachment_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name="board_type", nullable = false)
    private BoardType boardType;

    @Column(name="board_number", nullable = false)
    private Long boardNumber;

    @Column(name="file_order", nullable = false)
    private Integer fileOrder;

    @Column(name="original_name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String originalName;

    @Column(name="saved_name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String savedName;

    @Column(name="save_path", nullable = false, columnDefinition = "VARCHAR(255)")
    private String savePath;

    @Column(name="thumbnail_path", columnDefinition = "VARCHAR(255)")
    private String thumbnailPath;

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted=false;


    public void update(String originalFilename, String savedName, String savePath) {
        this.originalName = originalFilename;
        this.savedName = savedName;
        this.savePath = savePath;

    }

    public void delete(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
