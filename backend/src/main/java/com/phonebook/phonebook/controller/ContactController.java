package com.phonebook.phonebook.controller;

import com.phonebook.phonebook.dto.ContactRequest;
import com.phonebook.phonebook.dto.ContactResponse;
import com.phonebook.phonebook.dto.PagedContactsResponse;
import com.phonebook.phonebook.model.ContactCategory;
import com.phonebook.phonebook.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService service;

    public ContactController(ContactService service) {
        this.service = service;
    }

    @GetMapping
    public PagedContactsResponse getContacts(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) ContactCategory category,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(name = "page_size", required = false) Integer pageSizeSnake,
        @RequestParam(name = "pageSize", required = false) Integer pageSizeCamel
    ) {
        if (page < 1) {
            throw new IllegalArgumentException("Page must be at least 1");
        }

        int pageSize = pageSizeSnake != null
            ? pageSizeSnake
            : (pageSizeCamel != null ? pageSizeCamel : 8);

        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException(
                "Page size must be between 1 and 100"
            );
        }

        return service.getContacts(
            search,
            category,
            page,
            pageSize
        );
    }

    @GetMapping("/{id}")
    public ContactResponse getContact(
        @PathVariable Integer id
    ) {
        return service.getContact(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse createContact(
        @Valid @RequestBody ContactRequest request
    ) {
        return service.createContact(request);
    }

    @PutMapping("/{id}")
    public ContactResponse updateContact(
        @PathVariable Integer id,
        @Valid @RequestBody ContactRequest request
    ) {
        return service.updateContact(
            id,
            request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(
        @PathVariable Integer id
    ) {
        service.deleteContact(id);
    }
}
