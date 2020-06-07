package com.example.specifitations;

import com.example.domain.Worker;
import javafx.util.Pair;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class WorkerSpecifitation {
    public static Specification<Worker> workersWith(final Pair<String,String> filters) {
        return new Specification<Worker>() {
            @Override
            public Predicate toPredicate(Root<Worker> root,
                                         CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                if(Integer.parseInt(filters.getValue())<0)
                    return criteriaBuilder.not(root.isNull());

                return criteriaBuilder.like(root.get(filters.getKey()),filters.getValue());
            }
        };
    }
}
