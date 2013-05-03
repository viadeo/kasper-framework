// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.web;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Optional;
import com.sun.jersey.api.core.InjectParam;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.doc.nodes.AbstractDocumentedDomainNode;
import com.viadeo.kasper.doc.nodes.DocumentedDomain;
import com.viadeo.kasper.doc.nodes.RetMap;
import com.viadeo.kasper.doc.nodes.RetUnexistent;

@Path("/doc")
@Configurable
public class KasperDocResource {

	private static final String DEFAULT_UNSPECIFIED = "unspecified";
	
	@InjectParam
	private KasperLibrary kasperLibrary;
	
	// ------------------------------------------------------------------------
	
	@GET
	@Path(DocumentedDomain.PLURAL_TYPE_NAME)
	@Produces(MediaType.APPLICATION_JSON)
	public RetMap getDomains() {
		return new RetMap(getKasperLibrary().getDomains());
	}
	
	/*
	 * Return Object so json provider implementation will look after the runtime type of the
	 * the object, otherwise it would only serialize using fields from returned type.
	 */
	@GET
	@Path(DocumentedDomain.TYPE_NAME + "/{domainName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Object getDomain(@PathParam("domainName") final String domainName) {
		String retDomainName = domainName;
		if (null != domainName) {
			final Optional<DocumentedDomain> domain = getKasperLibrary().getDomainFromName(domainName);
			if (domain.isPresent()) {
				return domain.get();
			}
		} else {
			retDomainName = DEFAULT_UNSPECIFIED;
		}		
		return new RetUnexistent(DocumentedDomain.TYPE_NAME, retDomainName);
	}
	
	// ------------------------------------------------------------------------
	
	@GET
	@Path(DocumentedDomain.TYPE_NAME + "/{domainName}/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public Object getEntities(@PathParam("domainName") final String domainName, @PathParam("type") final String type) {
		String retDomainName = domainName;
		if (null != domainName) {
			final Optional<Map<String, AbstractDocumentedDomainNode>> entities = getKasperLibrary().getEntities(domainName, type);
			if (entities.isPresent()) {
				return new RetMap(entities.get());
			}
			return new RetUnexistent("type", type);
		} else {
			retDomainName = DEFAULT_UNSPECIFIED;
		}		
		return new RetUnexistent(DocumentedDomain.TYPE_NAME, retDomainName);
	}		
	
	// ------------------------------------------------------------------------
	
	@GET
	@Path(DocumentedDomain.TYPE_NAME + "/{domainName}/{type}/{entityName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Object getEntity(@PathParam("domainName") final String domainName, @PathParam("type") final String type, @PathParam("entityName") final String entityName) {
		String retDomainName = domainName;
		if (null != domainName) {
			if (null != entityName) {
				final Optional<AbstractDocumentedDomainNode> entity = getKasperLibrary().getEntity(domainName, type, entityName);
				if (entity.isPresent()) {
					return entity.get();
				}
			}
			return new RetUnexistent(type, entityName);
		} else {
			retDomainName = DEFAULT_UNSPECIFIED;
		}		
		return new RetUnexistent(DocumentedDomain.TYPE_NAME, retDomainName);
	}	
	
	// ------------------------------------------------------------------------
	
	public void setKasperLibrary(final KasperLibrary kasperLibrary) {
		this.kasperLibrary = kasperLibrary;
	}
	
	public KasperLibrary getKasperLibrary() {
		return this.kasperLibrary;
	}
	
}
