package com.fund.trade.job;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.fund.trade.service.TADailyConfirmService;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.context.XxlJobHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TAConfirmJob {
    public static final String RESOURCE = "ta-confirmation-job";
    private static final Logger log = LoggerFactory.getLogger(TAConfirmJob.class);

    private final TADailyConfirmService service;

    public TAConfirmJob(TADailyConfirmService service) {
        this.service = service;
    }

    @XxlJob("dailyConfirmJobHandler")
    @SentinelResource(
            value = RESOURCE,
            blockHandler = "handleTaConfirmBlocked",
            fallback = "handleTaConfirmFailure")
    public void dailyConfirmJobHandler() {
        LocalDate confirmDate = LocalDate.now();
        int processed = service.processDailyConfirm(confirmDate);
        XxlJobHelper.log("TA模拟确认完成，日期={}, 处理订单数={}", confirmDate, processed);
    }

    public void handleTaConfirmBlocked(BlockException exception) {
        String message = "TA确认熔断开启，本轮跳过处理，待确认订单将在后续任务重试";
        log.warn("{}: {}", message, exception.getMessage());
        XxlJobHelper.log(message);
    }

    public void handleTaConfirmFailure(Exception exception) {
        String message = "TA确认异常，本轮处理已中止，待确认订单将在后续任务重试";
        log.error(message, exception);
        XxlJobHelper.log("{}: {}", message, exception.getMessage());
    }
}
