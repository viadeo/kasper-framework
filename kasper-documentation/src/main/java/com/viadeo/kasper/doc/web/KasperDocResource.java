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

@Path("/")
@Configurable
public class KasperDocResource {

	private static final String defaultUnspecified = "unspecified";
	
	@InjectParam
	private KasperLibrary kasperLibrary;
	
	// ------------------------------------------------------------------------
	
	@GET
	@Path(DocumentedDomain.PLURAL_TYPE_NAME)
	@Produces(MediaType.APPLICATION_JSON)
	public String getDomains() {
		return new RetMap(getKasperLibrary().getDomains()).toJson();
	}
	
	@GET
	@Path(DocumentedDomain.TYPE_NAME + "/{domainName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDomain(@PathParam("domainName") String domainName) {
		if (null != domainName) {
			final Optional<DocumentedDomain> domain = getKasperLibrary().getDomainFromName(domainName);
			if (domain.isPresent()) {
				return domain.get().toJson();
			}
		} else {
			domainName = defaultUnspecified;
		}		
		return new RetUnexistent(DocumentedDomain.TYPE_NAME, domainName).toJson();
	}
	
	// ------------------------------------------------------------------------
	
	@GET
	@Path(DocumentedDomain.TYPE_NAME + "/{domainName}/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEntities(@PathParam("domainName") String domainName, @PathParam("type") String type) {
		if (null != domainName) {
			final Optional<Map<String, AbstractDocumentedDomainNode>> entities = getKasperLibrary().getEntities(domainName, type);
			if (entities.isPresent()) {
				return new RetMap(entities.get()).toJson();
			}
			return new RetUnexistent("type", type).toJson();
		} else {
			domainName = defaultUnspecified;
		}		
		return new RetUnexistent(DocumentedDomain.TYPE_NAME, domainName).toJson();
	}		
	
	// ------------------------------------------------------------------------
	
	@GET
	@Path(DocumentedDomain.TYPE_NAME + "/{domainName}/{type}/{entityName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEntity(final @PathParam("domainName") String domainName, final @PathParam("type") String type, final @PathParam("entityName") String entityName) {
		String retDomainName = domainName;
		if (null != domainName) {
			if (null != entityName) {
				final Optional<AbstractDocumentedDomainNode> entity = getKasperLibrary().getEntity(domainName, type, entityName);
				if (entity.isPresent()) {
					return entity.get().toJson();
				}
			}
			return new RetUnexistent(type, entityName).toJson();
		} else {
			retDomainName = defaultUnspecified;
		}		
		return new RetUnexistent(DocumentedDomain.TYPE_NAME, retDomainName).toJson();
	}	
	
	// ------------------------------------------------------------------------
	
	public void setKasperLibrary(final KasperLibrary kasperLibrary) {
		this.kasperLibrary = kasperLibrary;
	}
	
	public KasperLibrary getKasperLibrary() {
		return this.kasperLibrary;
	}
	
}
