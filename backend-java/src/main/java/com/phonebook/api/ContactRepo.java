package com.phonebook.api;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ContactRepo extends JpaRepository<Contact,Integer> {
  Optional<Contact> findByIdAndUserId(Integer id,Integer userId);
  List<Contact> findByUserId(Integer userId);
  List<Contact> findByUserIdIsNull();
  boolean existsByPhoneNumber(String phone);
  boolean existsByEmail(String email);
  boolean existsByPhoneNumberAndIdNot(String phone,Integer id);
  boolean existsByEmailAndIdNot(String email,Integer id);
}
