package com.nineleaps.leaps.model.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
@Tag("unit_tests")
@DisplayName("ProductUrl Tests")
class ProductUrlTest {

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        // Create a ProductUrl instance
        ProductUrl productUrl = new ProductUrl();

        // Set the values using setters
        productUrl.setId(1L);
        productUrl.setUrl("https://example.com/image.jpg");

        // Create a Product instance
        Product product = new Product();
        product.setId(10L);

        // Set the product for the ProductUrl
        productUrl.setProduct(product);

        // Test the getters
        assertEquals(1L, productUrl.getId());
        assertEquals("https://example.com/image.jpg", productUrl.getUrl());
        assertEquals(product, productUrl.getProduct());
    }



}