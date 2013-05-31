package com.viadeo.kasper.db.util;


import com.google.common.base.Joiner;

public class Sql {

    public static String in(Object... items) {

        return Joiner
                .on(",")
                .skipNulls()
                .join(items)
                .replaceAll(" ","");

    }
}
