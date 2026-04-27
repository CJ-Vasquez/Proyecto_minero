package com.minero.logistica.model.request;

import lombok.Data;

@Data
public class PaginacionRequest {
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "id";
    private String sortDirection = "DESC";
    
    public String getSortDirection() {
        return "ASC".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";
    }
}