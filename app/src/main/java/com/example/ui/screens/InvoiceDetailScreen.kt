package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.StatusBadge
import com.example.ui.theme.EmeraldPaid
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RoseOverdue
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.viewmodel.BillViewModel
import com.example.util.AmountToWordsConverter
import com.example.util.PdfGenerator
import com.example.util.PrinterUtils
import com.example.util.QrCodeUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    invoiceId: Long,
    viewModel: BillViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val context = LocalContext.current
    val profile by viewModel.businessProfile.collectAsStateWithLifecycle()
    val invoiceWithLines by viewModel.selectedInvoiceWithLines.collectAsStateWithLifecycle()

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(invoiceId) {
        viewModel.loadInvoiceDetail(invoiceId)
    }

    val data = invoiceWithLines ?: return

    val inv = data.invoice
    val lines = data.lines

    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice #${inv.invoiceNumber}", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_detail_back")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(inv.id) }, modifier = Modifier.testTag("btn_edit_invoice")) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.testTag("btn_delete_invoice")) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseOverdue)
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val pdfFile = PdfGenerator.generateInvoicePdf(context, data, profile)
                                PrinterUtils.sharePdf(context, pdfFile, "Share Invoice ${inv.invoiceNumber}")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_share_pdf"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share PDF")
                        }

                        Button(
                            onClick = {
                                val pdfFile = PdfGenerator.generateInvoicePdf(context, data, profile)
                                val msg = "Dear ${inv.customerName}, please find attached invoice #${inv.invoiceNumber} for amount ${AmountToWordsConverter.formatCurrency(inv.grandTotal, inv.currencySymbol)}."
                                PrinterUtils.sendWhatsApp(context, pdfFile, inv.customerPhone, msg)
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(46.dp)
                                .testTag("btn_whatsapp_share"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPaid)
                        ) {
                            Text("WhatsApp", fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = {
                                val pdfFile = PdfGenerator.generateInvoicePdf(context, data, profile)
                                PrinterUtils.printPdf(context, pdfFile)
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .testTag("btn_print_pdf")
                        ) {
                            Icon(imageVector = Icons.Default.Print, contentDescription = "Print")
                        }
                    }

                    if (inv.balanceDue > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showPaymentDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_record_payment"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Record Payment (${AmountToWordsConverter.formatCurrency(inv.balanceDue, inv.currencySymbol)} Due)", fontWeight = FontWeight.Bold)
                        }
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
            // Document Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = inv.invoiceType.uppercase(Locale.ROOT),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                                Text(
                                    text = inv.invoiceNumber,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            StatusBadge(status = inv.status)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Billed To:", fontSize = 11.sp, color = Slate700)
                                Text(inv.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (inv.customerCompany.isNotBlank()) {
                                    Text(inv.customerCompany, fontSize = 12.sp, color = Slate700)
                                }
                                if (inv.customerGstin.isNotBlank()) {
                                    Text("GSTIN: ${inv.customerGstin}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Date: ${df.format(Date(inv.invoiceDate))}", fontSize = 12.sp)
                                Text("Due: ${df.format(Date(inv.dueDate))}", fontSize = 12.sp, color = RoseOverdue)
                            }
                        }
                    }
                }
            }

            // High Fidelity Document Preview Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Business Header
                        Text(profile.companyName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate800)
                        Text(profile.streetAddress + ", " + profile.cityStatePincode, fontSize = 11.sp, color = Slate700)
                        if (profile.gstin.isNotBlank()) {
                            Text("GSTIN: ${profile.gstin}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFE2E8F0))
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Table Items
                        Text("ITEMS BREAKDOWN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(8.dp))

                        lines.forEach { line ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(line.itemName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Slate800)
                                    Text("${line.quantity} ${line.unit} × ₹${line.rate} (${line.taxRatePercent}% tax)", fontSize = 11.sp, color = Slate700)
                                }
                                Text(
                                    text = "₹ ${String.format("%.2f", line.lineTotal)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Slate800
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFE2E8F0))
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Total Calculation
                        DetailSummaryRow("Subtotal", "₹ ${String.format("%.2f", inv.subtotal)}")
                        if (!inv.isInterstate) {
                            DetailSummaryRow("CGST", "₹ ${String.format("%.2f", inv.cgstAmount)}")
                            DetailSummaryRow("SGST", "₹ ${String.format("%.2f", inv.sgstAmount)}")
                        } else {
                            DetailSummaryRow("IGST", "₹ ${String.format("%.2f", inv.igstAmount)}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Grand Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate800)
                            Text(
                                text = AmountToWordsConverter.formatCurrency(inv.grandTotal, inv.currencySymbol),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = AmountToWordsConverter.convertToWords(inv.grandTotal, inv.currencySymbol, inv.language),
                            fontSize = 11.sp,
                            color = Slate700
                        )
                    }
                }
            }
        }
    }

    // Record Payment Dialog
    if (showPaymentDialog) {
        var recAmountText by remember { mutableStateOf(inv.balanceDue.toString()) }
        var txnIdText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("Record Payment") },
            text = {
                Column {
                    Text("Total Balance Due: ${AmountToWordsConverter.formatCurrency(inv.balanceDue, inv.currencySymbol)}")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = recAmountText,
                        onValueChange = { recAmountText = it },
                        label = { Text("Payment Received (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = txnIdText,
                        onValueChange = { txnIdText = it },
                        label = { Text("Transaction ID / Reference") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val paidNow = recAmountText.toDoubleOrNull() ?: 0.0
                        val newAdvance = inv.advancePaid + paidNow
                        val newBal = (inv.grandTotal - newAdvance).coerceAtLeast(0.0)
                        val newStatus = if (newBal <= 0) "Paid" else "Partial"

                        viewModel.updatePaymentStatus(inv.id, newStatus, newAdvance, newBal, txnIdText)
                        showPaymentDialog = false
                        Toast.makeText(context, "Payment recorded successfully!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Save Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPaymentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Invoice") },
            text = { Text("Are you sure you want to delete invoice #${inv.invoiceNumber}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteInvoice(inv.id)
                        showDeleteDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseOverdue)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Slate700)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate800)
    }
}
