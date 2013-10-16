package com.viadeo.kasper.test.platform;

import com.google.common.collect.Maps;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.Entity;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class EntityClauseComponent<CLAUSE extends Clause, ENTITY extends Entity> extends ClauseComponent<CLAUSE> {

    private final Class<ENTITY> entityClass;
    private KasperID id;
    private final Map<String, Object> fields = Maps.newHashMap();

    EntityClauseComponent(final CLAUSE clause, Class<ENTITY> entityClass) {
        super(clause);
        this.entityClass = checkNotNull(entityClass);
    }

    public EntityClauseComponent<CLAUSE, ENTITY> withId(final KasperID id) {
        this.id = checkNotNull(id);
        return this;
    }

    public EntityClauseComponent<CLAUSE, ENTITY> withField(final String name, final Object value) {
        this.fields.put(checkNotNull(name), checkNotNull(value));
        return this;
    }

}
