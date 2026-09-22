
package com.phonebook.phonebook.service;

import com.phonebook.phonebook.dto.ContactRequest;
import com.phonebook.phonebook.dto.ContactResponse;
import com.phonebook.phonebook.model.Contact;
import com.phonebook.phonebook.model.ContactCategory;
import com.phonebook.phonebook.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository repository;

    @InjectMocks
    private ContactService service;

    @Test
    void createsContactWithCategory() {
        ContactRequest request =
            new ContactRequest(
                "Sohan Kumar",
                "+919876543210",
                "sohan@example.com",
                "Mumbai, India",
                ContactCategory.WORK
            );

        when(repository.findByPhoneNumber(any()))
            .thenReturn(Optional.empty());

        when(repository.findByEmail(any()))
            .thenReturn(Optional.empty());

        Contact saved = new Contact();
        saved.setId(1);
        saved.setName("Sohan Kumar");
        saved.setPhoneNumber("+919876543210");
        saved.setEmail("sohan@example.com");
        saved.setAddress("Mumbai, India");
        saved.setCategory(ContactCategory.WORK);

        when(repository.save(any(Contact.class)))
            .thenReturn(saved);

        ContactResponse response =
            service.createContact(request);

        assertEquals(
            "Sohan Kumar",
            response.name()
        );

        assertEquals(
            ContactCategory.WORK,
            response.category()
        );

        verify(repository)
            .save(any(Contact.class));
    }
}

