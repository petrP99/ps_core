package com.pers.repository.impl;

import com.pers.dto.filter.ClientFilterDto;
import com.pers.entity.Client;
import static com.pers.entity.QClient.client;
import com.pers.repository.FilterClientRepository;
import com.pers.repository.predicate.QPredicate;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;


@RequiredArgsConstructor
public class FilterClientRepositoryImpl implements FilterClientRepository {

    private final EntityManager entityManager;

    @Override
    public Page<Client> findAllByFilter(ClientFilterDto filter, Pageable pageable) {
        var predicate = QPredicate.builder()
                .add(filter.id(), client.id::eq)
                .add(filter.status(), client.status::eq)
                .add(filter.firstName(), client.firstName::containsIgnoreCase)
                .add(filter.lastName(), client.lastName::containsIgnoreCase)
                .add(filter.phone(), client.phone::containsIgnoreCase)
                .buildAnd();

        var query = new JPAQuery<Client>(entityManager)
                .select(client)
                .from(client)
                .where(predicate);

        List<Client> content = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = query.fetchCount();

        return new PageImpl<>(content, pageable, totalCount);

    }
}