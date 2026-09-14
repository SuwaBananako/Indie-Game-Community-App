package com.example.indiegamecommunity.network;

import java.util.List;

public class PageResponse<T> {
    public List<T> content = null;
    public int totalPages = 0;
    public long totalElements = 0;
    public int currentPage = 0;

    public List<T> getContent() { return content; }
    public int getTotalPages() { return totalPages; }
    public long getTotalElements() { return totalElements; }
    public int getCurrentPage() { return currentPage; }
}
