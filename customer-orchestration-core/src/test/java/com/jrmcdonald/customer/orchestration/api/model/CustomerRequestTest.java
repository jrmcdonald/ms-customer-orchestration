package com.jrmcdonald.customer.orchestration.api.model;

import org.force66.beantester.BeanTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerRequestTest {

    @Test
    @DisplayName("Should construct a valid bean")
    void shouldConstructAValidBean() {
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(CustomerRequest.class, new Object[]{"first", "last"});
    }

}