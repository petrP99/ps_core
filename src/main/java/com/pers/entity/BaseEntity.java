package com.pers.entity;

import java.io.Serializable;

/**
 * Базовый интерфейс для всех сущностей, обеспечивающий единый способ получения идентификатора.
 *
 * @param <T> тип идентификатора сущности
 */
public interface BaseEntity<T extends Serializable> {

    /**
     * Возвращает идентификатор сущности.
     *
     * @return идентификатор сущности
     */
    T getId();

    /**
     * Устанавливает идентификатор сущности.
     *
     * @param id идентификатор сущности
     */
    void setId(T id);
}