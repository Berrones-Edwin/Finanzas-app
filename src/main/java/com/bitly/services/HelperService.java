package com.bitly.services;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class HelperService {

    public LocalDateTime startOfMonth(LocalDate month) {

        LocalDateTime start = month.withDayOfMonth(1).atStartOfDay();

        return start;
    }

    public LocalDateTime endOfMonth(LocalDate month) {

        LocalDateTime end = month.withDayOfMonth(
                month.lengthOfMonth())
                .plusDays(1)
                .atStartOfDay()
                .minusNanos(1);

        return end;
    }
}
