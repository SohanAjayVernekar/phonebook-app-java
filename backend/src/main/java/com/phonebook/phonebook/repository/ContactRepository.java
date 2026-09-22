package com.phonebook.phonebook.repository;

import com.phonebook.phonebook.model.Contact;
import com.phonebook.phonebook.model.ContactCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    @Query("SELECT c FROM Contact c " +
           "WHERE (:search IS NULL OR :search = '' " +
           "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:category IS NULL OR c.category = :category) " +
           "ORDER BY c.id DESC")
    Page<Contact> searchContacts(
        @Param("search") String search,
        @Param("category") ContactCategory category,
        Pageable pageable
    );

    Optional<Contact> findByPhoneNumber(String phoneNumber);

    Optional<Contact> findByEmail(String email);
}
