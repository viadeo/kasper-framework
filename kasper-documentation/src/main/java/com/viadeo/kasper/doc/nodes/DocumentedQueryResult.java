// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.doc.nodes;

import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.exception.KasperException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DocumentedQueryResult extends DocumentedDomainNode{

    private static final long serialVersionUID = -5368242490125523480L;

    public static final String TYPE_NAME="queryResult";
    public static final String PLURAL_TYPE_NAME="queryResults";

    private DocumentedBean properties=null;

    // ----------------------------------------------------------------------------------------

    DocumentedQueryResult(final KasperLibrary kl){
        super(kl,TYPE_NAME,PLURAL_TYPE_NAME); // Used as empty queryResult to populate
    }

    public DocumentedQueryResult(KasperLibrary kl, final Class<? extends QueryResult> queryResultClazz){
        super(kl,TYPE_NAME,PLURAL_TYPE_NAME);

        final XKasperQueryResult annotation=queryResultClazz.getAnnotation(XKasperQueryResult.class);

        // Get description -----------------------------------------------------------
        String description="";
        if (null!=annotation){
            description=annotation.description();
        }
        if (description.isEmpty()){
            description=String.format("The %s queryResult",
                    queryResultClazz.getSimpleName().replaceAll("QueryResult", ""));
        }

        this.setName(queryResultClazz.getSimpleName());
        this.setDescription(description);
        this.properties=new DocumentedBean(queryResultClazz);
    }

    // -------------------------------------------------------------------------------
    public String getLabel(){
        if (null== this.label){
            return this.getName().replaceAll("QueryResult","");
        }
        return super.getLabel();
    }

    // -------------------------------------------------------------------------------

    public Collection<DocumentedNode> getQueryHandlers() {
        final KasperLibrary kl = this.getKasperLibrary();
        return kl.simpleNodesFrom( kl.getQueryHandlersForQueryResult(getName()) ).values();
    }

    // -------------------------------------------------------------------------------
    public DocumentedBean getProperties(){
        return this.properties;
    }

    // -------------------------------------------------------------------------------
    public DocumentedNode getDomain(){
        final List<DocumentedQueryHandler> queryServices=
                this.getKasperLibrary().getQueryHandlersForQueryResult(this.getName());

        final Set<DocumentedNode> domains = new HashSet<>();
        for (final DocumentedQueryHandler queryService : queryServices){
            boolean addInList = true;
            for (final DocumentedNode domain : domains) {
                if (domain.getName().contentEquals(queryService.getDomain().getName())) {
                    addInList = false;
                }
            }
            if (addInList) {
                domains.add(queryService.getDomain());
            }
        }
        if (!domains.isEmpty()){
            if (1== domains.size()){
                return domains.iterator().next();
            } else {
                throw new KasperException("More than one domain found");
            }
        }
        return null;
    }
}
