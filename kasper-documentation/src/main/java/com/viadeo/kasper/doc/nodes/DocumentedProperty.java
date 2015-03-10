// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.KasperResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class DocumentedProperty {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentedProperty.class);

	private final String name;
    private final String description;
	private final String type;
	private final String keyType;
    private final String defaultValues;
    private final Boolean isList;
    private final Boolean isMap;
    private final Boolean isLinkedConcept;
    private final Boolean isQueryResult;
    private final Set<DocumentedConstraint> constraints;
    private final int level;
    private List<DocumentedProperty> properties;
    private List<DocumentedProperty> keyProperties;

    private String elemType;
    private Boolean mandatory = false; /** javax.validation.constraints.NotNull */
	
    // ------------------------------------------------------------------------

    public DocumentedProperty(final Class<?> propertyClazz, final int level) {
        this("", "", propertyClazz.getSimpleName(), "", false, false, false, false, Sets.<DocumentedConstraint>newHashSet(), propertyClazz, level);
    }

    public DocumentedProperty(
            final String name,
            final String description,
            final String type,
            final String defaultValues,
            final Boolean isList,
            final Boolean isMap,
            final Boolean isLinkedConcept,
            final Boolean isQueryResult,
            final Set<DocumentedConstraint> constraints,
            final Class<?> propertyClazz
    ) {
        this(name, description, type, defaultValues, isList, isMap, isLinkedConcept, isQueryResult, constraints, propertyClazz, 0);
    }

    public DocumentedProperty(
            final String name,
            final String description,
            final String type,
            final String defaultValues,
            final Boolean isList,
            final Boolean isMap,
            final Boolean isLinkedConcept,
            final Boolean isQueryResult,
            final Set<DocumentedConstraint> constraints,
            final Class<?> propertyClazz,
            final int level
    ) {
        this(name, description, type, null, defaultValues, isList, isMap, isLinkedConcept, isQueryResult, constraints, propertyClazz, null, level);
    }

	public DocumentedProperty(
            final String name,
            final String description,
            final String type,
            final String keyType,
            final String defaultValues,
            final Boolean isList,
            final Boolean isMap,
            final Boolean isLinkedConcept,
            final Boolean isQueryResult,
            final Set<DocumentedConstraint> constraints,
            final Class<?> propertyClazz,
            final Class<?> keyPropertyClazz,
            final int level
    ) {
		this.name = name;
		this.description = description;
		this.type = type;
        this.keyType = keyType;
        this.defaultValues = defaultValues;
        this.isList = isList;
        this.isMap = isMap;
        this.isLinkedConcept = isLinkedConcept;
        this.isQueryResult = isQueryResult;
        this.constraints = constraints;
        this.level = level;

        // Discover by reflection the detail of a complex type
        this.properties = getProperties(name, propertyClazz, level + 1);

        if (keyPropertyClazz != null) {
            this.keyProperties = getProperties(name, keyPropertyClazz, level + 1);

            if (this.keyProperties == null) {
                keyProperties = Lists.newArrayList(new DocumentedProperty(keyPropertyClazz, level + 1));
            }
        }
    }

    private List<DocumentedProperty> getProperties(String name, Class propertyClazz, int level) {
        try {
            if (    ! propertyClazz.isPrimitive() && ! propertyClazz.isEnum() && ! propertyClazz.isInterface() &&
                    ! String.class.isAssignableFrom(propertyClazz) &&
                    ! Number.class.isAssignableFrom(propertyClazz) &&
                    ! Boolean.class.isAssignableFrom(propertyClazz) &&
                    ! ReadableInstant.class.isAssignableFrom(propertyClazz) &&
                    ! Date.class.isAssignableFrom(propertyClazz) &&
                    ! KasperID.class.isAssignableFrom(propertyClazz) &&
                    ! KasperResponse.class.isAssignableFrom(propertyClazz) &&
                    ! KasperReason.class.isAssignableFrom(propertyClazz) &&
                    ! QueryResult.class.isAssignableFrom(propertyClazz) &&
                    ! Throwable.class.isAssignableFrom(propertyClazz)
                    ) {
                DocumentedBean properties = new DocumentedBean(propertyClazz, level + 1);
                return Lists.newArrayList(properties);

            }
        } catch (StackOverflowError s) {
            LOGGER.error("Failed to retrieve the detail of a complex type, <name={}> <class={}>", name, propertyClazz, s);
        }
        return null;
    }
	// ------------------------------------------------------------------------

	public String getName() {
		return this.name;
	}

    public String getDescription() {
        return description;
    }

    public String getType() {
		return this.type;
	}

    public String getKeyType() {
        return keyType;
    }

    public String getDefaultValues() {
        return defaultValues;
    }

    public Boolean isList() {
		return isList;
	}

    public Boolean isMap() {
        return isMap;
    }

    public Boolean getLinkedConcept() {
        return isLinkedConcept;
    }

    public Boolean isQueryResult() {
        return isQueryResult;
    }

    public Boolean isMandatory() {
        return mandatory;
    }

    public Set<DocumentedConstraint> getConstraints() {
        return constraints;
    }

    public String getElemType() {
        return elemType;
    }

    public List<DocumentedProperty> getProperties() {
        return properties;
    }

    public List<DocumentedProperty> getKeyProperties() {
        return keyProperties;
    }

    public int getLevel() {
        return level;
    }

    // ------------------------------------------------------------------------

    public void setMandatory(final Boolean mandatory) {
        this.mandatory = mandatory;
    }


    public void appendConstraint(final String type, final String constraint) {
        constraints.add(new DocumentedConstraint(type, constraint));
    }

    public void setElemType(final String elemType){
        this.elemType = elemType;
    }

}
