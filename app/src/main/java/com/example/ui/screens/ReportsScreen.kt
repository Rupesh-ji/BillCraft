package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.EmeraldPaid
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RoseOverdue
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.viewmodel.BillViewModel
import com.example.util.AmountToWordsConverter
import java.io.File
import java.io.FileOutputStream

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ReportsScreen(
    viewModel: BillViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val invoices by viewModel.allInvoices.collectAsStateWithLifecycle()

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val jsonStr = inputStream?.bufferedReader().use { it?.readText() } ?: ""
                    if (jsonStr.isNotBlank()) {
                        val result = viewModel.restoreBackupJson(jsonStr)
                        Toast.makeText(
                            context,
                            "Database restored successfully! ${result.first} customers, ${result.second} products, ${result.third} invoices imported.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error restoring backup: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val totalSales = invoices.sumOf { it.grandTotal }
    val totalCgst = invoices.sumOf { it.cgstAmount }
    val totalSgst = invoices.sumOf { it.sgstAmount }
    val totalIgst = invoices.sumOf { it.igstAmount }
    val totalTax = totalCgst + totalSgst + totalIgst

    val totalPaid = invoices.sumOf { it.advancePaid + (if (it.status == "Paid") it.grandTotal - it.advancePaid else 0.0) }
    val totalPending = invoices.sumOf { it.balanceDue }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GST & Sales Analytics", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Text("Comprehensive tax reports & revenue insights for accounting.", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        // GST Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("GST Tax Liability Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)
                    Spacer(modifier = Modifier.height(12.dp))

                    ReportMetricRow("CGST Collected (Central)", AmountToWordsConverter.formatCurrency(totalCgst))
                    ReportMetricRow("SGST Collected (State)", AmountToWordsConverter.formatCurrency(totalSgst))
                    ReportMetricRow("IGST Collected (Interstate)", AmountToWordsConverter.formatCurrency(totalIgst))

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE2E8F0))
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Tax Liability:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate800)
                        Text(
                            text = AmountToWordsConverter.formatCurrency(totalTax),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }

        // Receivables Breakdown Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Collections vs Receivables", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)
                    Spacer(modifier = Modifier.height(12.dp))

                    ReportMetricRow("Total Billed Turnover", AmountToWordsConverter.formatCurrency(totalSales))
                    ReportMetricRow("Total Collected Payment", AmountToWordsConverter.formatCurrency(totalPaid), color = EmeraldPaid)
                    ReportMetricRow("Outstanding Pending Balance", AmountToWordsConverter.formatCurrency(totalPending), color = RoseOverdue)
                }
            }
        }

        // CSV Data Export Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Data Export for Tally / Excel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Export all invoices with complete line items and GST tax breakdown to CSV format.", fontSize = 12.sp, color = Slate700)

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val csvFile = exportInvoicesToCsv(context, invoices)
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", csvFile)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Export Invoices CSV"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_export_csv"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Invoices to CSV", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Cross-Platform Backup & Restore Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cross-Platform Backup & Restore", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Backup and restore full company database across Android and Windows Desktop platforms.", fontSize = 12.sp, color = Slate700)

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val backupJson = viewModel.exportBackupJson()
                                        val cacheFile = File(context.cacheDir, "billcraft_backup_${System.currentTimeMillis()}.json")
                                        cacheFile.writeText(backupJson)
                                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", cacheFile)
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "application/json"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            putExtra(Intent.EXTRA_SUBJECT, "BillCraft Complete Database Backup")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Share / Save Backup JSON"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Backup failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_export_backup"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Backup", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                restoreLauncher.launch("*/*")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_restore_backup"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore DB", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportMetricRow(label: String, value: String, color: Color = Slate800) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = Slate700)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

private fun exportInvoicesToCsv(context: android.content.Context, invoices: List<com.example.data.model.Invoice>): File {
    val file = File(context.cacheDir, "BillCraft_Invoices_Report.csv")
    val fos = FileOutputStream(file)
    val sb = StringBuilder()
    sb.append("Invoice Number,Type,Date,Customer Name,Customer GSTIN,Subtotal,CGST,SGST,IGST,Grand Total,Status\n")

    for (inv in invoices) {
        sb.append("${inv.invoiceNumber},${inv.invoiceType},${inv.invoiceDate},\"${inv.customerName}\",${inv.customerGstin},${inv.subtotal},${inv.cgstAmount},${inv.sgstAmount},${inv.igstAmount},${inv.grandTotal},${inv.status}\n")
    }

    fos.write(sb.toString().toByteArray())
    fos.close()
    return file
}
