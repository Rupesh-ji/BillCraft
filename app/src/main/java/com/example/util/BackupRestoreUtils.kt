package com.example.util

import com.example.data.model.BusinessProfile
import com.example.data.model.Customer
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItemLine
import com.example.data.model.ProductItem
import com.example.data.repository.BillRepository
import org.json.JSONArray
import org.json.JSONObject

object BackupRestoreUtils {

    suspend fun exportFullBackupJson(repository: BillRepository): String {
        val root = JSONObject()

        // Profile
        val profile = repository.getBusinessProfileSync()
        val profObj = JSONObject().apply {
            put("id", profile.id)
            put("companyName", profile.companyName)
            put("tagline", profile.tagline)
            put("streetAddress", profile.streetAddress)
            put("cityStatePincode", profile.cityStatePincode)
            put("phoneNumbers", profile.phoneNumbers)
            put("email", profile.email)
            put("website", profile.website)
            put("gstin", profile.gstin)
            put("panNumber", profile.panNumber)
            put("businessType", profile.businessType)
            put("bankAccountName", profile.bankAccountName)
            put("bankAccountNumber", profile.bankAccountNumber)
            put("bankIfsc", profile.bankIfsc)
            put("bankNameBranch", profile.bankNameBranch)
            put("upiId", profile.upiId)
            put("defaultTerms", profile.defaultTerms)
            put("defaultNotes", profile.defaultNotes)
        }
        root.put("profile", profObj)

        // Customers
        val customers = repository.allCustomersSync()
        val custArr = JSONArray()
        for (c in customers) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("companyName", c.companyName)
                put("email", c.email)
                put("phone", c.phone)
                put("billingAddress", c.billingAddress)
                put("shippingAddress", c.shippingAddress)
                put("gstin", c.gstin)
                put("isB2b", c.isB2b)
                put("createdAt", c.createdAt)
            }
            custArr.put(obj)
        }
        root.put("customers", custArr)

        // Products
        val products = repository.allProductsSync()
        val prodArr = JSONArray()
        for (p in products) {
            val obj = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("description", p.description)
                put("hsnSacCode", p.hsnSacCode)
                put("unitPrice", p.unitPrice)
                put("unit", p.unit)
                put("taxRatePercentage", p.taxRatePercentage)
                put("stockQuantity", p.stockQuantity)
                put("category", p.category)
            }
            prodArr.put(obj)
        }
        root.put("products", prodArr)

        // Invoices with Lines
        val invoices = repository.allInvoicesSync()
        val invArr = JSONArray()
        for (inv in invoices) {
            val fullInv = repository.getInvoiceWithLines(inv.id) ?: continue
            val invObj = JSONObject().apply {
                put("id", inv.id)
                put("invoiceNumber", inv.invoiceNumber)
                put("invoiceType", inv.invoiceType)
                put("invoiceDate", inv.invoiceDate)
                put("dueDate", inv.dueDate)
                put("poNumber", inv.poNumber)
                put("placeOfSupply", inv.placeOfSupply)
                put("paymentTerms", inv.paymentTerms)
                put("customerName", inv.customerName)
                put("customerCompany", inv.customerCompany)
                put("customerPhone", inv.customerPhone)
                put("customerEmail", inv.customerEmail)
                put("customerGstin", inv.customerGstin)
                put("customerBillingAddress", inv.customerBillingAddress)
                put("customerShippingAddress", inv.customerShippingAddress)
                put("isB2b", inv.isB2b)
                put("status", inv.status)
                put("subtotal", inv.subtotal)
                put("cgstAmount", inv.cgstAmount)
                put("sgstAmount", inv.sgstAmount)
                put("igstAmount", inv.igstAmount)
                put("grandTotal", inv.grandTotal)
                put("advancePaid", inv.advancePaid)
                put("balanceDue", inv.balanceDue)
                put("paymentMethod", inv.paymentMethod)
                put("transactionId", inv.transactionId)
                put("termsAndConditions", inv.termsAndConditions)
                put("notes", inv.notes)
                put("templateName", inv.templateName)
                put("themeColorHex", inv.themeColorHex)
                put("currencySymbol", inv.currencySymbol)
                put("language", inv.language)

                val linesArr = JSONArray()
                for (line in fullInv.lines) {
                    val lObj = JSONObject().apply {
                        put("id", line.id)
                        put("invoiceId", line.invoiceId)
                        put("itemSNo", line.itemSNo)
                        put("itemName", line.itemName)
                        put("itemDescription", line.itemDescription)
                        put("hsnSacCode", line.hsnSacCode)
                        put("quantity", line.quantity)
                        put("unit", line.unit)
                        put("rate", line.rate)
                        put("discountPercent", line.discountPercent)
                        put("taxRatePercent", line.taxRatePercent)
                        put("lineTotal", line.lineTotal)
                    }
                    linesArr.put(lObj)
                }
                put("lines", linesArr)
            }
            invArr.put(invObj)
        }
        root.put("invoices", invArr)

        return root.toString(2)
    }

    suspend fun restoreFullBackupJson(jsonString: String, repository: BillRepository): Triple<Int, Int, Int> {
        val root = JSONObject(jsonString)

        // Profile
        if (root.has("profile")) {
            val profObj = root.getJSONObject("profile")
            val profile = BusinessProfile(
                id = profObj.optInt("id", 1),
                companyName = profObj.optString("companyName", "Acme Traders"),
                tagline = profObj.optString("tagline", ""),
                streetAddress = profObj.optString("streetAddress", ""),
                cityStatePincode = profObj.optString("cityStatePincode", ""),
                phoneNumbers = profObj.optString("phoneNumbers", ""),
                email = profObj.optString("email", ""),
                website = profObj.optString("website", ""),
                gstin = profObj.optString("gstin", ""),
                panNumber = profObj.optString("panNumber", ""),
                businessType = profObj.optString("businessType", ""),
                bankAccountName = profObj.optString("bankAccountName", ""),
                bankAccountNumber = profObj.optString("bankAccountNumber", ""),
                bankIfsc = profObj.optString("bankIfsc", ""),
                bankNameBranch = profObj.optString("bankNameBranch", ""),
                upiId = profObj.optString("upiId", ""),
                defaultTerms = profObj.optString("defaultTerms", ""),
                defaultNotes = profObj.optString("defaultNotes", "")
            )
            repository.saveBusinessProfile(profile)
        }

        var custCount = 0
        if (root.has("customers")) {
            val custArr = root.getJSONArray("customers")
            for (i in 0 until custArr.length()) {
                val c = custArr.getJSONObject(i)
                val customer = Customer(
                    id = c.optLong("id", 0L),
                    name = c.optString("name", "Unknown"),
                    companyName = c.optString("companyName", ""),
                    email = c.optString("email", ""),
                    phone = c.optString("phone", ""),
                    billingAddress = c.optString("billingAddress", ""),
                    shippingAddress = c.optString("shippingAddress", ""),
                    gstin = c.optString("gstin", ""),
                    isB2b = c.optBoolean("isB2b", false),
                    createdAt = c.optLong("createdAt", System.currentTimeMillis())
                )
                repository.saveCustomer(customer)
                custCount++
            }
        }

        var prodCount = 0
        if (root.has("products")) {
            val prodArr = root.getJSONArray("products")
            for (i in 0 until prodArr.length()) {
                val p = prodArr.getJSONObject(i)
                val product = ProductItem(
                    id = p.optLong("id", 0L),
                    name = p.optString("name", "Item"),
                    description = p.optString("description", ""),
                    hsnSacCode = p.optString("hsnSacCode", ""),
                    unitPrice = p.optDouble("unitPrice", 0.0),
                    unit = p.optString("unit", "pcs"),
                    taxRatePercentage = p.optDouble("taxRatePercentage", 18.0),
                    stockQuantity = p.optInt("stockQuantity", 100),
                    category = p.optString("category", "General")
                )
                repository.saveProduct(product)
                prodCount++
            }
        }

        var invCount = 0
        if (root.has("invoices")) {
            val invArr = root.getJSONArray("invoices")
            for (i in 0 until invArr.length()) {
                val invObj = invArr.getJSONObject(i)
                val invoice = Invoice(
                    id = invObj.optLong("id", 0L),
                    invoiceNumber = invObj.optString("invoiceNumber", "INV-RESTORED"),
                    invoiceType = invObj.optString("invoiceType", "Tax Invoice"),
                    invoiceDate = invObj.optLong("invoiceDate", System.currentTimeMillis()),
                    dueDate = invObj.optLong("dueDate", System.currentTimeMillis()),
                    poNumber = invObj.optString("poNumber", ""),
                    placeOfSupply = invObj.optString("placeOfSupply", ""),
                    paymentTerms = invObj.optString("paymentTerms", "Net 15"),
                    customerName = invObj.optString("customerName", "Customer"),
                    customerCompany = invObj.optString("customerCompany", ""),
                    customerPhone = invObj.optString("customerPhone", ""),
                    customerEmail = invObj.optString("customerEmail", ""),
                    customerGstin = invObj.optString("customerGstin", ""),
                    customerBillingAddress = invObj.optString("customerBillingAddress", ""),
                    customerShippingAddress = invObj.optString("customerShippingAddress", ""),
                    isB2b = invObj.optBoolean("isB2b", false),
                    status = invObj.optString("status", "Unpaid"),
                    subtotal = invObj.optDouble("subtotal", 0.0),
                    cgstAmount = invObj.optDouble("cgstAmount", 0.0),
                    sgstAmount = invObj.optDouble("sgstAmount", 0.0),
                    igstAmount = invObj.optDouble("igstAmount", 0.0),
                    grandTotal = invObj.optDouble("grandTotal", 0.0),
                    advancePaid = invObj.optDouble("advancePaid", 0.0),
                    balanceDue = invObj.optDouble("balanceDue", 0.0),
                    paymentMethod = invObj.optString("paymentMethod", "UPI"),
                    transactionId = invObj.optString("transactionId", ""),
                    termsAndConditions = invObj.optString("termsAndConditions", ""),
                    notes = invObj.optString("notes", ""),
                    templateName = invObj.optString("templateName", "Modern"),
                    themeColorHex = invObj.optString("themeColorHex", "#1E293B"),
                    currencySymbol = invObj.optString("currencySymbol", "₹"),
                    language = invObj.optString("language", "English")
                )

                val linesList = mutableListOf<InvoiceItemLine>()
                if (invObj.has("lines")) {
                    val linesArr = invObj.getJSONArray("lines")
                    for (j in 0 until linesArr.length()) {
                        val lObj = linesArr.getJSONObject(j)
                        linesList.add(
                            InvoiceItemLine(
                                id = lObj.optLong("id", 0L),
                                invoiceId = lObj.optLong("invoiceId", 0L),
                                itemSNo = lObj.optInt("itemSNo", j + 1),
                                itemName = lObj.optString("itemName", "Item"),
                                itemDescription = lObj.optString("itemDescription", ""),
                                hsnSacCode = lObj.optString("hsnSacCode", ""),
                                quantity = lObj.optDouble("quantity", 1.0),
                                unit = lObj.optString("unit", "pcs"),
                                rate = lObj.optDouble("rate", 0.0),
                                discountPercent = lObj.optDouble("discountPercent", 0.0),
                                taxRatePercent = lObj.optDouble("taxRatePercent", 18.0),
                                lineTotal = lObj.optDouble("lineTotal", 0.0)
                            )
                        )
                    }
                }
                repository.saveInvoice(invoice, linesList)
                invCount++
            }
        }

        return Triple(custCount, prodCount, invCount)
    }
}
