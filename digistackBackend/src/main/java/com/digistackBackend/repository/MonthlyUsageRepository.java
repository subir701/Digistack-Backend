package com.digistackBackend.repository;

import com.digistackBackend.model.MonthlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.Optional;

@Repository
public interface MonthlyUsageRepository extends JpaRepository<MonthlyUsage,Long> {
    Optional<MonthlyUsage> findByMonth(YearMonth month);
    //Handy method to find MonthlyUsage with YearMonth
}
