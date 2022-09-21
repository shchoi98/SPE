package com.paylist.email;

import com.paylist.models.Invoice;

public interface EmailService {
    public void sendEmail(Invoice invoice);
}