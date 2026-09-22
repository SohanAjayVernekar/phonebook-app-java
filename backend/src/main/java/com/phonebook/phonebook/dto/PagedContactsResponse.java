package com.phonebook.phonebook.dto;

import java.util.List;

public record PagedContactsResponse(
    List<ContactResponse> items,
    long total,
    int page,
    int pageSize,
    int totalPages,
    String category
) {}
