package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: Int = 1,
    val companyName: String = "Acme Traders & Services",
    val tagline: String = "Quality & Reliability Guaranteed",
    val streetAddress: String = "123 Business Avenue, Tech Park",
    val cityStatePincode: String = "Mumbai, Maharashtra - 400001",
    val phoneNumbers: String = "+91 98765 43210 / +91 98123 45678",
    val email: String = "billing@acmetraders.com",
    val website: String = "www.acmetraders.com",
    val gstin: String = "27AAAAA0000A1Z5",
    val panNumber: String = "AAAAA0000A",
    val businessType: String = "Retail & Wholesale",
    val bankAccountName: String = "Acme Traders Current A/C",
    val bankAccountNumber: String = "987654321098",
    val bankIfsc: String = "SBIN0001234",
    val bankNameBranch: String = "State Bank of India, Main Branch",
    val upiId: String = "acmetraders@upi",
    val defaultTerms: String = "1. Goods once sold will not be taken back.\n2. Payment due within 15 days of invoice date.\n3. Subject to local jurisdiction.",
    val defaultNotes: String = "Thank you for your business!",
    val logoUri: String? = null,
    val signatureUri: String? = null
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val companyName: String = "",
    val email: String = "",
    val phone: String = "",
    val billingAddress: String = "",
    val shippingAddress: String = "",
    val gstin: String = "",
    val isB2b: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val hsnSacCode: String = "",
    val unitPrice: Double = 0.0,
    val unit: String = "pcs", // pcs, kg, box, hrs, set, mtr, etc.
    val taxRatePercentage: Double = 18.0, // 0, 5, 12, 18, 28
    val stockQuantity: Int = 100,
    val category: String = "General"
)

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val invoiceType: String = "Tax Invoice", // Tax Invoice, Proforma, Credit Note, Debit Note, Estimate
    val invoiceDate: Long = System.currentTimeMillis(),
    val dueDate: Long = System.currentTimeMillis() + (15L * 24 * 60 * 60 * 1000),
    val poNumber: String = "",
    val placeOfSupply: String = "Maharashtra (27)",
    val paymentTerms: String = "Net 15",
    
    // Customer Info
    val customerId: Long? = null,
    val customerName: String,
    val customerCompany: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val customerGstin: String = "",
    val customerBillingAddress: String = "",
    val customerShippingAddress: String = "",
    val isB2b: Boolean = false,
    
    // Status
    val status: String = "Unpaid", // Paid, Unpaid, Partial, Overdue, Cancelled
    
    // Calculations
    val subtotal: Double = 0.0,
    val itemDiscountTotal: Double = 0.0,
    val overallDiscountPercentage: Double = 0.0,
    val overallDiscountFlat: Double = 0.0,
    
    val isInterstate: Boolean = false, // false = CGST+SGST, true = IGST
    val cgstAmount: Double = 0.0,
    val sgstAmount: Double = 0.0,
    val igstAmount: Double = 0.0,
    
    val shippingCharges: Double = 0.0,
    val otherChargesLabel: String = "Packaging & Handling",
    val otherChargesAmount: Double = 0.0,
    val roundOff: Double = 0.0,
    val grandTotal: Double = 0.0,
    
    val advancePaid: Double = 0.0,
    val balanceDue: Double = 0.0,
    
    // Payment & Customization
    val paymentMethod: String = "UPI / Bank Transfer",
    val transactionId: String = "",
    val paymentDate: Long? = null,
    val termsAndConditions: String = "",
    val notes: String = "",
    
    // Styling
    val templateName: String = "Modern", // Classic, Modern, Minimal, Compact
    val themeColorHex: String = "#1E293B",
    val currencySymbol: String = "₹",
    val language: String = "English", // English or Hindi
    
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "invoice_items")
data class InvoiceItemLine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val itemSNo: Int,
    val itemName: String,
    val itemDescription: String = "",
    val hsnSacCode: String = "",
    val quantity: Double = 1.0,
    val unit: String = "pcs",
    val rate: Double = 0.0,
    val discountPercent: Double = 0.0,
    val taxRatePercent: Double = 18.0,
    val lineTotal: Double = 0.0
)

data class InvoiceWithLines(
    val invoice: Invoice,
    val lines: List<InvoiceItemLine>
)
