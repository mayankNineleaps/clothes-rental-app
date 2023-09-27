package com.nineleaps.leaps.model;

import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.enums.AddressType;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class AddressTest {

    @Test
    void addressConstructorWithDtoAndUser() {
        // Create a mock User
        User user = mock(User.class);

        AddressDto addressDto = new AddressDto();
        addressDto.setId(1L);
        addressDto.setAddressType(AddressType.OFFICE);
        addressDto.setAddressLine1("123 Street");
        addressDto.setAddressLine2("Apt 101");
        addressDto.setCity("City");
        addressDto.setState("State");
        addressDto.setPostalCode("12345");
        addressDto.setCountry("Country");
        addressDto.setDefaultAddress(true);

        Address address = new Address(addressDto, user);

        // Assert that the fields are set correctly
        assertEquals(addressDto.getId(), address.getId());
        assertEquals(addressDto.getAddressType(), address.getAddressType());
        assertEquals(addressDto.getAddressLine1(), address.getAddressLine1());
        assertEquals(addressDto.getAddressLine2(), address.getAddressLine2());
        assertEquals(addressDto.getCity(), address.getCity());
        assertEquals(addressDto.getState(), address.getState());
        assertEquals(addressDto.getPostalCode(), address.getPostalCode());
        assertEquals(addressDto.getCountry(), address.getCountry());
        assertEquals(addressDto.isDefaultAddress(), address.isDefaultAddress());
        assertEquals(user, address.getUser());
    }

    @Test
    void setAuditColumnsCreate() {
        // Create a mock User
        User user = mock(User.class);
        user.setId(1L);
        LocalDateTime createdAt = LocalDateTime.now();



        Address address = new Address();
        address.setAuditColumnsCreate(user);
        address.setAuditColumnsUpdate(user.getId());

        // Assert that the audit columns are set correctly
        assertEquals(user.getId(), address.getAddressCreatedBy());

    }

    @Test
    void setAuditColumnsUpdate() {
        Long userId = 1L;
        LocalDateTime updatedAt = LocalDateTime.now();

        Address address = new Address();
        address.setAuditColumnsUpdate(userId);

        // Assert that the audit columns are set correctly
        assertEquals(updatedAt.toLocalDate(), address.getAddressUpdatedAt().toLocalDate());
        assertEquals(userId, address.getAddressUpdatedBy());
    }
}
