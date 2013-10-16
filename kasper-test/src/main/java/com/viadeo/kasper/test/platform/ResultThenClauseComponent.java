package com.viadeo.kasper.test.platform;

import com.google.common.collect.Lists;
import com.viadeo.kasper.ddd.Entity;

import java.util.List;

public class ResultThenClauseComponent extends ClauseComponent<ThenClause> {

    private List<EntityClauseComponent> results = Lists.newArrayList();

    ResultThenClauseComponent(final ThenClause clause) {
        super(clause);
    }

    public <E extends Entity> EntityClauseComponent<ResultThenClauseComponent, E> contains(final Class<E> entityClass) {
        final EntityClauseComponent<ResultThenClauseComponent, E> entity = new EntityClauseComponent<>(this, entityClass);
        results.add(entity);
        return entity;
    }

}
