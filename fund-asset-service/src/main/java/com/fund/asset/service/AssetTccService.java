package com.fund.asset.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

@LocalTCC
public interface AssetTccService {
    @TwoPhaseBusinessAction(name = "pendingAssetReserve", commitMethod = "confirm", rollbackMethod = "cancel")
    boolean tryReserve(@BusinessActionContextParameter(paramName = "orderId") String orderId,
                       @BusinessActionContextParameter(paramName = "userId") Long userId,
                       @BusinessActionContextParameter(paramName = "productCode") String productCode,
                       @BusinessActionContextParameter(paramName = "shares") Long shares);

    boolean confirm(BusinessActionContext context);

    boolean cancel(BusinessActionContext context);
}
