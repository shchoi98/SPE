package com.paylist.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "invoices")
public class Invoice {

    private int uid;
    private String filename;
    private String sender;
    private String email;
    private Date dateReceived;
    private Status status;


    public Invoice(String filename, String sender, String email, Date dateReceived, Status status) {
        this.filename = filename;
        this.sender = sender;
        this.email = email;
        this.dateReceived = dateReceived;
        this.status = status;
    }

	//no argument-constructor
    public Invoice(){}

    @Id //primary key
    @GeneratedValue
    @NotNull
    @Column(name = "uid", unique = true)
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Column(name = "filename")
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Column(name = "sender")
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Column(name = "dateReceived")
    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    @Column(name = "status")
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "email")
    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
