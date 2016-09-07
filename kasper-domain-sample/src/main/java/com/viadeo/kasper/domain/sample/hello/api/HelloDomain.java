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
package com.viadeo.kasper.domain.sample.hello.api;

import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;

@XKasperDomain(
        label = "Hello",                /* Can be used to override the deducted label from class name */
        prefix = "hel",                 /* Mandatory, will be used for some ontology needs (data analysis) */
        description = "Hello domain",
        owner = "Loïc Dias Da Silva <ldiasdasilva@vieaoteam.com>"
)
public class HelloDomain implements Domain {

    // Validation constants ---------------------------------------------------
    public static final int MIN_BUDDY_LENGTH = 3;
    public static final int MAX_BUDDY_LENGTH = 48;
    public static final String REGEX_BUDDY = "[0-9a-zA-Z ]+";

    public static final int MIN_HELLO_LENGTH = 5;
    public static final int MAX_HELLO_LENGTH = 140;
    public static final String REGEX_HELLO = "[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)";

    // Validation messages ----------------------------------------------------
    public static final String NOT_PROVIDED_BUDDY_MSG = "the buddy must be provided";
    public static final String BAD_LENGTH_BUDDY_MSG = "the buddy must have a (3-48) chars length range";
    public static final String INVALID_BUDDY_MSG = "the buddy must be alphanum string";

    public static final String NOT_PROVIDED_HELLO_MSG = "the message must be provided";
    public static final String BAD_LENGTH_HELLO_MSG = "the message must have a (5-140) chars length range";
    public static final String INVALID_HELLO_MSG = "the message contains invalid characters";

}
