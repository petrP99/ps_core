package com.pers.schedule;

import com.pers.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyScheduler implements CommandLineRunner {

    private final CurrencyService currencyService;

    @Override
    public void run(String... args) {
        currencyService.updateRates();
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void dailyUpdate() {
        currencyService.updateRates();
    }
}
