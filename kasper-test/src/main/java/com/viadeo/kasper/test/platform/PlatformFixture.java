// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.configuration.*;
import com.viadeo.kasper.test.exception.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class PlatformFixture implements IPlatformFixture {

    private Platform platform;
    private Context context;

    private GivenClause given;
    private WhenClause when;
    private ThenClause then;

    private boolean ensured = false;

    // ------------------------------------------------------------------------

    public PlatformFixture() {
        this(new PlatformFactory().getPlatform());
    }

    public PlatformFixture(final Platform platform) {
        this.platform = checkNotNull(platform);
        this.context = DefaultContextBuilder.get();
        CurrentContext.set(this.context);
    }

    public static PlatformFixture defaults() {
        return new PlatformFixture();
    }

    public static PlatformFixture from(final Platform platform) {
        return new PlatformFixture(platform);
    }

    // ------------------------------------------------------------------------

    public Context context() {
        return this.context;
    }

    // ------------------------------------------------------------------------

    public PlatformFixture scan(final String...packages) {
        for (final String pck : packages) {
            this.platform.getRootProcessor().addScanPrefix(pck);
        }
        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    public GivenClause given() {
        if (null == this.given) {
            this.given = new GivenClause(this);
        }
        return this.given;
    }

    @Override
    public WhenClause when() {
        if (null == this.when) {
            this.when = new WhenClause(this);
        }
        return this.when;
    }

    @Override
    public ThenClause then() {
        if (null == this.then) {
            this.then = new ThenClause(this);
        }
        return this.then;
    }

    // ------------------------------------------------------------------------

    public boolean isEnsured() {
        return this.ensured;
    }

    @Override
    public ThenClause ensure() {
        if ( ! this.platform.isBooted()) {
            this.platform.boot();
        }
        boolean given = true;
        if (null != this.given) {
            given = this.given.apply();
        }
        if (given) {
            if (when.apply()) {
                if (then.apply()) {
                    this.ensured = true;
                    return this.then;
                }
            }
        }
        throw new KasperTestException("Some errors detected during fixture application");
    }

}
