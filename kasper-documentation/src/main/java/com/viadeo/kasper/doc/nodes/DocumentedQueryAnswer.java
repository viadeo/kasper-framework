// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.doc.nodes;

import com.viadeo.kasper.cqrs.query.QueryAnswer;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryAnswer;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.exception.KasperException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DocumentedQueryAnswer extends DocumentedDomainNode{

    private static final long serialVersionUID = -5368242490125523480L;

    public static final String TYPE_NAME="queryAnswer";
    public static final String PLURAL_TYPE_NAME="queryAnswers";

    private DocumentedProperties properties=null;

    // ----------------------------------------------------------------------------------------

    DocumentedQueryAnswer(final KasperLibrary kl){
        super(kl,TYPE_NAME,PLURAL_TYPE_NAME); // Used as empty queryAnswer to populate
    }

    public DocumentedQueryAnswer(KasperLibrary kl, final Class<? extends QueryAnswer> queryAnswerClazz){
        super(kl,TYPE_NAME,PLURAL_TYPE_NAME);

        final XKasperQueryAnswer annotation=queryAnswerClazz.getAnnotation(XKasperQueryAnswer.class);

        // Get description -----------------------------------------------------------
        String description="";
        if (null!=annotation){
            description=annotation.description();
        }
        if (description.isEmpty()){
            description=String.format("The %s queryAnswer",
                    queryAnswerClazz.getSimpleName().replaceAll("QueryAnswer", ""));
        }

        this.setName(queryAnswerClazz.getSimpleName());
        this.setDescription(description);
        this.properties=new DocumentedProperties(queryAnswerClazz);
    }

    // -------------------------------------------------------------------------------
    public String getLabel(){
        if (null== this.label){
            return this.getName().replaceAll("QueryAnswer","");
        }
        return super.getLabel();
    }

    // -------------------------------------------------------------------------------

    public Collection<DocumentedNode> getQueryServices() {
        final KasperLibrary kl = this.getKasperLibrary();
        return kl.simpleNodesFrom( kl.getQueryServicesForQueryAnswer(getName()) ).values();
    }

    // -------------------------------------------------------------------------------
    public DocumentedProperties getProperties(){
        return this.properties;
    }

    // -------------------------------------------------------------------------------
    public DocumentedNode getDomain(){
        final List<DocumentedQueryService> queryServices=
                this.getKasperLibrary().getQueryServicesForQueryAnswer(this.getName());

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
