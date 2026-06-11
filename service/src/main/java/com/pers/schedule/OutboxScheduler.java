package com.pers.schedule;

import com.pers.service.OutboxPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxPublisherService outboxPublisherService;

    @Value("${outbox.publisher.retention-days:7}")
    private long retentionDays;

    @Scheduled(
            fixedDelayString = "${outbox.publisher.fixed-delay-ms:1000}",
            initialDelayString = "${outbox.publisher.initial-delay-ms:1000}"
    )
    public void publishPendingEvents() {
        outboxPublisherService.publishBatch();
    }

    @Scheduled(cron = "${outbox.publisher.cleanup-cron:0 * * * * *}")
    public void cleanupPublishedEvents() {
        int deleted = outboxPublisherService.deletePublishedBefore(
                LocalDateTime.now().minusDays(retentionDays)
        );
        if (deleted > 0) {
            log.info("Deleted {} published outbox events", deleted);
        }
    }
}
