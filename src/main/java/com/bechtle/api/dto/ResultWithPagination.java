package com.bechtle.api.dto;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ResultWithPagination<M> {
    @Expose
    private int size;
    @Expose
    private int page;
    @Expose
    private Long count;
    @Expose
    private List<M> entities;

    public ResultWithPagination(int size, int page, Long count, List<M> entities) {
        this.size = size;
        this.page = page;
        this.count = count;
        this.entities = entities;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<M> getEntities() {
        return entities;
    }

    public void setEntities(List<M> entities) {
        this.entities = entities;
    }
}
