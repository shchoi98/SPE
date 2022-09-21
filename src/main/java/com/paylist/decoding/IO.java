package com.paylist.decoding;

import java.io.IOException;
import java.text.ParseException;

import com.paylist.models.Invoice;

public interface IO {
    public Invoice decodeEmail(String filename) throws IOException, ParseException;
}