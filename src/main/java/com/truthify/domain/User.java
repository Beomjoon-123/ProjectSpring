package com.truthify.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //  PK INT 타입으로 통일

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(name = "login_pw", nullable = false, length = 255)
    private String loginPw;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "role", nullable = false, length = 20)
    private String role; // ROLE_USER, ROLE_SELLER, ROLE_ADMIN
    
    @CreatedDate
    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;
    
    @Column(name = "nickname") 
    private String nickname;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;
    // User 엔티티의 연관 관계는 필요할 때 추가할 예정 (mappedBy)
    // 1:N 관계: List<AdText> adTexts;
    // 1:N 관계: List<UserFeedback> feedbacks; 
    
    // Lombok @Builder, 생성자 등 생략
}