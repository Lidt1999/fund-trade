package com.fund.payment.service;

import com.fund.payment.entity.PaymentTransaction;
import com.fund.payment.repository.PaymentAccountRepository;
import com.fund.payment.repository.PaymentTransactionRepository;
import io.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentTccServiceImpl implements PaymentTccService {
    private final PaymentAccountRepository accounts;
    private final PaymentTransactionRepository transactions;

    public PaymentTccServiceImpl(PaymentAccountRepository accounts, PaymentTransactionRepository transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    @Override
    @Transactional
    public boolean tryFreeze(String requestId, String orderId, Long userId, Long amount) {
        PaymentTransaction existing = transactions.findByRequestId(requestId).orElse(null);
        if (existing != null) {
            return "TRY".equals(existing.getStatus()) || "CONFIRMED".equals(existing.getStatus());
        }
        if (accounts.freeze(userId, amount) == 0) {
            return false;
        }
        PaymentTransaction transaction = new PaymentTransaction(requestId, orderId, userId, amount, "FREEZE");
        transaction.setStatus("TRY");
        transactions.save(transaction);
        return true;
    }

    @Override
    @Transactional
    public boolean confirm(BusinessActionContext context) {
        String requestId = actionValue(context, "requestId");
        PaymentTransaction transaction = transactions.findByRequestId(requestId).orElse(null);
        if (transaction == null || "CONFIRMED".equals(transaction.getStatus())) {
            return transaction != null;
        }
        if (!"TRY".equals(transaction.getStatus())) {
            return "CANCELLED".equals(transaction.getStatus());
        }
        if (accounts.debit(transaction.getUserId(), transaction.getAmount()) == 0) {
            return false;
        }
        transaction.setStatus("CONFIRMED");
        transactions.save(transaction);
        return true;
    }

    @Override
    @Transactional
    public boolean cancel(BusinessActionContext context) {
        String requestId = actionValue(context, "requestId");
        PaymentTransaction transaction = transactions.findByRequestId(requestId).orElse(null);
        if (transaction == null || "CANCELLED".equals(transaction.getStatus())) {
            return transaction != null;
        }
        if ("CONFIRMED".equals(transaction.getStatus())) {
            return true;
        }
        if (accounts.release(transaction.getUserId(), transaction.getAmount()) == 0) {
            return false;
        }
        transaction.setStatus("CANCELLED");
        transactions.save(transaction);
        return true;
    }

    private String actionValue(BusinessActionContext context, String key) {
        Object value = context.getActionContext(key);
        return value == null ? null : value.toString();
    }
}
