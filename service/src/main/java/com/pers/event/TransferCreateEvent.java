package com.pers.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferCreateEvent {

    private Long transferId;
    private Long clientId;
    private Long cardIdFrom;
    private Long cardIdTo;
    private BigDecimal amount;
    private LocalDateTime time;
    private String recipient;
    private String message;
    private String status;
}
