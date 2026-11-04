package com.fund.payment.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

@LocalTCC
public interface PaymentTccService {
    @TwoPhaseBusinessAction(name = "paymentFreeze", commitMethod = "confirm", rollbackMethod = "cancel")
    boolean tryFreeze(@BusinessActionContextParameter(paramName = "requestId") String requestId,
                      @BusinessActionContextParameter(paramName = "orderId") String orderId,
                      @BusinessActionContextParameter(paramName = "userId") Long userId,
                      @BusinessActionContextParameter(paramName = "amount") Long amount);

    boolean confirm(BusinessActionContext context);

    boolean cancel(BusinessActionContext context);
}
