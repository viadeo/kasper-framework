// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public abstract class RetBase implements Serializable {
	private static final long serialVersionUID = 1864387214923151069L;

    private static final Logger LOGGER = LoggerFactory.getLogger(RetBase.class);

	private static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
		mapper.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
		mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
		mapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
		mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
	}

	private final String type;

	// ------------------------------------------------------------------------

	protected RetBase(final String type) {
		this.type = type;
	}

	// ------------------------------------------------------------------------

	public String getType() {
		return type;
	}

	/**
	 * Do not use it in jax-rs resources, instead return the object as is and
	 * let jersey serialize it to json.
	 * 
	 * @return the json representation of this object
	 */
	@Deprecated
	public String toJson() {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {

			mapper.writeValue(os, this);

		} catch (final JsonGenerationException e) {
            LOGGER.error("", e);
			return "";
		} catch (final JsonMappingException e) {
            LOGGER.error("", e);
			return "";
		} catch (final IOException e) {
            LOGGER.error("", e);
			return "";
		}

		try {
			return os.toString(Charsets.UTF_8.name());
		} catch (final UnsupportedEncodingException e) {
            LOGGER.error("", e);
			return "";
		}
	}

}
