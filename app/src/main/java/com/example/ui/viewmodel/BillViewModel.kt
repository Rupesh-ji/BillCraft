package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.BusinessProfile
import com.example.data.model.Customer
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItemLine
import com.example.data.model.InvoiceWithLines
import com.example.data.model.ProductItem
import com.example.data.repository.BillRepository
import com.example.util.SampleDataSeeder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BillViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = BillRepository(
        db.businessProfileDao(),
        db.customerDao(),
        db.productDao(),
        db.invoiceDao()
    )

    // Data Flows
    val businessProfile: StateFlow<BusinessProfile> = repository.businessProfile
        .combine(MutableStateFlow(Unit)) { prof, _ -> prof ?: BusinessProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BusinessProfile())

    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts: StateFlow<List<ProductItem>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInvoices: StateFlow<List<Invoice>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Filter States for Invoices
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("All") // All, Paid, Unpaid, Partial, Overdue, Estimate
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    val filteredInvoices: StateFlow<List<Invoice>> = combine(
        allInvoices,
        _searchQuery,
        _selectedStatusFilter
    ) { invoices, query, filter ->
        invoices.filter { inv ->
            val matchesQuery = query.isBlank() ||
                    inv.invoiceNumber.contains(query, ignoreCase = true) ||
                    inv.customerName.contains(query, ignoreCase = true) ||
                    inv.customerCompany.contains(query, ignoreCase = true)
            
            val matchesFilter = when (filter) {
                "All" -> true
                "Paid" -> inv.status == "Paid"
                "Unpaid" -> inv.status == "Unpaid"
                "Partial" -> inv.status == "Partial"
                "Overdue" -> inv.dueDate < System.currentTimeMillis() && inv.status != "Paid"
                "Estimate" -> inv.invoiceType == "Estimate"
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Selected Invoice Details State
    private val _selectedInvoiceWithLines = MutableStateFlow<InvoiceWithLines?>(null)
    val selectedInvoiceWithLines: StateFlow<InvoiceWithLines?> = _selectedInvoiceWithLines.asStateFlow()

    init {
        viewModelScope.launch {
            SampleDataSeeder.seedSampleDataIfEmpty(repository)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateStatusFilter(filter: String) {
        _selectedStatusFilter.value = filter
    }

    fun loadInvoiceDetail(invoiceId: Long) {
        viewModelScope.launch {
            _selectedInvoiceWithLines.value = repository.getInvoiceWithLines(invoiceId)
        }
    }

    fun saveBusinessProfile(profile: BusinessProfile) {
        viewModelScope.launch {
            repository.saveBusinessProfile(profile)
        }
    }

    fun saveCustomer(customer: Customer, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveCustomer(customer)
            onSaved()
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    fun saveProduct(product: ProductItem, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveProduct(product)
            onSaved()
        }
    }

    fun deleteProduct(product: ProductItem) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun saveFullInvoice(invoice: Invoice, lines: List<InvoiceItemLine>, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.saveInvoice(invoice, lines)
            loadInvoiceDetail(id)
            onComplete(id)
        }
    }

    fun updatePaymentStatus(invoiceId: Long, status: String, advancePaid: Double, balanceDue: Double, txnId: String) {
        viewModelScope.launch {
            repository.updateInvoicePayment(
                invoiceId = invoiceId,
                status = status,
                advancePaid = advancePaid,
                balanceDue = balanceDue,
                txnId = txnId,
                paymentDate = System.currentTimeMillis()
            )
            loadInvoiceDetail(invoiceId)
        }
    }

    fun deleteInvoice(invoiceId: Long) {
        viewModelScope.launch {
            repository.deleteInvoice(invoiceId)
            _selectedInvoiceWithLines.value = null
        }
    }

    suspend fun generateNextInvoiceNumber(prefix: String = "INV-2026-"): String {
        val count = repository.getInvoiceCount() + 1
        return String.format("%s%03d", prefix, count)
    }

    suspend fun exportBackupJson(): String {
        return com.example.util.BackupRestoreUtils.exportFullBackupJson(repository)
    }

    suspend fun restoreBackupJson(jsonString: String): Triple<Int, Int, Int> {
        return com.example.util.BackupRestoreUtils.restoreFullBackupJson(jsonString, repository)
    }
}
