package com.jrmcdonald.customer.orchestration.api.model;

import org.force66.beantester.BeanTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class CustomerResponseTest {

    @Test
    @DisplayName("Should construct a valid bean")
    void shouldConstructAValidBean() {
        BeanTester beanTester = new BeanTester();
        beanTester.addTestValues(Instant.class, new Object[]{Instant.now()});
        beanTester.testBean(CustomerResponse.class, new Object[]{"customer-id-123", "first", "last", Instant.now()});
    }
}