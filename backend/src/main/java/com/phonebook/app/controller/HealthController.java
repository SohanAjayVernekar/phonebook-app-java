package com.phonebook.app.controller;
import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController public class HealthController { private final JdbcTemplate jdbc; public HealthController(JdbcTemplate j){jdbc=j;} @GetMapping("/api/health") public Map<String,String> health(){String db="online";try{jdbc.queryForObject("SELECT 1",Integer.class);}catch(Exception e){db="offline";}return Map.of("api","online","database",db);}}
