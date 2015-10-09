// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.root.command.handler;

import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.domain.sample.root.api.Facebook;



@XKasperCommandHandler(domain=Facebook.class)
@Deprecated
@SuppressWarnings("deprecation")
public class AddConnectionToGroupHandler extends AutowiredCommandHandler<com.viadeo.kasper.domain.sample.root.api.command.AddConnectionToGroupCommand> {
}
