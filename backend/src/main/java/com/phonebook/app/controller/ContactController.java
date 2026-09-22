package com.phonebook.app.controller;
import com.phonebook.app.dto.*; import com.phonebook.app.model.*; import com.phonebook.app.repository.*; import com.phonebook.app.security.*;
import jakarta.validation.Valid; import org.springframework.data.domain.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException; import java.util.*;
@RestController @RequestMapping("/contacts")
public class ContactController {
 private final ContactRepository contacts; private final AuthService auth; private final JwtService jwt;
 public ContactController(ContactRepository c,AuthService a,JwtService j){contacts=c;auth=a;jwt=j;}
 @GetMapping public Map<String,Object> list(@RequestParam(required=false) String search,@RequestParam(required=false) String category,@RequestParam(defaultValue="1") int page,@RequestParam(defaultValue="8") int page_size,@RequestHeader(value="Authorization",required=false) String header){
  User u=current(header); if(page<1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Page number must be at least 1"); if(page_size<1||page_size>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Page size must be between 1 and 100"); validateCategory(category);
  String s=(search==null||search.isBlank())?null:search; Page<Contact> p=contacts.search(u.getId(),s,category,PageRequest.of(page-1,page_size)); Map<String,Object> out=new LinkedHashMap<>();out.put("items",p.getContent().stream().map(ContactResponse::from).toList());out.put("total",p.getTotalElements());out.put("page",page);out.put("page_size",page_size);out.put("total_pages",Math.max(1,p.getTotalPages()));out.put("category",category);return out;
 }
 @PostMapping public ResponseEntity<ContactResponse> create(@Valid @RequestBody ContactRequest r,@RequestHeader(value="Authorization",required=false) String h){r.validate();User u=current(h);Contact c=new Contact();apply(c,r,u);return ResponseEntity.status(201).body(ContactResponse.from(contacts.save(c)));}
 @GetMapping("/{id}") public ContactResponse get(@PathVariable Integer id,@RequestHeader(value="Authorization",required=false) String h){return ContactResponse.from(find(id,current(h)));}
 @PutMapping("/{id}") public ContactResponse update(@PathVariable Integer id,@Valid @RequestBody ContactRequest r,@RequestHeader(value="Authorization",required=false) String h){r.validate();Contact c=find(id,current(h));apply(c,r,c.getUser());return ContactResponse.from(contacts.save(c));}
 @DeleteMapping("/{id}") public Map<String,Object> delete(@PathVariable Integer id,@RequestHeader(value="Authorization",required=false) String h){find(id,current(h));contacts.deleteById(id);return Map.of("message","Contact deleted successfully","id",id);}
 private void apply(Contact c,ContactRequest r,User u){c.setUser(u);c.setName(r.name().trim());c.setPhoneNumber(r.phone_number().trim());c.setEmail(r.email());c.setAddress(r.address());c.setCategory(r.category()==null?"FRIEND":r.category());}
 private Contact find(Integer id,User u){return contacts.findByIdAndUserId(id,u.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not found"));}
 private void validateCategory(String c){if(c!=null&&!Set.of("WORK","FAMILY","FRIEND").contains(c))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Category must be WORK, FAMILY, or FRIEND");}
 private User current(String h){try{if(h==null||!h.startsWith("Bearer "))throw new Exception();return auth.required(jwt.userId(h.substring(7)));}catch(Exception e){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Could not validate authentication credentials");}}
}
