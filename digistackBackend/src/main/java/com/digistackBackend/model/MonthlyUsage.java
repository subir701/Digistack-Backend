package com.digistackBackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private YearMonth month;

    private Integer totalKeywordUsed;
}
