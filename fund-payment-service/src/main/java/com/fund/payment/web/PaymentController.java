package com.fund.payment.web;

import com.fund.common.api.Result;
import com.fund.payment.entity.PaymentAccount;
import com.fund.payment.entity.PaymentTransaction;
import com.fund.payment.repository.PaymentAccountRepository;
import com.fund.payment.repository.PaymentTransactionRepository;
import com.fund.payment.service.PaymentTccService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentAccountRepository accounts;
    private final PaymentTransactionRepository transactions;
    private final PaymentTccService tcc;

    public PaymentController(PaymentAccountRepository accounts, PaymentTransactionRepository transactions,
                             PaymentTccService tcc) {
        this.accounts = accounts; this.transactions = transactions; this.tcc = tcc;
    }

    @PostMapping("/tcc/try")
    public Result<Boolean> tryFreeze(@Valid @RequestBody PaymentRequest request) {
        return Result.success(tcc.tryFreeze(request.requestId(), request.orderId(), request.userId(), request.amount()));
    }

    @GetMapping("/accounts") public Result<List<PaymentAccount>> accounts() { return Result.success(accounts.findAll()); }
    @PostMapping("/accounts") public Result<PaymentAccount> createAccount(@Valid @RequestBody AccountRequest request) {
        if (accounts.findByUserId(request.userId()).isPresent()) throw badRequest("支付账户已存在");
        return Result.success(accounts.save(new PaymentAccount(request.userId(), request.availableBalance())));
    }

    @PostMapping("/freeze")
    @Transactional
    public Result<PaymentTransaction> freeze(@Valid @RequestBody PaymentRequest request) {
        PaymentTransaction existing = existing(request);
        if (existing != null) return Result.success(existing);
        if (accounts.freeze(request.userId(), request.amount()) == 0) throw badRequest("余额不足");
        return Result.success(transactions.save(new PaymentTransaction(request.requestId(), request.orderId(),
                request.userId(), request.amount(), "FREEZE")));
    }

    @PostMapping("/debit")
    @Transactional
    public Result<PaymentTransaction> debit(@Valid @RequestBody PaymentRequest request) {
        PaymentTransaction existing = existing(request);
        if (existing != null) return Result.success(existing);
        if (accounts.debit(request.userId(), request.amount()) == 0) throw badRequest("冻结资金不足");
        return Result.success(transactions.save(new PaymentTransaction(request.requestId(), request.orderId(),
                request.userId(), request.amount(), "DEBIT")));
    }

    @PostMapping("/release")
    @Transactional
    public Result<PaymentTransaction> release(@Valid @RequestBody PaymentRequest request) {
        PaymentTransaction existing = existing(request);
        if (existing != null) return Result.success(existing);
        if (accounts.release(request.userId(), request.amount()) == 0) throw badRequest("冻结资金不足");
        return Result.success(transactions.save(new PaymentTransaction(request.requestId(), request.orderId(),
                request.userId(), request.amount(), "RELEASE")));
    }

    @PostMapping("/credit")
    @Transactional
    public Result<PaymentTransaction> credit(@Valid @RequestBody PaymentRequest request) {
        PaymentTransaction existing = existing(request);
        if (existing != null) return Result.success(existing);
        if (accounts.credit(request.userId(), request.amount()) == 0) throw notFound("支付账户不存在");
        return Result.success(transactions.save(new PaymentTransaction(request.requestId(), request.orderId(),
                request.userId(), request.amount(), "CREDIT")));
    }

    private PaymentTransaction existing(PaymentRequest request) {
        return transactions.findByRequestId(request.requestId()).orElse(null);
    }
    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
