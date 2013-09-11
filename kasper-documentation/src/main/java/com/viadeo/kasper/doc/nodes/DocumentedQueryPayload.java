// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryPayload;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.exception.KasperException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DocumentedQueryPayload  extends DocumentedDomainNode{

    private static final long serialVersionUID = -5368242490125523480L;

    public static final String TYPE_NAME="queryPayload";
    public static final String PLURAL_TYPE_NAME="queryPayloads";

    private DocumentedBean properties=null;

    // ----------------------------------------------------------------------------------------

    DocumentedQueryPayload(final KasperLibrary kl){
        super(kl,TYPE_NAME,PLURAL_TYPE_NAME); // Used as empty queryPayload to populate
    }

    public DocumentedQueryPayload(KasperLibrary kl, final Class<? extends QueryPayload> queryPayloadClazz){
        super(kl,TYPE_NAME,PLURAL_TYPE_NAME);

        final XKasperQueryPayload annotation=queryPayloadClazz.getAnnotation(XKasperQueryPayload.class);

        // Get description -----------------------------------------------------------
        String description="";
        if (null!=annotation){
            description=annotation.description();
        }
        if (description.isEmpty()){
            description=String.format("The %s queryPayload",
                    queryPayloadClazz.getSimpleName().replaceAll("QueryPayload", ""));
        }

        this.setName(queryPayloadClazz.getSimpleName());
        this.setDescription(description);
        this.properties=new DocumentedBean(queryPayloadClazz);
    }

    // -------------------------------------------------------------------------------
    public String getLabel(){
        if (null== this.label){
            return this.getName().replaceAll("QueryPayload","");
        }
        return super.getLabel();
    }

    // -------------------------------------------------------------------------------
    public Collection<DocumentedNode> getQueryServices(){
        final KasperLibrary kl=this.getKasperLibrary();
        return kl.simpleNodesFrom( kl.getQueryServicesForQueryPayload(getName()) ).values();
    }

    // -------------------------------------------------------------------------------
    public DocumentedBean getProperties(){
        return this.properties;
    }

    // -------------------------------------------------------------------------------
    public DocumentedNode getDomain(){
        final List<DocumentedQueryService> queryServices=
                this.getKasperLibrary().getQueryServicesForQueryPayload(this.getName());

        Set<DocumentedNode> domains=new HashSet<DocumentedNode>();
        for (DocumentedQueryService queryService:queryServices){
            domains.add(queryService.getDomain());
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
