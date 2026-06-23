package com.pers.service;

import com.pers.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountClosureCheckService {

    public boolean approve(Account account) {
        log.info("Имитация внешней проверки закрытия счета accountId={}", account.getId());
        return true;
    }
}
