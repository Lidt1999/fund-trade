package com.fund.trade.job;

import com.fund.trade.service.ReconciliationService;
import com.fund.trade.service.BusinessDayCalendar;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReconciliationJob {
    private final ReconciliationService service;
    private final BusinessDayCalendar calendar;

    public ReconciliationJob(ReconciliationService service, BusinessDayCalendar calendar) {
        this.service = service;
        this.calendar = calendar;
    }

    @XxlJob("dailyReconciliationJobHandler")
    public void dailyReconciliationJobHandler() {
        LocalDate reportDate = calendar.previousBusinessDay(LocalDate.now());
        var report = service.reconcile(reportDate);
        XxlJobHelper.log("日终对账完成，日期={}, 总订单={}, 差异数={}",
                reportDate, report.getTotalOrders(),
                report.getMismatchCount() + report.getMissingInTa() + report.getMissingInSystem());
    }
}
