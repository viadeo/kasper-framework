package com.viadeo.kasper.context.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class DefaultContextUTest {

    @Test
    public void getFirstIpAddress_withEmpty_shouldReturnAbsent() {

        // Given
        Context context = new DefaultContext();
        context.setIpAddress("");

        // When
        Optional<String> ipAddress = context.getFirstIpAddress();

        // Then
        assertFalse(ipAddress.isPresent());

    }

    @Test
    public void getFirstIpAddress_withOneIpAddress_shouldReturnIpAddress() {

        // Given
        Context context = new DefaultContext();
        String ipAddress = "127.0.0.1";
        context.setIpAddress(ipAddress);

        // When
        Optional<String> result = context.getFirstIpAddress();

        // Then
        assertEquals(ipAddress, result.get());
    }

    @Test
    public void getFirstIpAddress_withSeveralIpAddresses_shouldReturnFirstIpAddress() {

        // Given
        Context context = new DefaultContext();
        context.setIpAddress("90.48.246.60, 10.0.1.250, 10.0.7.94, 10.0.1.250");

        // When
        Optional<String> ipAddress = context.getFirstIpAddress();

        // Then
        assertEquals("90.48.246.60", ipAddress.get());

    }

    @Test
    public void getFirstIpAddress_withSeveralIpAddressesWithSpacesEverywhereHihihi_shouldReturnFirstIpAddress() {

        // Given
        Context context = new DefaultContext();
        context.setIpAddress("   90.48.246.60   , 10.0.1.250 ,   10.0.7.94, 10.0.1.250                ");

        // When
        Optional<String> ipAddress = context.getFirstIpAddress();

        // Then
        assertEquals("90.48.246.60", ipAddress.get());

    }
}