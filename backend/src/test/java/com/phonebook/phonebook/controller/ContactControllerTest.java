
package com.phonebook.phonebook.controller;

import com.phonebook.phonebook.dto.ContactResponse;
import com.phonebook.phonebook.dto.PagedContactsResponse;
import com.phonebook.phonebook.model.ContactCategory;
import com.phonebook.phonebook.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactService service;

    @Test
    void returnsPagedContacts() throws Exception {
        ContactResponse response =
            new ContactResponse(
                1,
                "Sohan",
                "+919876543210",
                "sohan@example.com",
                "Mumbai",
                ContactCategory.FAMILY,
                OffsetDateTime.now()
            );

        when(service.getContacts(
            anyString(),
            eq(ContactCategory.FAMILY),
            eq(1),
            eq(3)
        )).thenReturn(
            new PagedContactsResponse(
                List.of(response),
                1,
                1,
                3,
                1,
                "FAMILY"
            )
        );

        mockMvc.perform(
                get("/contacts")
                    .param("search", "sohan")
                    .param("category", "FAMILY")
                    .param("page", "1")
                    .param("pageSize", "3")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                "$.items[0].name",
                is("Sohan")
            ))
            .andExpect(jsonPath(
                "$.items[0].category",
                is("FAMILY")
            ))
            .andExpect(jsonPath(
                "$.total",
                is(1)
            ));
    }
}

