package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Customer
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItemLine
import com.example.data.model.ProductItem
import com.example.ui.theme.EmeraldPaid
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.RoseOverdue
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.viewmodel.BillViewModel
import com.example.util.AmountToWordsConverter
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceEditorScreen(
    viewModel: BillViewModel,
    initialInvoiceType: String = "Tax Invoice",
    existingInvoiceId: Long? = null,
    onBack: () -> Unit,
    onSavedAndPreview: (Long) -> Unit
) {
    val customers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val products by viewModel.allProducts.collectAsStateWithLifecycle()
    val profile by viewModel.businessProfile.collectAsStateWithLifecycle()

    var invoiceType by remember { mutableStateOf(initialInvoiceType) }
    var invoiceNumber by remember { mutableStateOf("") }
    var poNumber by remember { mutableStateOf("") }
    var placeOfSupply by remember { mutableStateOf("Maharashtra (27)") }
    
    var invoiceDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var dueDate by remember { mutableLongStateOf(System.currentTimeMillis() + (15L * 24 * 60 * 60 * 1000)) }

    // Customer
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var customerName by remember { mutableStateOf("") }
    var customerCompany by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf("") }
    var customerGstin by remember { mutableStateOf("") }
    var billingAddress by remember { mutableStateOf("") }
    var shippingAddress by remember { mutableStateOf("") }
    var sameAsBilling by remember { mutableStateOf(true) }
    var isB2b by remember { mutableStateOf(false) }

    // Line items
    val lineItems = remember { mutableStateListOf<InvoiceItemLine>() }

    // Taxes & Adjustment
    var isInterstate by remember { mutableStateOf(false) }
    var overallDiscountFlat by remember { mutableDoubleStateOf(0.0) }
    var shippingCharges by remember { mutableDoubleStateOf(0.0) }
    var advancePaid by remember { mutableDoubleStateOf(0.0) }

    // Template Customization
    var selectedTemplate by remember { mutableStateOf("Modern") }
    var themeColorHex by remember { mutableStateOf("#1E293B") }

    var termsText by remember { mutableStateOf(profile.defaultTerms) }

    // Auto-generate invoice number on launch if new
    LaunchedEffect(Unit) {
        if (existingInvoiceId != null) {
            viewModel.loadInvoiceDetail(existingInvoiceId)
            val invWithLines = viewModel.selectedInvoiceWithLines.value
            if (invWithLines != null) {
                val inv = invWithLines.invoice
                invoiceType = inv.invoiceType
                invoiceNumber = inv.invoiceNumber
                poNumber = inv.poNumber
                placeOfSupply = inv.placeOfSupply
                invoiceDate = inv.invoiceDate
                dueDate = inv.dueDate
                customerName = inv.customerName
                customerCompany = inv.customerCompany
                customerPhone = inv.customerPhone
                customerEmail = inv.customerEmail
                customerGstin = inv.customerGstin
                billingAddress = inv.customerBillingAddress
                shippingAddress = inv.customerShippingAddress
                isB2b = inv.isB2b
                isInterstate = inv.isInterstate
                overallDiscountFlat = inv.overallDiscountFlat
                shippingCharges = inv.shippingCharges
                advancePaid = inv.advancePaid
                selectedTemplate = inv.templateName
                themeColorHex = inv.themeColorHex
                termsText = inv.termsAndConditions
                lineItems.clear()
                lineItems.addAll(invWithLines.lines)
            }
        } else {
            invoiceNumber = viewModel.generateNextInvoiceNumber(
                if (invoiceType == "Estimate") "EST-2026-" else "INV-2026-"
            )
            // Add default initial row
            lineItems.add(
                InvoiceItemLine(
                    invoiceId = 0,
                    itemSNo = 1,
                    itemName = "Professional Services / Product",
                    itemDescription = "",
                    hsnSacCode = "9983",
                    quantity = 1.0,
                    unit = "pcs",
                    rate = 1000.0,
                    discountPercent = 0.0,
                    taxRatePercent = 18.0,
                    lineTotal = 1000.0
                )
            )
        }
    }

    // Dynamic Calculations
    val subtotal = lineItems.sumOf { it.quantity * it.rate }
    val itemDiscountTotal = lineItems.sumOf { (it.quantity * it.rate) * (it.discountPercent / 100.0) }
    val taxableBase = (subtotal - itemDiscountTotal - overallDiscountFlat).coerceAtLeast(0.0)

    val totalTaxAmount = lineItems.sumOf { line ->
        val lineBase = (line.quantity * line.rate * (1 - line.discountPercent / 100.0))
        lineBase * (line.taxRatePercent / 100.0)
    }

    val cgstAmount = if (!isInterstate) totalTaxAmount / 2.0 else 0.0
    val sgstAmount = if (!isInterstate) totalTaxAmount / 2.0 else 0.0
    val igstAmount = if (isInterstate) totalTaxAmount else 0.0

    val exactTotal = taxableBase + totalTaxAmount + shippingCharges
    val grandTotal = round(exactTotal)
    val roundOff = grandTotal - exactTotal
    val balanceDue = (grandTotal - advancePaid).coerceAtLeast(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingInvoiceId != null) "Edit $invoiceNumber" else "Create $invoiceType",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_editor_back")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val inv = buildInvoiceObject(
                                existingInvoiceId ?: 0, invoiceNumber, invoiceType, invoiceDate, dueDate,
                                poNumber, placeOfSupply, customerName, customerCompany, customerPhone, customerEmail,
                                customerGstin, billingAddress, if (sameAsBilling) billingAddress else shippingAddress,
                                isB2b, subtotal, itemDiscountTotal, overallDiscountFlat, isInterstate,
                                cgstAmount, sgstAmount, igstAmount, shippingCharges, roundOff, grandTotal,
                                advancePaid, balanceDue, selectedTemplate, themeColorHex, termsText, profile
                            )
                            viewModel.saveFullInvoice(inv, lineItems) { id ->
                                onSavedAndPreview(id)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("btn_save_and_preview"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & View PDF", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. INVOICE META & TYPE CARD
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Document Type", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        val types = listOf("Tax Invoice", "Proforma", "Estimate", "Credit Note", "Debit Note")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(types) { t ->
                                FilterChip(
                                    selected = invoiceType == t,
                                    onClick = { invoiceType = t },
                                    label = { Text(t, fontSize = 12.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = invoiceNumber,
                                onValueChange = { invoiceNumber = it },
                                label = { Text("Invoice Number") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_invoice_number"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = poNumber,
                                onValueChange = { poNumber = it },
                                label = { Text("P.O. Number (Opt)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        // Quick Due Date Selection
                        Text("Due Date Options", fontSize = 12.sp, color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(7, 15, 30).forEach { days ->
                                OutlinedButton(
                                    onClick = { dueDate = invoiceDate + (days * 24L * 60 * 60 * 1000) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+$days Days", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 2. CUSTOMER / CLIENT SECTION
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Client / Billed To", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("B2B Invoice", fontSize = 12.sp, color = Slate700)
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = isB2b,
                                    onCheckedChange = { isB2b = it },
                                    modifier = Modifier.testTag("switch_b2b")
                                )
                            }
                        }

                        // Autocomplete selector from database
                        if (customers.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Select Saved Customer:", fontSize = 12.sp, color = Slate700)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(customers) { c ->
                                    FilterChip(
                                        selected = selectedCustomer?.id == c.id,
                                        onClick = {
                                            selectedCustomer = c
                                            customerName = c.name
                                            customerCompany = c.companyName
                                            customerPhone = c.phone
                                            customerEmail = c.email
                                            customerGstin = c.gstin
                                            billingAddress = c.billingAddress
                                            shippingAddress = c.shippingAddress
                                            isB2b = c.isB2b
                                        },
                                        label = { Text(c.name) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Customer / Contact Name *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_customer_name"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = customerCompany,
                                onValueChange = { customerCompany = it },
                                label = { Text("Company Name") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = { customerPhone = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                            )
                        }

                        if (isB2b) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customerGstin,
                                onValueChange = { customerGstin = it },
                                label = { Text("Customer GSTIN Number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = billingAddress,
                            onValueChange = { billingAddress = it },
                            label = { Text("Billing Address") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = sameAsBilling, onCheckedChange = { sameAsBilling = it })
                            Text("Shipping Address same as Billing", fontSize = 12.sp)
                        }

                        if (!sameAsBilling) {
                            OutlinedTextField(
                                value = shippingAddress,
                                onValueChange = { shippingAddress = it },
                                label = { Text("Shipping Address") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }
                    }
                }
            }

            // 3. PRODUCTS & ITEMS TABLE
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Line Items & Services (${lineItems.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)

                            // Add Item Button
                            IconButton(
                                onClick = {
                                    lineItems.add(
                                        InvoiceItemLine(
                                            invoiceId = 0,
                                            itemSNo = lineItems.size + 1,
                                            itemName = "",
                                            quantity = 1.0,
                                            unit = "pcs",
                                            rate = 0.0,
                                            taxRatePercent = 18.0
                                        )
                                    )
                                },
                                modifier = Modifier.testTag("btn_add_line_item")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item", tint = PrimaryBlue)
                            }
                        }

                        // Quick Inventory Item Picker
                        if (products.isNotEmpty()) {
                            Text("Quick Add from Catalog:", fontSize = 11.sp, color = Slate700)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(products) { prod ->
                                    FilterChip(
                                        selected = false,
                                        onClick = {
                                            lineItems.add(
                                                InvoiceItemLine(
                                                    invoiceId = 0,
                                                    itemSNo = lineItems.size + 1,
                                                    itemName = prod.name,
                                                    itemDescription = prod.description,
                                                    hsnSacCode = prod.hsnSacCode,
                                                    quantity = 1.0,
                                                    unit = prod.unit,
                                                    rate = prod.unitPrice,
                                                    taxRatePercent = prod.taxRatePercentage,
                                                    lineTotal = prod.unitPrice
                                                )
                                            )
                                        },
                                        label = { Text("+ ${prod.name}") }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Line items list
                        lineItems.forEachIndexed { index, line ->
                            LineItemRow(
                                index = index,
                                line = line,
                                onUpdate = { updatedLine ->
                                    val calcTotal = (updatedLine.quantity * updatedLine.rate) * (1 - updatedLine.discountPercent / 100.0)
                                    lineItems[index] = updatedLine.copy(lineTotal = calcTotal)
                                },
                                onDelete = {
                                    if (lineItems.size > 1) {
                                        lineItems.removeAt(index)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            // 4. TAX & CALCULATIONS CONFIG
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tax & Adjustments", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Interstate IGST Supply", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text("Toggle ON for IGST, OFF for CGST + SGST", fontSize = 11.sp, color = Slate700)
                            }
                            Switch(
                                checked = isInterstate,
                                onCheckedChange = { isInterstate = it },
                                modifier = Modifier.testTag("switch_interstate")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = if (overallDiscountFlat == 0.0) "" else overallDiscountFlat.toString(),
                                onValueChange = { overallDiscountFlat = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("Flat Discount (₹)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = if (shippingCharges == 0.0) "" else shippingCharges.toString(),
                                onValueChange = { shippingCharges = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("Shipping (₹)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = if (advancePaid == 0.0) "" else advancePaid.toString(),
                            onValueChange = { advancePaid = it.toDoubleOrNull() ?: 0.0 },
                            label = { Text("Advance Amount Received (₹)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            }

            // 5. AUTO CALCULATED TOTALS PREVIEW
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate800)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Invoice Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))

                        SummaryRow("Subtotal:", AmountToWordsConverter.formatCurrency(subtotal))
                        if (itemDiscountTotal + overallDiscountFlat > 0) {
                            SummaryRow("Total Discount:", "- ${AmountToWordsConverter.formatCurrency(itemDiscountTotal + overallDiscountFlat)}")
                        }

                        if (!isInterstate) {
                            SummaryRow("CGST (9% avg):", AmountToWordsConverter.formatCurrency(cgstAmount))
                            SummaryRow("SGST (9% avg):", AmountToWordsConverter.formatCurrency(sgstAmount))
                        } else {
                            SummaryRow("IGST (18% avg):", AmountToWordsConverter.formatCurrency(igstAmount))
                        }

                        if (shippingCharges > 0) {
                            SummaryRow("Shipping Charges:", AmountToWordsConverter.formatCurrency(shippingCharges))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.2f))
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Grand Total:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                text = AmountToWordsConverter.formatCurrency(grandTotal),
                                color = EmeraldPaid,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        if (advancePaid > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            SummaryRow("Balance Due:", AmountToWordsConverter.formatCurrency(balanceDue), color = RoseOverdue)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = AmountToWordsConverter.convertToWords(grandTotal),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 6. STYLING & THEME SELECTOR
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("PDF Template Design", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(8.dp))

                        val templates = listOf("Modern", "Classic", "Minimal", "Compact")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            templates.forEach { temp ->
                                FilterChip(
                                    selected = selectedTemplate == temp,
                                    onClick = { selectedTemplate = temp },
                                    label = { Text(temp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Header Accent Color", fontSize = 12.sp, color = Slate700)
                        Spacer(modifier = Modifier.height(6.dp))
                        val colors = listOf("#1E293B", "#2563EB", "#0D9488", "#7C3AED", "#10B981")
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            colors.forEach { cHex ->
                                val parsed = try { Color(android.graphics.Color.parseColor(cHex)) } catch (e: Exception) { PrimaryBlue }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(parsed)
                                        .clickable { themeColorHex = cHex }
                                        .border(
                                            width = if (themeColorHex == cHex) 3.dp else 0.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LineItemRow(
    index: Int,
    line: InvoiceItemLine,
    onUpdate: (InvoiceItemLine) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate100),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Item #${index + 1}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Item", tint = RoseOverdue)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = line.itemName,
                onValueChange = { onUpdate(line.copy(itemName = it)) },
                label = { Text("Item / Service Description *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = line.hsnSacCode,
                    onValueChange = { onUpdate(line.copy(hsnSacCode = it)) },
                    label = { Text("HSN/SAC") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = if (line.quantity == 0.0) "" else line.quantity.toString(),
                    onValueChange = { onUpdate(line.copy(quantity = it.toDoubleOrNull() ?: 1.0)) },
                    label = { Text("Qty") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = if (line.rate == 0.0) "" else line.rate.toString(),
                    onValueChange = { onUpdate(line.copy(rate = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Rate (₹)") },
                    modifier = Modifier.weight(1.2f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = if (line.taxRatePercent == 0.0) "" else line.taxRatePercent.toString(),
                    onValueChange = { onUpdate(line.copy(taxRatePercent = it.toDoubleOrNull() ?: 18.0)) },
                    label = { Text("Tax %") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = if (line.discountPercent == 0.0) "" else line.discountPercent.toString(),
                    onValueChange = { onUpdate(line.copy(discountPercent = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Disc %") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "₹ ${String.format("%.2f", line.lineTotal)}",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, color: Color = Color.White) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        Text(value, color = color, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}

private fun buildInvoiceObject(
    id: Long,
    number: String,
    type: String,
    date: Long,
    due: Long,
    po: String,
    place: String,
    cName: String,
    cCompany: String,
    cPhone: String,
    cEmail: String,
    cGstin: String,
    billing: String,
    shipping: String,
    isB2b: Boolean,
    subtotal: Double,
    itemDisc: Double,
    overallDisc: Double,
    isInterstate: Boolean,
    cgst: Double,
    sgst: Double,
    igst: Double,
    shippingChg: Double,
    roundOff: Double,
    grandTotal: Double,
    advance: Double,
    balance: Double,
    template: String,
    themeColor: String,
    terms: String,
    profile: com.example.data.model.BusinessProfile
): Invoice {
    val status = when {
        balance <= 0.0 -> "Paid"
        advance > 0.0 -> "Partial"
        due < System.currentTimeMillis() -> "Overdue"
        else -> "Unpaid"
    }

    return Invoice(
        id = id,
        invoiceNumber = number,
        invoiceType = type,
        invoiceDate = date,
        dueDate = due,
        poNumber = po,
        placeOfSupply = place,
        customerName = cName.ifBlank { "Cash Customer" },
        customerCompany = cCompany,
        customerPhone = cPhone,
        customerEmail = cEmail,
        customerGstin = cGstin,
        customerBillingAddress = billing,
        customerShippingAddress = shipping,
        isB2b = isB2b,
        status = status,
        subtotal = subtotal,
        itemDiscountTotal = itemDisc,
        overallDiscountFlat = overallDisc,
        isInterstate = isInterstate,
        cgstAmount = cgst,
        sgstAmount = sgst,
        igstAmount = igst,
        shippingCharges = shippingChg,
        roundOff = roundOff,
        grandTotal = grandTotal,
        advancePaid = advance,
        balanceDue = balance,
        templateName = template,
        themeColorHex = themeColor,
        termsAndConditions = terms
    )
}
