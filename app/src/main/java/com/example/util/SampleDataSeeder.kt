package com.example.util

import com.example.data.model.BusinessProfile
import com.example.data.model.Customer
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItemLine
import com.example.data.model.ProductItem
import com.example.data.repository.BillRepository

object SampleDataSeeder {

    suspend fun seedSampleDataIfEmpty(repository: BillRepository) {
        if (repository.getInvoiceCount() > 0) return

        // 1. Business Profile
        val profile = BusinessProfile(
            companyName = "Apex Digital Solutions",
            tagline = "IT Hardware & Professional Software Services",
            streetAddress = "Suite 402, Cyber Tower, Bandra Kurla Complex",
            cityStatePincode = "Mumbai, Maharashtra - 400051",
            phoneNumbers = "+91 98200 12345 / +91 22 6789 0000",
            email = "accounts@apexdigital.in",
            website = "www.apexdigital.in",
            gstin = "27AAACA1234A1Z1",
            panNumber = "AAACA1234A",
            businessType = "Service & Retail",
            bankAccountName = "Apex Digital Solutions Current A/C",
            bankAccountNumber = "50200012345678",
            bankIfsc = "HDFC0000123",
            bankNameBranch = "HDFC Bank, BKC Branch",
            upiId = "apexdigital@hdfcbank",
            defaultTerms = "1. Payment is due within 15 days from the date of invoice.\n2. Interest @ 18% p.a. will be charged on overdue payments.\n3. Goods once sold are subject to standard manufacturer warranty.",
            defaultNotes = "Thank you for partnering with Apex Digital Solutions!"
        )
        repository.saveBusinessProfile(profile)

        // 2. Customers
        val cust1Id = repository.saveCustomer(
            Customer(
                name = "Rahul Sharma",
                companyName = "Nexus Enterprises",
                email = "rahul@nexus.co.in",
                phone = "+91 98111 22233",
                billingAddress = "12 Industrial Estate, Andheri East\nMumbai - 400069",
                shippingAddress = "12 Industrial Estate, Andheri East\nMumbai - 400069",
                gstin = "27BBBNE9876B1Z9",
                isB2b = true
            )
        )

        val cust2Id = repository.saveCustomer(
            Customer(
                name = "Priya Mehta",
                companyName = "Creative Pixels Studio",
                email = "priya@creativepixels.com",
                phone = "+91 99300 44556",
                billingAddress = "Flat 801, Sea View Apartments, Worli\nMumbai - 400018",
                shippingAddress = "Flat 801, Sea View Apartments, Worli\nMumbai - 400018",
                gstin = "",
                isB2b = false
            )
        )

        val cust3Id = repository.saveCustomer(
            Customer(
                name = "Vikram Patel",
                companyName = "Greenline Logistics",
                email = "billing@greenline.com",
                phone = "+91 97222 33445",
                billingAddress = "45 Freight Zone, Sector 18\nAhmedabad, Gujarat - 380009",
                shippingAddress = "45 Freight Zone, Sector 18\nAhmedabad, Gujarat - 380009",
                gstin = "24CCCPG5432C1Z4",
                isB2b = true
            )
        )

        // 3. Products
        val prod1 = repository.saveProduct(
            ProductItem(
                name = "Dell UltraSharp 27\" 4K Monitor",
                description = "27-inch 4K UHD IPS Monitor with USB-C Hub",
                hsnSacCode = "84716060",
                unitPrice = 38500.0,
                unit = "pcs",
                taxRatePercentage = 18.0,
                stockQuantity = 25,
                category = "Hardware"
            )
        )

        val prod2 = repository.saveProduct(
            ProductItem(
                name = "Logitech MX Master 3S Wireless Mouse",
                description = "Ergonomic Performance Bluetooth Mouse",
                hsnSacCode = "84716060",
                unitPrice = 8490.0,
                unit = "pcs",
                taxRatePercentage = 18.0,
                stockQuantity = 50,
                category = "Accessories"
            )
        )

        val prod3 = repository.saveProduct(
            ProductItem(
                name = "Custom Enterprise Software Maintenance",
                description = "Quarterly Cloud Infrastructure & Security Maintenance",
                hsnSacCode = "998313",
                unitPrice = 25000.0,
                unit = "hrs",
                taxRatePercentage = 18.0,
                stockQuantity = 999,
                category = "Services"
            )
        )

        val prod4 = repository.saveProduct(
            ProductItem(
                name = "Mechanical RGB Ergonomic Keyboard",
                description = "Hot-swappable Mechanical Switches Keyboard",
                hsnSacCode = "84716060",
                unitPrice = 6200.0,
                unit = "pcs",
                taxRatePercentage = 18.0,
                stockQuantity = 30,
                category = "Accessories"
            )
        )

        // 4. Sample Invoices
        // Invoice 1: Paid B2B
        val inv1Lines = listOf(
            InvoiceItemLine(
                invoiceId = 0,
                itemSNo = 1,
                itemName = "Dell UltraSharp 27\" 4K Monitor",
                itemDescription = "27-inch 4K UHD IPS Monitor",
                hsnSacCode = "84716060",
                quantity = 2.0,
                unit = "pcs",
                rate = 38500.0,
                discountPercent = 5.0,
                taxRatePercent = 18.0,
                lineTotal = 73150.0
            ),
            InvoiceItemLine(
                invoiceId = 0,
                itemSNo = 2,
                itemName = "Logitech MX Master 3S Wireless Mouse",
                itemDescription = "Bluetooth Ergonomic Mouse",
                hsnSacCode = "84716060",
                quantity = 2.0,
                unit = "pcs",
                rate = 8490.0,
                discountPercent = 0.0,
                taxRatePercent = 18.0,
                lineTotal = 16980.0
            )
        )
        val inv1Subtotal = 90130.0
        val inv1Cgst = inv1Subtotal * 0.09
        val inv1Sgst = inv1Subtotal * 0.09
        val inv1Grand = inv1Subtotal + inv1Cgst + inv1Sgst

        val inv1 = Invoice(
            invoiceNumber = "INV-2026-001",
            invoiceType = "Tax Invoice",
            invoiceDate = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000),
            dueDate = System.currentTimeMillis() + (8L * 24 * 60 * 60 * 1000),
            poNumber = "PO-NEXUS-881",
            placeOfSupply = "Maharashtra (27)",
            customerId = cust1Id,
            customerName = "Rahul Sharma",
            customerCompany = "Nexus Enterprises",
            customerPhone = "+91 98111 22233",
            customerEmail = "rahul@nexus.co.in",
            customerGstin = "27BBBNE9876B1Z9",
            customerBillingAddress = "12 Industrial Estate, Andheri East\nMumbai - 400069",
            customerShippingAddress = "12 Industrial Estate, Andheri East\nMumbai - 400069",
            isB2b = true,
            status = "Paid",
            subtotal = inv1Subtotal,
            isInterstate = false,
            cgstAmount = inv1Cgst,
            sgstAmount = inv1Sgst,
            grandTotal = inv1Grand,
            advancePaid = inv1Grand,
            balanceDue = 0.0,
            paymentMethod = "UPI",
            transactionId = "UPI/2026/091283",
            paymentDate = System.currentTimeMillis() - (6L * 24 * 60 * 60 * 1000),
            templateName = "Modern",
            themeColorHex = "#1E293B"
        )
        repository.saveInvoice(inv1, inv1Lines)

        // Invoice 2: Unpaid B2C
        val inv2Lines = listOf(
            InvoiceItemLine(
                invoiceId = 0,
                itemSNo = 1,
                itemName = "Custom Enterprise Software Maintenance",
                itemDescription = "Quarterly Cloud & Security Audit",
                hsnSacCode = "998313",
                quantity = 1.0,
                unit = "hrs",
                rate = 25000.0,
                discountPercent = 0.0,
                taxRatePercent = 18.0,
                lineTotal = 25000.0
            )
        )
        val inv2Subtotal = 25000.0
        val inv2Cgst = inv2Subtotal * 0.09
        val inv2Sgst = inv2Subtotal * 0.09
        val inv2Grand = inv2Subtotal + inv2Cgst + inv2Sgst

        val inv2 = Invoice(
            invoiceNumber = "INV-2026-002",
            invoiceType = "Tax Invoice",
            invoiceDate = System.currentTimeMillis() - (2L * 24 * 60 * 60 * 1000),
            dueDate = System.currentTimeMillis() + (13L * 24 * 60 * 60 * 1000),
            poNumber = "",
            placeOfSupply = "Maharashtra (27)",
            customerId = cust2Id,
            customerName = "Priya Mehta",
            customerCompany = "Creative Pixels Studio",
            customerPhone = "+91 99300 44556",
            customerEmail = "priya@creativepixels.com",
            customerGstin = "",
            customerBillingAddress = "Flat 801, Sea View Apartments, Worli\nMumbai - 400018",
            customerShippingAddress = "Flat 801, Sea View Apartments, Worli\nMumbai - 400018",
            isB2b = false,
            status = "Unpaid",
            subtotal = inv2Subtotal,
            isInterstate = false,
            cgstAmount = inv2Cgst,
            sgstAmount = inv2Sgst,
            grandTotal = inv2Grand,
            advancePaid = 0.0,
            balanceDue = inv2Grand,
            templateName = "Classic",
            themeColorHex = "#0284C7"
        )
        repository.saveInvoice(inv2, inv2Lines)

        // Invoice 3: Interstate IGST Partial Payment
        val inv3Lines = listOf(
            InvoiceItemLine(
                invoiceId = 0,
                itemSNo = 1,
                itemName = "Dell UltraSharp 27\" 4K Monitor",
                itemDescription = "27-inch 4K UHD IPS Monitor",
                hsnSacCode = "84716060",
                quantity = 1.0,
                unit = "pcs",
                rate = 38500.0,
                discountPercent = 0.0,
                taxRatePercent = 18.0,
                lineTotal = 38500.0
            ),
            InvoiceItemLine(
                invoiceId = 0,
                itemSNo = 2,
                itemName = "Mechanical RGB Ergonomic Keyboard",
                itemDescription = "Mechanical Hot-Swappable Keyboard",
                hsnSacCode = "84716060",
                quantity = 2.0,
                unit = "pcs",
                rate = 6200.0,
                discountPercent = 0.0,
                taxRatePercent = 18.0,
                lineTotal = 12400.0
            )
        )
        val inv3Subtotal = 50900.0
        val inv3Igst = inv3Subtotal * 0.18
        val inv3Grand = inv3Subtotal + inv3Igst
        val inv3Advance = 25000.0
        val inv3Bal = inv3Grand - inv3Advance

        val inv3 = Invoice(
            invoiceNumber = "INV-2026-003",
            invoiceType = "Tax Invoice",
            invoiceDate = System.currentTimeMillis() - (1L * 24 * 60 * 60 * 1000),
            dueDate = System.currentTimeMillis() + (14L * 24 * 60 * 60 * 1000),
            poNumber = "GL-PO-5510",
            placeOfSupply = "Gujarat (24)",
            customerId = cust3Id,
            customerName = "Vikram Patel",
            customerCompany = "Greenline Logistics",
            customerPhone = "+91 97222 33445",
            customerEmail = "billing@greenline.com",
            customerGstin = "24CCCPG5432C1Z4",
            customerBillingAddress = "45 Freight Zone, Sector 18\nAhmedabad, Gujarat - 380009",
            customerShippingAddress = "45 Freight Zone, Sector 18\nAhmedabad, Gujarat - 380009",
            isB2b = true,
            status = "Partial",
            subtotal = inv3Subtotal,
            isInterstate = true,
            igstAmount = inv3Igst,
            grandTotal = inv3Grand,
            advancePaid = inv3Advance,
            balanceDue = inv3Bal,
            paymentMethod = "Bank Transfer",
            transactionId = "NEFT/HDFC/0019283",
            paymentDate = System.currentTimeMillis() - (1L * 24 * 60 * 60 * 1000),
            templateName = "Minimal",
            themeColorHex = "#0D9488"
        )
        repository.saveInvoice(inv3, inv3Lines)
    }
}
