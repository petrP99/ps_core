package com.pers.http.rest;

import com.pers.dto.request.AccountBalanceOperationRequest;
import com.pers.dto.request.AccountOperationContextRequest;
import com.pers.dto.request.CardOperationContextRequest;
import com.pers.dto.request.PhoneOperationContextRequest;
import com.pers.dto.response.AccountOperationContextResponse;
import com.pers.dto.response.CardOperationContextResponse;
import com.pers.http.config.CurrentClientId;
import com.pers.service.BankingOperationSupportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/transfers")
public class BankingOperationSupportController {

    public static final String X_INTERNAL_TOKEN = "X-Internal-Token";
    private final BankingOperationSupportService supportService;


    @PostMapping("/card-context")
    public ResponseEntity<CardOperationContextResponse> cardContext(@RequestHeader(X_INTERNAL_TOKEN) String token,
                                                                    @Valid @RequestBody CardOperationContextRequest request,
                                                                    @CurrentClientId UUID clientId) {
        supportService.verifyInternalToken(token);
        return ResponseEntity.ok(supportService.getCardContext(request, clientId));
    }

    @PostMapping("/phone-context")
    public ResponseEntity<CardOperationContextResponse> phoneContext(@RequestHeader(X_INTERNAL_TOKEN) String token,
                                                                     @Valid @RequestBody PhoneOperationContextRequest request,
                                                                     @CurrentClientId UUID clientId) {
        supportService.verifyInternalToken(token);
        return ResponseEntity.ok(supportService.getPhoneContext(request, clientId));
    }

    @PostMapping("/account-context")
    public ResponseEntity<AccountOperationContextResponse> accountContext(@RequestHeader(X_INTERNAL_TOKEN) String token,
                                                                          @Valid @RequestBody AccountOperationContextRequest request,
                                                                          @CurrentClientId UUID clientId) {
        supportService.verifyInternalToken(token);
        return ResponseEntity.ok(supportService.getAccountContext(request, clientId));
    }

    @PostMapping("/account-execute")
    public ResponseEntity<Void> executeAccountOperation(@RequestHeader(X_INTERNAL_TOKEN) String token,
                                                        @Valid @RequestBody AccountBalanceOperationRequest request,
                                                        @CurrentClientId UUID clientId) {
        supportService.verifyInternalToken(token);
        supportService.executeAccountOperation(request, clientId);
        return ResponseEntity.noContent().build();
    }
}
