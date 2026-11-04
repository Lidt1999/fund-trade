package com.fund.trade.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BusinessDayCalendar {
    private final Set<LocalDate> holidays;

    public BusinessDayCalendar(@Value("${trade.calendar.holidays:}") String configuredHolidays) {
        if (configuredHolidays == null || configuredHolidays.isBlank()) {
            holidays = Collections.emptySet();
        } else {
            holidays = Arrays.stream(configuredHolidays.split(","))
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .map(LocalDate::parse)
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    public LocalDate previousBusinessDay(LocalDate date) {
        LocalDate candidate = date.minusDays(1);
        while (!isBusinessDay(candidate)) {
            candidate = candidate.minusDays(1);
        }
        return candidate;
    }

    public LocalDate nextBusinessDay(LocalDate date) {
        LocalDate candidate = date.plusDays(1);
        while (!isBusinessDay(candidate)) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }

    public boolean isBusinessDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && !holidays.contains(date);
    }
}
