package com.phonebook.app.dto;

import java.util.List;

public record BulkDeleteRequest(List<Integer> ids) {}
