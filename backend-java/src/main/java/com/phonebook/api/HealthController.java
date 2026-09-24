package com.phonebook.api;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Map;
@RestController
public class HealthController {
  private final JdbcTemplate db;
  HealthController(JdbcTemplate db){this.db=db;}
  @GetMapping("/api/health")
  public Object health(){
    try{ db.queryForObject("SELECT 1", Integer.class); }
    catch(Exception e){ throw new ApiException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,"Database unavailable"); }
    return Map.of("api","online","database","online");
  }
}
