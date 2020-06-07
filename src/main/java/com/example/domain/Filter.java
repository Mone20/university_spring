package com.example.domain;

import javax.persistence.Entity;


public class Filter {
    private String firstFilter;
    private String secondFilter;

    public Filter() {
    }

    public String getFirstFilter() {
        return firstFilter;
    }

    public void setFirstFilter(String firstFilter) {
        this.firstFilter = firstFilter;
    }

    public String getSecondFilter() {
        return secondFilter;
    }

    public void setSecondFilter(String secondFilter) {
        this.secondFilter = secondFilter;
    }
    public boolean firstIsEmpty()
    {
        if(firstFilter.length()==0||firstFilter==null)
            return true;
        return false;
    }
    public boolean secondIsEmpty()
    {
        if(secondFilter.length()==0||secondFilter==null)
            return true;
        return false;
    }
}
