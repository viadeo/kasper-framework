// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.core.boot;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.boot.SpringComponentsInstanceManager;
import com.viadeo.kasper.exception.KasperException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.GenericApplicationContext;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SpringComponentsInstanceManagerTest {

    private SpringComponentsInstanceManager sman;
    private GenericApplicationContext ctx;

    // ------------------------------------------------------------------------

    @Before
    public void setUp() {
        this.sman = new SpringComponentsInstanceManager();
        this.ctx = new GenericApplicationContext();
        this.sman.setApplicationContext(this.ctx);
    }

    private void registerSingletonBeanFromClass(final Class<?> clazz) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(clazz)
                .setScope(BeanDefinition.SCOPE_SINGLETON);
        ctx.registerBeanDefinition("string", builder.getBeanDefinition());
    }

    // ------------------------------------------------------------------------

    @Test
    public void existingBeanShouldBeFound() {
        // Given
        registerSingletonBeanFromClass(String.class);
        final String stringBean = ctx.getBean(String.class);

        // When
        final Optional<String> optStringBean = sman.getInstanceFromClass(String.class);

        // Then
        assertTrue(optStringBean.isPresent());
        assertTrue(optStringBean.get() == stringBean);
    }

    @Test
    public void unexistentBeanShouldBeCreatedAndFound() {
        // When
        final Optional<String> optStringBean = sman.getInstanceFromClass(String.class);

        // Then
        assertTrue(optStringBean.isPresent());
    }

    @Test
    public void unexistentBeanShouldRaiseExceptionWithFlag() {
        // Given
        sman.setBeansMustExists(true);

        try {
            // When
            final Optional<String> optStringBean = sman.getInstanceFromClass(String.class);
            // Then should not
            fail();
        } catch (final KasperException e) {
            // Then OK
        }

    }

    @Test
    public void attemptToRecordAnExistingBeanShouldFail() {
        // Given
        registerSingletonBeanFromClass(String.class);
        final String stringBean = ctx.getBean(String.class);

        try {
            // When
            sman.recordInstance(String.class, "test");
            // Then should not
            fail();
        } catch (final KasperException e) {
            // Then OK
        }
    }

    @Test
    public void registerUnregisteredBeanShouldBeOk() {
        // Given
        final String strInstance = "test";

        // When
        sman.recordInstance(String.class, strInstance);
        final Optional<String> optStr =sman.getInstanceFromClass(String.class);

        // Then
        assertTrue(optStr.isPresent());
        assertTrue(optStr.get() == strInstance);
    }

}
