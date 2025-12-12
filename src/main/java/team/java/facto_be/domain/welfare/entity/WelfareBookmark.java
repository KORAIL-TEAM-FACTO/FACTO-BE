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

@Entity
@Table(name = "tbl_welfare_bookmark")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareBookmark extends BaseTimeEntity {

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

    public void updateServNm(String servNm) {
        this.servNm = servNm;
    }
}
