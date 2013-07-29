
Query services filtering
========================

Kasper framework allows you to define filters on Query services.

These filters can be of two kind :

- Query filters : can be used to mutate the query before its processing by the service
- DTO filters : can be used to mutate the DTO generated after processing of the query by the service

In order to define a service filter, you have to :

1. Implement QueryFilter or DTOFilter interfaces (Kasper core)
2. Add the annotation @XKasperServiceFilter, where you can define an optional name for your filter

ex :

**ValidateIdQueryFilter.class** :

::

    @XKasperServiceFilter( name = "ValidateUniverseId" )
    public class ValidateIdQueryFilter implements QueryFilter {

        @Override
        public void filter(final Context context, final Query query) throws KasperQueryException {
            if (HasAnIdQuery.class.isAssignableFrom(query)) {
                if (((HasAnIdQuery) query).id > 42) {
                    throw new KasperQueryException("The id cannot be greater than 42 !");
                }
            }
        }

    }

A filter can be defined global (set the global flag (global = true) on the annotation).

**IdEraserDTOFilter.class** :

::

    @XKasperServiceFilter( global = true ) // Will be applied to all query services
    public class IdEraserDTOFilter implements DTOFilter {

        @Override
        public void filter(final Context context, final DTO dto) throws KasperQueryException {
            if (HasAnIdDTO.class.isAssignableFrom(dto)) {
                ((HasAnIdDTO) dto).id = "";
            }
        }

    }

Global filters will be applied after user-defined filters, and user-defined filters are applied in the order of their definition within the annotation.

A non-global filter can then be associated to one or several services using the @XKasperQueryService annotation,
filling the 'filters' field.


**GetThingsQueryService.classs** :

::

    @XKasperQueryService( ... , filters = ValidateIdQueryFilter.class )
    public class GetThingsQueryService implements QueryService<GetThingsQuery, ThingsListDTO> {

        @Override
        public ThingsListDTO retrieve(final GetThingsQuery query) throws KasperQueryException {
            ...
        }

    }


