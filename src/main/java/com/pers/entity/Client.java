package com.pers.entity;

import com.pers.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Клиент.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"firstName", "lastName"})
@ToString
@Builder
@Entity
public class Client implements BaseEntity<UUID> {

    /**
     * Уникальный идентификатор клиента.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Имя клиента.
     */
    private String firstName;

    /**
     * Фамилия клиента.
     */
    private String lastName;

    /**
     * Номер телефона клиента.
     */
    private String phone;

    /**
     * Дата и время регистрации клиента.
     */
    private LocalDateTime createdTime;

    /**
     * Статус клиента.
     */
    @Enumerated(EnumType.STRING)
    private Status status;
}
