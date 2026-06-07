package com.pers.repository.impl;

import com.pers.dto.filter.CardFilterDto;
import com.pers.entity.Card;
import com.pers.repository.FilterCardRepository;
import com.pers.repository.predicate.QPredicate;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.pers.entity.QCard.card;

@RequiredArgsConstructor
public class FilterCardRepositoryImpl implements FilterCardRepository {

    private final EntityManager entityManager;

    @Override
    public Page<Card> findAllByFilter(CardFilterDto filter, Pageable pageable) {
        var predicate = QPredicate.builder()
//                .add(filter.id(), card.id::eq)
                .add(filter.clientId(), card.clientId::eq)
                .add(filter.expireDate(), card.expireDate::before)
                .add(filter.status(), card.status::eq)
                .buildAnd();

        var query = new JPAQuery<Card>(entityManager)
                .select(card)
                .from(card)
                .where(predicate);

        List<Card> content = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = query.fetchCount();

        return new PageImpl<>(content, pageable, totalCount);

    }

}