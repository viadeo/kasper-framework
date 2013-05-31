package com.viadeo.kasper.db.util;


import java.util.Collection;

public class ArrayWrapper {

    private final Collection<?> items;

    public ArrayWrapper(Collection<?> c) {
        items = c;
    }

    public String getList() {
        StringBuilder s = new StringBuilder();
        if (items != null) {
            for (Object id : items) {
                s.append(id.toString()).append(",");
            }
            if ( s.length()>0 ) {
                s.deleteCharAt(s.length()-1);
            }
        }
        return s.toString();
    }

}
