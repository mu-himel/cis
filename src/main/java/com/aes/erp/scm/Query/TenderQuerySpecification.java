package com.aes.erp.scm.Query;

import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TenderQuerySpecification {
    public static Specification<Tender> getTenderSpecification(
            Optional<String> searchFilter,
            Optional<TenderType> tenderType,
            Optional<Long> startDate,
            Optional<Long> endDate) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            root.join("tenderItems", JoinType.LEFT);
            List<Predicate> predicates = new ArrayList<>();

            Predicate searchFilterPredicate;
            if (searchFilter.isPresent() && !searchFilter.get().isEmpty()) {
                searchFilterPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("itemCategory").get("name")),
                                "%" + searchFilter.get().toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenderCreator").get("name")),
                                "%" + searchFilter.get().toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenderStatus").as(String.class)),
                                "%" + searchFilter.get().toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenderType").as(String.class)),
                                "%" + searchFilter.get().toLowerCase() + "%")
                );
                if(!searchFilterPredicate.isNegated())predicates.add(searchFilterPredicate);
            }

            Predicate tenderTypePredicate = tenderType.map(type ->
                            criteriaBuilder.equal(root.get("tenderType"), type))
                    .orElse(criteriaBuilder.conjunction());
            if(!tenderTypePredicate.isNegated())predicates.add(tenderTypePredicate);

            Predicate startDatePredicate = startDate.map(date ->
                            criteriaBuilder.greaterThanOrEqualTo(root.get("creationDate"), date))
                    .orElse(criteriaBuilder.conjunction());
            if(!startDatePredicate.isNegated())predicates.add(startDatePredicate);

            Predicate endDatePredicate = endDate.map(date ->
                            criteriaBuilder.lessThanOrEqualTo(root.get("creationDate"), date))
                    .orElse(criteriaBuilder.conjunction());
            if(!endDatePredicate.isNegated())predicates.add(endDatePredicate);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
