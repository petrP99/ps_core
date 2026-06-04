package com.pers.repository;

import com.pers.dto.filter.TransferFilterDto;
import com.pers.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FilterTransferRepository {

    Page<Transfer> findAllByFilter(TransferFilterDto filterDto, Pageable pageable);

    Page<Transfer> findAllByClientByFilter(TransferFilterDto filterDto, Pageable pageable, UUID clientId);

}