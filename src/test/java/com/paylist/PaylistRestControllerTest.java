package com.paylist;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.paylist.controllers.PaylistRestController;
import com.paylist.models.Invoice;
import com.paylist.models.Status;
import com.paylist.models.repositories.InvoiceRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(PaylistRestController.class)
@SpringBootApplication
public class PaylistRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private PaylistRestController paylistRestController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paylistRestController).build();
    }

    @Test
    public void getAllTest() throws Exception {

        // Invoice(String filename, String sender, String email, Date dateReceived,
        // Status status

        List<Invoice> invoiceList = Arrays.asList(
                new Invoice("test1", "testSender1", "send1@test.com", new Date(01 - 01 - 01), Status.PAID),
                new Invoice("test2", "testSender2", "send2@test.com", new Date(02 - 02 - 02), Status.CANCELLED));

        Invoice invoice1 = invoiceList.get(0);
        Invoice invoice2 = invoiceList.get(1);
        invoice1.setUid(1);
        invoice2.setUid(2);

        when(invoiceRepository.findAll()).thenReturn(invoiceList);

        mockMvc.perform(get("/api/invoices")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uid", is(1))).andExpect(jsonPath("$[0].filename", is("test1")))
                .andExpect(jsonPath("$[0].sender", is("testSender1")))
                .andExpect(jsonPath("$[0].dateReceived", is(01 - 01 - 01)))
                .andExpect(jsonPath("$[0].status", is("PAID")))

                .andExpect(jsonPath("$[1].uid", is(2))).andExpect(jsonPath("$[1].filename", is("test2")))
                .andExpect(jsonPath("$[1].sender", is("testSender2")))
                .andExpect(jsonPath("$[1].dateReceived", is(02 - 02 - 02)))
                .andExpect(jsonPath("$[1].status", is("CANCELLED")));

    }

    @Test
    public void getByUidTest() throws Exception {
        when(invoiceRepository.findByUid(1))
                .thenReturn(new Invoice("test", "test", "test@test.com", new Date(01 - 01 - 01), Status.PAID));

        Invoice testInvoice = invoiceRepository.findByUid(1);
        Assert.assertEquals("test", testInvoice.getFilename());
    }

    @Test
    public void changeStatusTest() throws Exception {

        when(invoiceRepository.findAll())
                .thenReturn(Stream.of(new Invoice("test", "test", "test@test.com", new Date(01 - 01 - 01), Status.PAID))
                        .collect(Collectors.toList()));

        List<Invoice> test = invoiceRepository.findAll();
        test.get(0).setStatus(Status.PENDING);

        Assert.assertEquals(Status.PENDING, test.get(0).getStatus());

    }

    @Test
    public void saveInvoiceTest() throws Exception {

        Invoice i = new Invoice("test", "test", "test@test.com", new Date(01 - 01 - 01), Status.PAID);

        invoiceRepository.save(i);

        verify(invoiceRepository, times(1)).save(i);

    }

    @Test
    public void deleteInvoiceTest() throws Exception {

        when(invoiceRepository.findAll())
                .thenReturn(Stream.of(new Invoice("test", "test", "test@test.com", new Date(01 - 01 - 01), Status.PAID))
                        .collect(Collectors.toList()));

        List<Invoice> del = invoiceRepository.findAll();

        Invoice t = del.get(0);

        invoiceRepository.delete(t);

        verify(invoiceRepository, times(0)).save(t);
    }

    @Test
    public void contextLoads() throws Exception {

        when(invoiceRepository.findAll()).thenReturn(Collections.emptyList());
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/invoices").accept(MediaType.APPLICATION_JSON)).andReturn();
        verify(invoiceRepository).findAll();
    }

}