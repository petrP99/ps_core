package com.pers.mapper;

import com.pers.dto.response.AccountResponseDto;
import com.pers.dto.response.CardResponseDto;
import com.pers.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для преобразования сущности Account в AccountResponseDto.
 */
@Mapper(componentModel = SPRING)
public interface AccountReadMapper {

    /**
     * Преобразует сущность Account в AccountResponseDto.
     *
     * @param account сущность счёта
     * @param cards   список карт, привязанных к счёту
     * @return AccountResponseDto
     */
    @Mapping(target = "cards", source = "cards")
    AccountResponseDto toDto(Account account, List<CardResponseDto> cards);
}