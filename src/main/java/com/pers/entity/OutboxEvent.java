package com.pers.entity;

import com.pers.enums.OutboxEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Событие исходящей очереди (outbox) для отправки в Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outbox_event")
public class OutboxEvent implements BaseEntity<UUID> {

    /**
     * Уникальный идентификатор события.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Идентификатор агрегата, к которому относится событие.
     */
    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    /**
     * Тип события.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private OutboxEventType eventType;

    /**
     * Ключ события для Kafka.
     */
    @Column(name = "event_key", nullable = false)
    private String eventKey;

    /**
     * JSON-представление полезной нагрузки события.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "trace_parent", length = 128)
    private String traceParent;

    /**
     * Статус обработки события.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxEventType status;

    /**
     * Количество попыток публикации.
     */
    @Column(nullable = false)
    private int attempts;

    /**
     * Дата и время создания события.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время следующей попытки публикации.
     */
    @Column(name = "next_attempt_at", nullable = false)
    private LocalDateTime nextAttemptAt;

    /**
     * Дата и время успешной публикации.
     */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /**
     * Текст последней ошибки при публикации.
     */
    @Column(name = "last_error", length = 1000)
    private String lastError;
}
