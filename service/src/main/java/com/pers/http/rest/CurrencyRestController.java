package com.pers.http.rest;

import com.pers.dto.response.CurrencyRatesResponseDto;
import com.pers.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/currency")
public class CurrencyRestController {

    private final CurrencyService currencyService;

    @GetMapping("/rates")
    public ResponseEntity<CurrencyRatesResponseDto> getRates() {
        return ResponseEntity.ok(currencyService.getRates());
    }
}
