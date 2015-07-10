// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.viadeo.kasper.api.domain.command.Command;
import com.viadeo.kasper.api.domain.command.CommandResponse;

public abstract class CommandInterceptorFactory implements InterceptorFactory<Command, CommandResponse> {

}
