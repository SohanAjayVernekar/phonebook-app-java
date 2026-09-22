package com.phonebook.app.repository;
import com.phonebook.app.model.Contact;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface ContactRepository extends JpaRepository<Contact,Integer>, JpaSpecificationExecutor<Contact> {
    @Query("select c from Contact c where c.user.id=:userId and (coalesce(:search, '') = '' or lower(c.name) like lower(concat('%',:search,'%')) or lower(c.phoneNumber) like lower(concat('%',:search,'%'))) and (:category is null or c.category=:category) order by c.id desc")
    Page<Contact> search(@Param("userId") Integer userId,@Param("search") String search,@Param("category") String category,Pageable pageable);
    Optional<Contact> findByIdAndUserId(Integer id, Integer userId);
    List<Contact> findAllByUserId(Integer userId);
    List<Contact> findAllByIdInAndUserId(Collection<Integer> ids, Integer userId);
    long countByUserId(Integer userId);
    java.util.List<Contact> findByUserIsNull();
}
