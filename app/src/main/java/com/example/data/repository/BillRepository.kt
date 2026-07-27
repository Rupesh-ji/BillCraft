package com.example.data.repository

import com.example.data.dao.BusinessProfileDao
import com.example.data.dao.CustomerDao
import com.example.data.dao.InvoiceDao
import com.example.data.dao.ProductDao
import com.example.data.model.BusinessProfile
import com.example.data.model.Customer
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItemLine
import com.example.data.model.InvoiceWithLines
import com.example.data.model.ProductItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BillRepository(
    private val profileDao: BusinessProfileDao,
    private val customerDao: CustomerDao,
    private val productDao: ProductDao,
    private val invoiceDao: InvoiceDao
) {
    val businessProfile: Flow<BusinessProfile?> = profileDao.getProfileFlow()
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    val allProducts: Flow<List<ProductItem>> = productDao.getAllProducts()
    val allInvoices: Flow<List<Invoice>> = invoiceDao.getAllInvoices()

    suspend fun saveBusinessProfile(profile: BusinessProfile) {
        profileDao.insertOrUpdate(profile)
    }

    suspend fun getBusinessProfileSync(): BusinessProfile {
        return profileDao.getProfileSync() ?: BusinessProfile()
    }

    suspend fun allCustomersSync(): List<Customer> = customerDao.getAllCustomersSync()
    suspend fun allProductsSync(): List<ProductItem> = productDao.getAllProductsSync()
    suspend fun allInvoicesSync(): List<Invoice> = invoiceDao.getAllInvoicesSync()

    // Customer
    suspend fun saveCustomer(customer: Customer): Long = customerDao.insert(customer)
    suspend fun updateCustomer(customer: Customer) = customerDao.update(customer)
    suspend fun deleteCustomer(customer: Customer) = customerDao.delete(customer)

    // Product
    suspend fun saveProduct(product: ProductItem): Long = productDao.insert(product)
    suspend fun updateProduct(product: ProductItem) = productDao.update(product)
    suspend fun deleteProduct(product: ProductItem) = productDao.delete(product)

    // Invoice
    suspend fun saveInvoice(invoice: Invoice, lines: List<InvoiceItemLine>): Long {
        return invoiceDao.insertFullInvoice(invoice, lines)
    }

    suspend fun getInvoiceWithLines(invoiceId: Long): InvoiceWithLines? {
        val inv = invoiceDao.getInvoiceById(invoiceId) ?: return null
        val lines = invoiceDao.getLinesForInvoice(invoiceId)
        return InvoiceWithLines(inv, lines)
    }

    suspend fun updateInvoicePayment(invoiceId: Long, status: String, advancePaid: Double, balanceDue: Double, txnId: String, paymentDate: Long) {
        invoiceDao.updatePaymentStatus(invoiceId, status, advancePaid, balanceDue, txnId, paymentDate)
    }

    suspend fun deleteInvoice(invoiceId: Long) {
        invoiceDao.deleteLinesForInvoice(invoiceId)
        invoiceDao.deleteInvoiceById(invoiceId)
    }

    suspend fun getInvoiceCount(): Int = invoiceDao.getInvoiceCount()
}
