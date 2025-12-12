package team.java.facto_be.domain.welfare.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.global.entity.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_recent_welfare_view")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentWelfareView extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;

    @Column(name = "serv_id", nullable = false, length = 100)
    private String servId;

    @Column(name = "serv_nm", nullable = false, length = 255)
    private String servNm;

    @Column(name = "last_viewed_at", nullable = false)
    private LocalDateTime lastViewedAt;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    public void refreshView(String servNm) {
        this.servNm = servNm;
        this.lastViewedAt = LocalDateTime.now();
        this.viewCount = (this.viewCount == null ? 1 : this.viewCount + 1);
    }
}
