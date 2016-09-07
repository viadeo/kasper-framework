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
package com.viadeo.kasper.domain.sample.hello.command.handler;

import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.command.AutowiredEntityCommandHandler;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.domain.sample.hello.api.command.SendHelloToBuddyCommand;
import com.viadeo.kasper.domain.sample.hello.command.entity.Hello;
import com.viadeo.kasper.domain.sample.hello.command.repository.HelloRepository;

import javax.inject.Inject;

@XKasperCommandHandler( /* Required annotation to define the sticked domain */
        domain = HelloDomain.class,
        description = "Submit a new Hello message to a specified buddy"
)
public class SendHelloToBuddyCommandHandler extends AutowiredEntityCommandHandler<SendHelloToBuddyCommand, Hello> {

    private HelloRepository repository;

    @Inject
    public SendHelloToBuddyCommandHandler(HelloRepository repository) {
        this.repository = repository;
    }

    @Override
    public CommandResponse handle(final SendHelloToBuddyCommand command) {

        /**
         * Just check for aggregate presence
         */
        if (repository.has(command.getIdToUse())) {
            return CommandResponse.error(
                CoreReasonCode.CONFLICT,
                "An hello message already exists for this id"
            );
        }

        /**
         * Creates the new aggregate
         */
        final Hello newHello = new Hello(
            command.getIdToUse(),
            command.getMessage(),
            command.getForBuddy()
        );

        /**
         * Checks whether this buddy has not already sent the same hello message
         * using the dedicated business method from the repository
         */
        if ( repository.hasTheSameMessage(newHello)) {
            return CommandResponse.error(
                CoreReasonCode.CONFLICT,
                "This buddy already sent the same hello message"
            );
        }

        /**
         * Add the new aggregate to its repository
         */
        repository.add(newHello);

        return CommandResponse.ok();
    }

}
