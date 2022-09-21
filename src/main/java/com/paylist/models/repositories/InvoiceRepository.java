package com.paylist.models.repositories;

import com.paylist.models.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {

    public List<Invoice> findAll();

    public Invoice findByUid(Integer uid);

}
