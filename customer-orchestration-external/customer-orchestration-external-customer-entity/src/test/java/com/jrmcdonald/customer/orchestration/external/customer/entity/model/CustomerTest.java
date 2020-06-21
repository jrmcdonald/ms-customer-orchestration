package com.jrmcdonald.customer.orchestration.external.customer.entity.model;

import org.force66.beantester.BeanTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class CustomerTest {

    @Test
    @DisplayName("Should construct a valid bean")
    void shouldConstructAValidBean() {
        BeanTester beanTester = new BeanTester();
        beanTester.addTestValues(Instant.class, new Object[]{Instant.now()});
        beanTester.testBean(Customer.class);
    }
}