package com.shliffen.backend.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 * Comparator for LocalDateTimeObjects
 */
public class LocalDateComparator implements Comparator<LocalDate> {

    protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @Override
    public int compare(LocalDate o1, LocalDate o2) {
        return formatter.format(o1).compareTo(formatter.format(o2));
    }
}
