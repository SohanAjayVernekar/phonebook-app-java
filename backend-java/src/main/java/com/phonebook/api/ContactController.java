package com.phonebook.api;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.data.domain.*;
import java.time.*;
import java.util.*;
import java.util.regex.*;
@RestController @RequestMapping("/api/contacts")
public class ContactController {
 private final ContactRepo contacts; private final UserRepo users;
 private static final Set<String> CATS=Set.of("WORK","FAMILY","FRIEND"); private static final Pattern PHONE=Pattern.compile("^\\+?[1-9]\\d{6,19}$");
 ContactController(ContactRepo c,UserRepo u){contacts=c;users=u;}
 private User user(org.springframework.security.core.Authentication a){return users.findById((Integer)a.getPrincipal()).orElseThrow(()->new ApiException(HttpStatus.BAD_REQUEST,"Could not validate authentication credentials"));}
 private Contact owned(int id,int uid){return contacts.findByIdAndUserId(id,uid).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Contact not found"));}
 private Contact apply(Contact c,Dto.ContactReq r,int uid){
  if(r.name()==null||r.name().isBlank()||r.name().length()>255)throw new ApiException(HttpStatus.BAD_REQUEST,"size must be between 0 and 255");
  if(r.phone_number()==null||r.phone_number().length()<7||r.phone_number().length()>20)throw new ApiException(HttpStatus.BAD_REQUEST,"size must be between 7 and 20");
  var phone=r.phone_number().trim();if(!PHONE.matcher(phone).matches())throw new ApiException(HttpStatus.BAD_REQUEST,"Phone number must contain 7-20 digits and may start with +");
  if(r.email()!=null&&!r.email().isBlank()&&!r.email().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))throw new ApiException(HttpStatus.BAD_REQUEST,"must be a well-formed email address");
  var cat=r.category()==null?"FRIEND":r.category().toUpperCase();if(!CATS.contains(cat))throw new ApiException(HttpStatus.BAD_REQUEST,"Category must be WORK, FAMILY, or FRIEND");
  boolean phoneTaken=(c.id==null)?contacts.existsByPhoneNumber(phone):contacts.existsByPhoneNumberAndIdNot(phone,c.id);if(phoneTaken)throw new ApiException(HttpStatus.CONFLICT,"Phone number is already in use");
  if(r.email()!=null&&!r.email().isBlank()){boolean emailTaken=(c.id==null)?contacts.existsByEmail(r.email()):contacts.existsByEmailAndIdNot(r.email(),c.id);if(emailTaken)throw new ApiException(HttpStatus.CONFLICT,"Email address is already in use");}
  c.userId=uid;c.name=r.name().trim();c.phoneNumber=phone;c.email=(r.email()==null||r.email().isBlank()?null:r.email());c.address=r.address();c.category=cat;if(c.createdAt==null)c.createdAt=Instant.now();return contacts.save(c);
 }
 @GetMapping Object list(@RequestParam(required=false) String search,@RequestParam(required=false) String category,@RequestParam(required=false) String categories,@RequestParam(required=false) String sort,@RequestParam(name="has_email",required=false) String hasEmail,@RequestParam(name="date_from",required=false) String dateFrom,@RequestParam(name="date_to",required=false) String dateTo,@RequestParam(defaultValue="1") int page,@RequestParam(name="page_size",defaultValue="8") int size,org.springframework.security.core.Authentication a){
  if(page<1)throw new ApiException(HttpStatus.BAD_REQUEST,"Page number must be at least 1");if(size<1||size>100)throw new ApiException(HttpStatus.BAD_REQUEST,"Page size must be between 1 and 100");
  if(sort!=null&&!Set.of("newest","oldest","name_asc","name_desc").contains(sort))throw new ApiException(HttpStatus.BAD_REQUEST,"sort must be one of newest, oldest, name_asc, name_desc");
  int uid=user(a).id;List<Contact> all=new ArrayList<>(contacts.findByUserId(uid));
  if(search!=null&&!search.isBlank()){String q=search.trim().toLowerCase();all.removeIf(c->!c.name.toLowerCase().contains(q)&&!c.phoneNumber.toLowerCase().contains(q));}
  Set<String> cs=new HashSet<>();if(category!=null){var cu=category.toUpperCase();if(!CATS.contains(cu))throw new ApiException(HttpStatus.BAD_REQUEST,"Category must be WORK, FAMILY, or FRIEND");cs.add(cu);}if(categories!=null)for(String x:categories.split(",")){var cu=x.trim().toUpperCase();if(cu.isEmpty())continue;if(!CATS.contains(cu))throw new ApiException(HttpStatus.BAD_REQUEST,"Category must be WORK, FAMILY, or FRIEND");cs.add(cu);}if(!cs.isEmpty())all.removeIf(c->!cs.contains(c.category));
  Boolean wantEmail=null;if(hasEmail!=null){if(hasEmail.equalsIgnoreCase("true"))wantEmail=true;else if(hasEmail.equalsIgnoreCase("false"))wantEmail=false;else throw new ApiException(HttpStatus.BAD_REQUEST,"has_email must be true or false");boolean w=wantEmail;all.removeIf(c->w!=(c.email!=null));}
  if(dateFrom!=null){Instant f;try{java.time.LocalDate.parse(dateFrom);f=Instant.parse(dateFrom+"T00:00:00Z");}catch(Exception e){throw new ApiException(HttpStatus.BAD_REQUEST,"Dates must use the format yyyy-MM-dd");}all.removeIf(c->c.createdAt.isBefore(f));} if(dateTo!=null){Instant t;try{java.time.LocalDate.parse(dateTo);t=Instant.parse(dateTo+"T00:00:00Z").plus(1,java.time.temporal.ChronoUnit.DAYS);}catch(Exception e){throw new ApiException(HttpStatus.BAD_REQUEST,"Dates must use the format yyyy-MM-dd");}all.removeIf(c->!c.createdAt.isBefore(t));}
  Comparator<Contact> cmp=Comparator.comparing(c->c.id);if("oldest".equals(sort)){}else if("name_asc".equals(sort))cmp=Comparator.comparing(c->c.name.toLowerCase());else if("name_desc".equals(sort))cmp=Comparator.comparing((Contact c)->c.name.toLowerCase()).reversed();else cmp=cmp.reversed();all.sort(cmp);
  int total=all.size(), pages=Math.max(1,(int)Math.ceil(total/(double)size)),from=Math.min((page-1)*size,total),to=Math.min(from+size,total);var items=all.subList(from,to).stream().map(Dto::contact).toList();
  var out=new LinkedHashMap<String,Object>();out.put("items",items);out.put("total",total);out.put("page",page);out.put("page_size",size);out.put("total_pages",pages);out.put("category",category);out.put("sort",sort==null?"newest":sort);return out;
 }
 @PostMapping ResponseEntity<?> create(@RequestBody Dto.ContactReq r,org.springframework.security.core.Authentication a){return ResponseEntity.status(201).body(Dto.contact(apply(new Contact(),r,user(a).id)));}
 @GetMapping("/{id}") Object get(@PathVariable int id,org.springframework.security.core.Authentication a){return Dto.contact(owned(id,user(a).id));}
 @PutMapping("/{id}") Object update(@PathVariable int id,@RequestBody Dto.ContactReq r,org.springframework.security.core.Authentication a){return Dto.contact(apply(owned(id,user(a).id),r,user(a).id));}
 @DeleteMapping("/{id}") Object delete(@PathVariable int id,org.springframework.security.core.Authentication a){owned(id,user(a).id);contacts.deleteById(id);return Map.of("message","Contact deleted successfully","id",id);}
 @PostMapping("/bulk-delete") Object bulk(@RequestBody Dto.Bulk b,org.springframework.security.core.Authentication a){if(b.ids()==null||b.ids().isEmpty())throw new ApiException(HttpStatus.BAD_REQUEST,"Provide at least one contact id");if(b.ids().size()>100)throw new ApiException(HttpStatus.BAD_REQUEST,"You can delete at most 100 contacts at once");int uid=user(a).id;long n=b.ids().stream().map(i->contacts.findByIdAndUserId(i,uid).orElse(null)).filter(Objects::nonNull).peek(contacts::delete).count();return Map.of("deleted",n);}
}
