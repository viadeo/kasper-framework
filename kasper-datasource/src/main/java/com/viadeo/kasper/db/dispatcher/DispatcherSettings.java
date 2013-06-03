// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.db.dispatcher;

import java.util.LinkedHashSet;


public final class DispatcherSettings {

	private Dsn balancedDsn;
	private Dsn defaultDsn;
	private LinkedHashSet<Dsn> dsns;

	private Dsn networkDsn;

    // ------------------------------------------------------------------------

	/**
	 * Gets the balanced dsn.
	 * 
	 * @return the balanced dsn
	 */
	public Dsn getBalancedDsn() {
		return balancedDsn;
	}

	/**
	 * Gets the default dsn.
	 * 
	 * @return the default dsn
	 */
	public Dsn getDefaultDsn() {
		return defaultDsn;
	}

	/**
	 * Gets the dsns.
	 * 
	 * @return the dsns
	 */
	public LinkedHashSet<Dsn> getDsns() {
		return dsns;
	}

	/**
	 * Gets the network dsn.
	 * 
	 * @return the network dsn
	 */
	public Dsn getNetworkDsn() {
		return networkDsn;
	}

	/**
	 * Sets the balanced dsn.
	 * 
	 * @param balancedDsn the new balanced dsn
	 */
	public void setBalancedDsn(final Dsn balancedDsn) {
		this.balancedDsn = balancedDsn;
	}

    // ------------------------------------------------------------------------

	/**
	 * Sets the default dsn.
	 * 
	 * @param defaultDsn the new default dsn
	 */
	public void setDefaultDsn(final Dsn defaultDsn) {
		this.defaultDsn = defaultDsn;
	}

	/**
	 * Sets the dsns.
	 * 
	 * @param dsns the new dsns
	 */
    public void setDsns(final LinkedHashSet<Dsn> dsns) {
		this.dsns = dsns;
	}

	/**
	 * Sets the network dsn.
	 * 
	 * @param networkDsn the new network dsn
	 */
	public void setNetworkDsn(final Dsn networkDsn) {
		this.networkDsn = networkDsn;
	}

}
