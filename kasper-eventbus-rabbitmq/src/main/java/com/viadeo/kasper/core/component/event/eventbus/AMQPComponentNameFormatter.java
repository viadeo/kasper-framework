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
package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.viadeo.kasper.core.component.event.listener.EventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public class AMQPComponentNameFormatter {

    protected static final String DEAD_LETTER_SUFFIX = "_dead-letter";

    private String exchangeNameFormat = "%exchange%-%exchange-version%";
    private String deadLetterExchangeNameFormat = "%exchange%-%exchange-version%" + DEAD_LETTER_SUFFIX;
    private String queueNameFormat = "%exchange%_%cluster%_%listener%";
    private String deadLetterQueueNameFormat = "%queue%" + DEAD_LETTER_SUFFIX;

    private Pattern queueNamePattern = Pattern.compile("([^_]+)_([^_]+)_(.+)");
    private Pattern deadLetterqueueNamePattern = Pattern.compile("([^_]+)_([^_]+)_(.+)" + DEAD_LETTER_SUFFIX);

    public String getQueueName(String exchangeName, String clusterName, EventListener eventListener) {
        return this.getQueueName(exchangeName, clusterName, eventListener.getName());
    }

    @VisibleForTesting
    protected String getQueueName(String exchangeName, String clusterName, String eventListenerClassName) {
        return queueNameFormat
                .replace("%exchange%", exchangeName)
                .replace("%cluster%", clusterName)
                .replace("%listener%", eventListenerClassName);
    }

    public Optional<QueueInfo> extractQueueInfo(String queueName) {
        checkNotNull(queueName);

        Matcher matcher = deadLetterqueueNamePattern.matcher(queueName);

        if ( ! matcher.find()) {
            matcher = queueNamePattern.matcher(queueName);

            if ( ! matcher.find()) {
                return Optional.absent();
            }
        }

        return Optional.of(
                new QueueInfo(
                        queueName,
                        matcher.group(1),
                        matcher.group(3),
                        queueName.endsWith(DEAD_LETTER_SUFFIX) ? Boolean.TRUE : Boolean.FALSE
                )
        );
    }

    public String getDeadLetterQueueName(String exchangeName, String clusterName, EventListener eventListener) {
        return deadLetterQueueNameFormat
                .replace("%queue%", getQueueName(exchangeName, clusterName, eventListener.getName()));
    }

    public String getFallbackDeadLetterQueueName(String exchangeName, String exchangeVersion, String clusterName) {
        return exchangeName + "-" + exchangeVersion + "_" + clusterName + DEAD_LETTER_SUFFIX;
    }

    public String getFullExchangeName(String name, String version) {
        return exchangeNameFormat.replace("%exchange%", name).replace("%exchange-version%", version);
    }

    public String getDeadLetterExchangeName(String name, String version) {
        return deadLetterExchangeNameFormat.replace("%exchange%", name).replace("%exchange-version%", version);
    }

    /**
     * Set the name format used to create the queues
     * this format accept 3 placeholders :
     * - %cluster%
     * - %exchange%
     * - %listener%
     *
     * @param queueNameFormat the queue name format
     */
    public void setQueueNameFormat(String queueNameFormat) {
        this.queueNameFormat = checkNotNull(queueNameFormat);
        String regex = queueNameFormat
                .replace("%exchange%", "([^_]+)")
                .replace("%cluster%", "([^_]+)")
                .replace("%listener%", "(.+)");

        this.queueNamePattern = Pattern.compile(regex);
        this.deadLetterqueueNamePattern = Pattern.compile(regex + DEAD_LETTER_SUFFIX);
    }

    /**
     * Set the dead letter exchange name format
     * this format accept 2 placeholder :
     *  - %exchange%
     *  - %exchange-version%
     *
     * @param deadLetterExchangeNameFormat the dead-letter exchange name format
     */
    public void setDeadLetterExchangeNameFormat(String deadLetterExchangeNameFormat) {
        this.deadLetterExchangeNameFormat = checkNotNull(deadLetterExchangeNameFormat);
    }

    /**
     * Set the dead letter queue name format
     * this format accept 1 placeholder :
     * - %queue%
     *
     * @param deadLetterQueueNameFormat the dead-letter queue name format
     */
    public void setDeadLetterQueueNameFormat(String deadLetterQueueNameFormat) {
        this.deadLetterQueueNameFormat = checkNotNull(deadLetterQueueNameFormat);
    }

    /**
     * Set the exchange name format
     * this format accept 2 placeholder :
     *  - %exchange%
     *  - %exchange-version%
     * @param exchangeNameFormat the exchange name format
     */
    public void setExchangeNameFormat(String exchangeNameFormat) {
        this.exchangeNameFormat = checkNotNull(exchangeNameFormat);
    }
}
