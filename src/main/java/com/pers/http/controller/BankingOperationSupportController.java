package com.pers.http.controller;

import com.pers.dto.request.AccountBalanceOperationRequest;
import com.pers.dto.request.AccountOperationContextRequest;
import com.pers.dto.request.CashbackPayoutRequest;
import com.pers.dto.request.CardOperationContextRequest;
import com.pers.dto.request.PhoneOperationContextRequest;
import com.pers.dto.response.AccountOperationContextResponse;
import com.pers.dto.response.CashbackPayoutResponse;
import com.pers.dto.response.CardOperationContextResponse;
import com.pers.dto.response.ClientBalanceSnapshotResponse;
import com.pers.http.config.ClientId;
import com.pers.service.BankingOperationSupportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal")
public class BankingOperationSupportController {

    public static final String X_INTERNAL_TOKEN = "X-Internal-Token";
    private final BankingOperationSupportService supportService;


    @PostMapping("/transfers/card-context")
    public ResponseEntity<CardOperationContextResponse> cardContext(@RequestHeader(X_INTERNAL_TOKEN) String token,
                                                                    @Valid @RequestBody CardOperationContextRequest request,
                                                                    @ClientId UUID clientId) {
        supportService.verifyInternalToken(token);
        return ResponseEntity.ok(supportService.getCardContext(request, clientId));
    }

    @PostMapping("/transfers/phone-context")
    public ResponseEntity<CardOperationContextResponse> phoneContext(@RequestHeader(X_INTERNAL_TOKEN) String token,
                                                                     @Valid @RequestBody PhoneOperationContextRequest request,
                                                                     @ClientId UUID clientId) {
        supportService.verifyInternalToken(token);
        return ResponseEntity.ok(supportService.getPhoneContext(request, clientId));
    }

    @PostMapping("/transfers/account-context")
    public ResponseEntity<AccountOperationContextResponse> accountContext(@RequestHeader(X_INTERNAL_TOKEN) String token,
                                                                          @Valid @RequestBody AccountOperationContextRequest request,
                                                                          @ClientId UUID clientId) {
        supportService.verifyInternalToken(token);
        return ResponseEntity.ok(supportService.getAccountContext(request, clientId));
    }

    @PostMapping("/transfers/account-execute")
    public ResponseEntity<Void> executeAccountOperation(@RequestHeader(X_INTERNAL_TOKEN) String token,
                                                        @Valid @RequestBody AccountBalanceOperationRequest request,
                                                        @ClientId UUID clientId) {
        supportService.verifyInternalToken(token);
        supportService.executeAccountOperation(request, clientId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cashback/payout")
    public ResponseEntity<CashbackPayoutResponse> executeCashbackPayout(
            @RequestHeader(X_INTERNAL_TOKEN) String token,
            @Valid @RequestBody CashbackPayoutRequest request
    ) {
        supportService.verifyCashbackInternalToken(token);
        return ResponseEntity.ok(supportService.executeCashbackPayout(request));
    }

    @GetMapping("/cashback/client-balances")
    public ResponseEntity<List<ClientBalanceSnapshotResponse>> cashbackClientBalances(
            @RequestHeader(X_INTERNAL_TOKEN) String token
    ) {
        supportService.verifyCashbackInternalToken(token);
        return ResponseEntity.ok(supportService.getClientBalanceSnapshotsForCashback());
    }
}
