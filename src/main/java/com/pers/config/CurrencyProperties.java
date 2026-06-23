package com.pers.config;

import com.pers.enums.Currency;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "currency")
public record CurrencyProperties(@NotEmpty List<Currency> targetCurrencies) {

    public CurrencyProperties {
        targetCurrencies = targetCurrencies == null
                ? List.of()
                : List.copyOf(targetCurrencies);
    }
}
