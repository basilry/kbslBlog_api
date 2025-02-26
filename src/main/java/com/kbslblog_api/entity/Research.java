package com.kbslblog_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "research")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Research {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 자동 생성

    private String title;
    private String subTitle;
    private String date;  // 예: "20220513"
    private String url;   // 선택적
}
