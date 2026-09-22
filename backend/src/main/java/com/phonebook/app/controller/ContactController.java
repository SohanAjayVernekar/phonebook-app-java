package com.phonebook.app.controller;
import com.phonebook.app.dto.*; import com.phonebook.app.model.*; import com.phonebook.app.repository.*; import com.phonebook.app.security.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.*; import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
import java.time.*; import java.time.format.DateTimeParseException; import java.util.*;
@RestController @RequestMapping("/api/contacts")
public class ContactController {
 private static final Set<String> CATEGORIES = Set.of("WORK","FAMILY","FRIEND");
 private static final Set<String> SORTS = Set.of("newest","oldest","name_asc","name_desc");
 private final ContactRepository contacts; private final AuthService auth; private final JwtService jwt;
 public ContactController(ContactRepository c,AuthService a,JwtService j){contacts=c;auth=a;jwt=j;}

 @GetMapping public Map<String,Object> list(
   @RequestParam(required=false) String search,
   @RequestParam(required=false) String category,
   @RequestParam(required=false) String categories,
   @RequestParam(required=false) String sort,
   @RequestParam(name="has_email",required=false) String hasEmail,
   @RequestParam(name="date_from",required=false) String dateFrom,
   @RequestParam(name="date_to",required=false) String dateTo,
   @RequestParam(defaultValue="1") int page,
   @RequestParam(defaultValue="8") int page_size,
   @RequestHeader(value="Authorization",required=false) String header){
  User u=current(header);
  if(page<1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Page number must be at least 1");
  if(page_size<1||page_size>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Page size must be between 1 and 100");
  Set<String> cats=parseCategories(category,categories);
  Boolean emailFlag=parseHasEmail(hasEmail);
  OffsetDateTime from=parseBoundary(dateFrom,false);
  OffsetDateTime to=parseBoundary(dateTo,true);
  String s=(search==null||search.isBlank())?null:search.trim();
  Specification<Contact> spec=filterSpec(u.getId(),s,cats,emailFlag,from,to);
  Pageable pageable=PageRequest.of(page-1,page_size,sortOrder(sort));
  Page<Contact> p=contacts.findAll(spec,pageable);
  Map<String,Object> out=new LinkedHashMap<>();
  out.put("items",p.getContent().stream().map(ContactResponse::from).toList());
  out.put("total",p.getTotalElements());
  out.put("page",page);
  out.put("page_size",page_size);
  out.put("total_pages",Math.max(1,p.getTotalPages()));
  out.put("category",category);
  out.put("sort",sort==null||sort.isBlank()?"newest":sort);
  return out;
 }
 @PostMapping public ResponseEntity<ContactResponse> create(@Valid @RequestBody ContactRequest r,@RequestHeader(value="Authorization",required=false) String h){r.validate();User u=current(h);Contact c=new Contact();apply(c,r,u);return ResponseEntity.status(201).body(ContactResponse.from(contacts.save(c)));}
 @GetMapping("/{id}") public ContactResponse get(@PathVariable Integer id,@RequestHeader(value="Authorization",required=false) String h){return ContactResponse.from(find(id,current(h)));}
 @PutMapping("/{id}") public ContactResponse update(@PathVariable Integer id,@Valid @RequestBody ContactRequest r,@RequestHeader(value="Authorization",required=false) String h){r.validate();Contact c=find(id,current(h));apply(c,r,c.getUser());return ContactResponse.from(contacts.save(c));}
 @DeleteMapping("/{id}") public Map<String,Object> delete(@PathVariable Integer id,@RequestHeader(value="Authorization",required=false) String h){find(id,current(h));contacts.deleteById(id);return Map.of("message","Contact deleted successfully","id",id);}

 @PostMapping("/bulk-delete")
 public Map<String,Object> bulkDelete(@RequestBody BulkDeleteRequest body,@RequestHeader(value="Authorization",required=false) String h){
  User u=current(h);
  List<Integer> ids=body==null?null:body.ids();
  if(ids==null||ids.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Provide at least one contact id");
  if(ids.size()>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You can delete at most 100 contacts at once");
  // Only delete contacts that belong to the signed-in user.
  List<Contact> owned=contacts.findAllByIdInAndUserId(new LinkedHashSet<>(ids),u.getId());
  contacts.deleteAll(owned);
  return Map.of("deleted",owned.size());
 }

 // ---- filtering / sorting helpers -------------------------------------

 private Specification<Contact> filterSpec(Integer userId,String search,Set<String> cats,Boolean hasEmail,OffsetDateTime from,OffsetDateTime to){
  Specification<Contact> spec=(root,q,cb)->cb.equal(root.get("user").get("id"),userId);
  if(search!=null){
   String pattern=likePattern(search);
   spec=spec.and((root,q,cb)->cb.or(
     cb.like(cb.lower(root.get("name")),pattern,'\\'),
     cb.like(cb.lower(root.get("phoneNumber")),pattern,'\\')));
  }
  if(!cats.isEmpty()) spec=spec.and((root,q,cb)->root.get("category").in(cats));
  if(Boolean.TRUE.equals(hasEmail)) spec=spec.and((root,q,cb)->cb.isNotNull(root.get("email")));
  if(Boolean.FALSE.equals(hasEmail)) spec=spec.and((root,q,cb)->cb.isNull(root.get("email")));
  if(from!=null) spec=spec.and((root,q,cb)->cb.greaterThanOrEqualTo(root.get("createdAt"),from));
  if(to!=null) spec=spec.and((root,q,cb)->cb.lessThan(root.get("createdAt"),to));
  return spec;
 }

 private String likePattern(String value){
  StringBuilder sb=new StringBuilder("%");
  for(char ch:value.toLowerCase().toCharArray()){
   if(ch=='\\'||ch=='%'||ch=='_') sb.append('\\');
   sb.append(ch);
  }
  return sb.append('%').toString();
 }

 private Set<String> parseCategories(String single,String multi){
  Set<String> out=new LinkedHashSet<>();
  if(single!=null&&!single.isBlank()){
   String v=single.trim().toUpperCase();
   if(!CATEGORIES.contains(v)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Category must be WORK, FAMILY, or FRIEND");
   out.add(v);
  }
  if(multi!=null&&!multi.isBlank()){
   for(String part:multi.split(",")){
    String v=part.trim().toUpperCase();
    if(v.isEmpty()) continue;
    if(!CATEGORIES.contains(v)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Category must be WORK, FAMILY, or FRIEND");
    out.add(v);
   }
  }
  return out;
 }

 private Boolean parseHasEmail(String value){
  if(value==null||value.isBlank()) return null;
  if(value.equalsIgnoreCase("true")) return Boolean.TRUE;
  if(value.equalsIgnoreCase("false")) return Boolean.FALSE;
  throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"has_email must be true or false");
 }

 // date_from is inclusive; date_to covers the whole chosen day.
 private OffsetDateTime parseBoundary(String value,boolean endOfDay){
  if(value==null||value.isBlank()) return null;
  try{
   LocalDate d=LocalDate.parse(value);
   return endOfDay
     ? d.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime()
     : d.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
  }catch(DateTimeParseException e){
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Dates must use the format yyyy-MM-dd");
  }
 }

 private Sort sortOrder(String sort){
  String key=(sort==null||sort.isBlank())?"newest":sort.trim();
  if(!SORTS.contains(key)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"sort must be one of newest, oldest, name_asc, name_desc");
  return switch(key){
   case "oldest" -> Sort.by(Sort.Direction.ASC,"id");
   case "name_asc" -> Sort.by(Sort.Direction.ASC,"name").and(Sort.by(Sort.Direction.DESC,"id"));
   case "name_desc" -> Sort.by(Sort.Direction.DESC,"name").and(Sort.by(Sort.Direction.DESC,"id"));
   default -> Sort.by(Sort.Direction.DESC,"id");
  };
 }

 private void apply(Contact c,ContactRequest r,User u){c.setUser(u);c.setName(r.name().trim());c.setPhoneNumber(r.phone_number().trim());c.setEmail(r.email());c.setAddress(r.address());c.setCategory(r.category()==null?"FRIEND":r.category());}
 private Contact find(Integer id,User u){return contacts.findByIdAndUserId(id,u.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not found"));}
 private User current(String h){try{if(h==null||!h.startsWith("Bearer "))throw new Exception();return auth.required(jwt.userId(h.substring(7)));}catch(Exception e){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Could not validate authentication credentials");}}
}
