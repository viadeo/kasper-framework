/*
 * Copyright 2010 Viadeo.com
 */

package com.viadeo.kasper.db.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Dsn {

	public final static Logger LOGGER = LoggerFactory.getLogger(Dsn.class);

	private boolean alwaysReadOn = false;
    private String read;
    private String write;
	private String tableName;
    private String className;

	/**
	 * Instantiates a new dsn.
	 */
	public Dsn() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 * 
	 * The key must be only the regular expression or the table name regular expression. 
	 * The read and write Datasource must not be part of the key
	 * to avoid having more than one entry for the same regular expression (table name or class name)
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// take string value of regular expression. the compiled version of variable is different
		result = prime * result;
		// Take the string (upper case) version of the variable, not the compiled regular expression
		result = prime * result + ((tableName == null) ? 0 : tableName.toString().hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 * The key must be only the regular expression or the table name regular expression. 
	 * The read and write Datasource must not be part of the key
	 * to avoid having more than one entry for the same regular expression (table name or class name)
	 * So the equals method must take into account only regexpr (table name or class name)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Dsn other = (Dsn) obj;

		if (tableName == null) {
			if (other.tableName != null) {
				return false;
			}
		} else if (tableName.toString().equals(other.tableName.toString())) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Found existing element in configuration " + other.tableName.toString());
			}
			return true;
		} else {
			return false;
		}

		return true;
	}

    public String getRead() {
        return read;
    }

    public String getWrite() {
        return write;
    }


	/**
	 * Sets the always read on.
	 * 
	 * @param alwaysOnRead the new always read on
	 */
	public void setAlwaysReadOn(final boolean alwaysOnRead) {
		alwaysReadOn = alwaysOnRead;
	}

	/**
	 * Sets the class name.
	 * 
	 * @param className the new class name
	 */
	public void setClassName(final String className) {
        this.className = className;
        tableName = className;
	}

	/**
	 * Sets the class name.
	 * 
	 * @param tableName the new class name
	 */
	public void setTableName(final String tableName) {
        this.tableName = tableName;
	}

    public String getTableName() {
        return tableName;
    }

    /**
	 * Sets the dsn.
	 * 
	 * @param dsn the new dsn
	 */
	public void setDsn(final String dsn) {
		read = write = dsn;
	}

	/**
	 * Sets the read.
	 * 
	 * @param dsn the new read
	 */
	public void setRead(final String dsn) {
		read = dsn;
	}


	/**
	 * Sets the write.
	 * 
	 * @param dsn the new write
	 */
	public void setWrite(final String dsn) {
		write = dsn;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("dsn:{read=").append(read).append(", write=").append(write);
		if (tableName != null) {
			sb.append(", tableName:").append(tableName);
		}
        if (className != null) {
            sb.append(", className:").append(className);
        }
		if (alwaysReadOn) {
			sb.append(", alwaysReadOn:true");
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Gets the always read on.
	 * 
	 * @return the always read on
	 */
	Boolean getAlwaysReadOn() {
		return alwaysReadOn;
	}

}
