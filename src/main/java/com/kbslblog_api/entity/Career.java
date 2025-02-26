package com.kbslblog_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "career")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Career {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String startDate;  // 예: "201203"
    private String endDate;    // 예: "201702" (빈 문자열인 경우도 고려)
}
