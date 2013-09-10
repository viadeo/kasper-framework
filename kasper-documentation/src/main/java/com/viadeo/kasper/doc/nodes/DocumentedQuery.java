// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;
import com.viadeo.kasper.doc.KasperLibrary;

public final class DocumentedQuery extends DocumentedDomainNode{

    private static final long serialVersionUID = -4581629164926662306L;

    public static final String TYPE_NAME="query";
    public static final String PLURAL_TYPE_NAME="queries";

    private DocumentedBean properties = null;

    // ----------------------------------------------------------------------------------

    DocumentedQuery(final KasperLibrary kl){
        super(kl,TYPE_NAME,PLURAL_TYPE_NAME);  // Used as empty query to populate
    }

    public DocumentedQuery(KasperLibrary kl, final Class<? extends Query> queryClazz) {
        super(kl,TYPE_NAME,PLURAL_TYPE_NAME);

        final XKasperQuery annotation=queryClazz.getAnnotation(XKasperQuery.class);

        // Get description --------------------------------------------------
        String description="";
        if (null!=annotation){
            description=annotation.description();
        }
        if (description.isEmpty()){
            description=String.format("The %s query",
                    queryClazz.getSimpleName().replaceAll("Query", ""));
        }

        // - Register the domain to the locator -------------------------
        this.setName(queryClazz.getSimpleName());
        this.setDescription(description);
        this.properties=new DocumentedBean(queryClazz);
    }

    // ----------------------------------------------------------------------

    public String getLabel(){
        if (null == this.label){
            return this.getName().replaceAll("Query", "");
        }
        return super.getLabel();
    }

    // ----------------------------------------------------------------------

    public DocumentedNode getQueryService(){
        final KasperLibrary kl=this.getKasperLibrary();
        final Optional<DocumentedQueryService> queryService=kl.getQueryServiceForQuery(getName());

        if (queryService.isPresent()){
            return kl.getSimpleNodeFrom(queryService.get());
        }

        return null;
    }

    // ----------------------------------------------------------------------

    public DocumentedBean getProperties(){
        return this.properties;
    }

    // ----------------------------------------------------------------------

    public DocumentedNode getDomain(){
        final Optional<DocumentedQueryService> queryService=
                this.getKasperLibrary().getQueryServiceForQuery(this.getName());

        if (queryService.isPresent()){
            return new DocumentedNode(queryService.get().getDomain());
        }
        return null;
    }

}