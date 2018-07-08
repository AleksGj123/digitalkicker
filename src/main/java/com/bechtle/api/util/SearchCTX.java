package com.bechtle.api.util;

import java.util.Map;


public class SearchCTX {

    private int page;
    private int size;
    private Map<String, String[]> filter;
    private Map<String, String[]> sorting;

    public SearchCTX(String page, String size, Map<String, String[]> filter, Map<String, String[]> sorting){
        int pageAsInt = page == null ? 0 : Integer.parseInt(page);
        int sizeAsInt = size == null ? 25 : Integer.parseInt(size);
        this.page = pageAsInt;
        this.size = sizeAsInt;
        this.filter = filter;
        this.sorting = sorting;
    }

    public int getPage() {return page;}

    public void setPage(int page) {this.page = page;}

    public int getSize() {return size;}

    public void setSize(int size) {this.size = size;}

    public Map<String, String[]> getFilter() {return filter;}

    public void setFilter(Map<String, String[]> filter) {this.filter = filter;}

    public Map<String, String[]> getSorting() {return sorting;}

    public void setSorting(Map<String, String[]> sorting) {this.sorting = sorting;}

}
