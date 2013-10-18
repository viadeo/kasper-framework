// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.doc.nodes;

import com.viadeo.kasper.cqrs.query.QueryAnswer;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.core.resolvers.QueryAnswerResolver;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DocumentedQueryAnswer extends DocumentedDomainNode {

    private static final long serialVersionUID = -5368242490125523480L;

    public static final String TYPE_NAME="queryAnswer";
    public static final String PLURAL_TYPE_NAME="queryAnswers";

    private DocumentedBean properties;

    DocumentedQueryAnswer(final KasperLibrary kl){
        super(kl,TYPE_NAME,PLURAL_TYPE_NAME);
    }

    public DocumentedQueryAnswer(KasperLibrary kl, final Class<? extends QueryAnswer> queryAnswerClazz){
        this(kl);
        
        final QueryAnswerResolver resolver = this.getKasperLibrary().getResolverFactory().getQueryAnswerResolver();

        setName(queryAnswerClazz.getSimpleName());
        setLabel(resolver.getLabel(queryAnswerClazz));
        setDescription(resolver.getDescription(queryAnswerClazz));
        properties = new DocumentedBean(queryAnswerClazz);
    }

    public Collection<DocumentedNode> getQueryServices(){
        final KasperLibrary kl=this.getKasperLibrary();
        return kl.simpleNodesFrom( kl.getQueryServicesForQueryAnswer(getName()) ).values();
    }

    public DocumentedBean getProperties(){
        return this.properties;
    }

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
