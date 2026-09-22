package com.phonebook.phonebook.service;

import com.phonebook.phonebook.dto.ContactRequest;
import com.phonebook.phonebook.dto.ContactResponse;
import com.phonebook.phonebook.dto.PagedContactsResponse;
import com.phonebook.phonebook.model.Contact;
import com.phonebook.phonebook.model.ContactCategory;
import com.phonebook.phonebook.repository.ContactRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContactService {

    private final ContactRepository repository;

    public ContactService(ContactRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PagedContactsResponse getContacts(
        String search,
        ContactCategory category,
        int page,
        int pageSize
    ) {
        Pageable pageable = PageRequest.of(
            page - 1,
            pageSize,
            Sort.by(Sort.Direction.DESC, "id")
        );

        var result = repository.searchContacts(
            search,
            category,
            pageable
        );

        return new PagedContactsResponse(
            result.getContent().stream()
                .map(this::toResponse)
                .toList(),
            result.getTotalElements(),
            page,
            pageSize,
            Math.max(result.getTotalPages(), 1),
            category == null ? null : category.name()
        );
    }

    @Transactional(readOnly = true)
    public ContactResponse getContact(Integer id) {
        return toResponse(findContact(id));
    }

    public ContactResponse createContact(
        ContactRequest request
    ) {
        ensureUniquePhone(
            request.phoneNumber(),
            null
        );

        ensureUniqueEmail(
            request.email(),
            null
        );

        Contact contact = new Contact();

        apply(contact, request);

        return toResponse(
            repository.save(contact)
        );
    }

    public ContactResponse updateContact(
        Integer id,
        ContactRequest request
    ) {
        Contact contact =
            findContact(id);

        ensureUniquePhone(
            request.phoneNumber(),
            id
        );

        ensureUniqueEmail(
            request.email(),
            id
        );

        apply(contact, request);

        return toResponse(
            repository.save(contact)
        );
    }

    public void deleteContact(Integer id) {
        repository.delete(
            findContact(id)
        );
    }

    private Contact findContact(Integer id) {
        return repository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "Contact not found"
                )
            );
    }

    private void apply(
        Contact contact,
        ContactRequest request
    ) {
        contact.setName(
            request.name().trim()
        );

        contact.setPhoneNumber(
            request.phoneNumber().trim()
        );

        contact.setEmail(
            normalize(request.email())
        );

        contact.setAddress(
            normalize(request.address())
        );

        contact.setCategory(
            request.category()
        );
    }

    private String normalize(
        String value
    ) {
        if (value == null) {
            return null;
        }

        String trimmed =
            value.trim();

        return trimmed.isEmpty()
            ? null
            : trimmed;
    }

    private void ensureUniquePhone(
        String phone,
        Integer currentId
    ) {
        repository
            .findByPhoneNumber(
                phone.trim()
            )
            .filter(existing ->
                currentId == null ||
                !existing.getId()
                    .equals(currentId)
            )
            .ifPresent(existing -> {
                throw new DataIntegrityViolationException(
                    "Phone number already exists"
                );
            });
    }

    private void ensureUniqueEmail(
        String email,
        Integer currentId
    ) {
        if (
            email == null ||
            email.isBlank()
        ) {
            return;
        }

        repository
            .findByEmail(
                email.trim()
            )
            .filter(existing ->
                currentId == null ||
                !existing.getId()
                    .equals(currentId)
            )
            .ifPresent(existing -> {
                throw new DataIntegrityViolationException(
                    "Email already exists"
                );
            });
    }

    private ContactResponse toResponse(
        Contact contact
    ) {
        return new ContactResponse(
            contact.getId(),
            contact.getName(),
            contact.getPhoneNumber(),
            contact.getEmail(),
            contact.getAddress(),
            contact.getCategory(),
            contact.getCreatedAt()
        );
    }
}
