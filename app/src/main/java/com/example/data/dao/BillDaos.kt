package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.BusinessProfile
import com.example.data.model.Customer
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItemLine
import com.example.data.model.ProductItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessProfileDao {
    @Query("SELECT * FROM business_profile WHERE id = 1 LIMIT 1")
    fun getProfileFlow(): Flow<BusinessProfile?>

    @Query("SELECT * FROM business_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileSync(): BusinessProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: BusinessProfile)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers ORDER BY name ASC")
    suspend fun getAllCustomersSync(): List<Customer>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Long): Customer?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' OR companyName LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: Customer): Long

    @Update
    suspend fun update(customer: Customer)

    @Delete
    suspend fun delete(customer: Customer)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductItem>>

    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAllProductsSync(): List<ProductItem>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR hsnSacCode LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<ProductItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductItem): Long

    @Update
    suspend fun update(product: ProductItem)

    @Delete
    suspend fun delete(product: ProductItem)
}

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY invoiceDate DESC, id DESC")
    fun getAllInvoices(): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices ORDER BY invoiceDate DESC, id DESC")
    suspend fun getAllInvoicesSync(): List<Invoice>

    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getInvoiceById(id: Long): Invoice?

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId ORDER BY itemSNo ASC")
    suspend fun getLinesForInvoice(invoiceId: Long): List<InvoiceItemLine>

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId ORDER BY itemSNo ASC")
    fun getLinesForInvoiceFlow(invoiceId: Long): Flow<List<InvoiceItemLine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLines(lines: List<InvoiceItemLine>)

    @Query("DELETE FROM invoice_items WHERE invoiceId = :invoiceId")
    suspend fun deleteLinesForInvoice(invoiceId: Long)

    @Transaction
    suspend fun insertFullInvoice(invoice: Invoice, lines: List<InvoiceItemLine>): Long {
        val invoiceId = insertInvoice(invoice)
        deleteLinesForInvoice(invoiceId)
        val updatedLines = lines.map { it.copy(invoiceId = invoiceId) }
        insertLines(updatedLines)
        return invoiceId
    }

    @Query("UPDATE invoices SET status = :status, advancePaid = :advancePaid, balanceDue = :balanceDue, transactionId = :txnId, paymentDate = :paymentDate WHERE id = :invoiceId")
    suspend fun updatePaymentStatus(invoiceId: Long, status: String, advancePaid: Double, balanceDue: Double, txnId: String, paymentDate: Long)

    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoiceById(invoiceId: Long)

    @Query("SELECT COUNT(*) FROM invoices")
    suspend fun getInvoiceCount(): Int
}
