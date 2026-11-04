package com.fund.trade.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessDayCalendarTest {
    private final BusinessDayCalendar calendar = new BusinessDayCalendar("2026-09-07");

    @Test
    void skipsWeekendAndConfiguredHoliday() {
        assertThat(calendar.previousBusinessDay(LocalDate.of(2026, 9, 8)))
                .isEqualTo(LocalDate.of(2026, 9, 4));
    }

    @Test
    void skipsWeekendWhenFindingNextBusinessDay() {
        assertThat(calendar.nextBusinessDay(LocalDate.of(2026, 9, 4)))
                .isEqualTo(LocalDate.of(2026, 9, 8));
    }
}
