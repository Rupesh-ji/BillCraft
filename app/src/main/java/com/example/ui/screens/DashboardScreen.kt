package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Invoice
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberPending
import com.example.ui.theme.EmeraldPaid
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.RoseOverdue
import com.example.ui.theme.Slate700
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.BillViewModel
import com.example.util.AmountToWordsConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: BillViewModel,
    onCreateInvoice: (String) -> Unit, // "Tax Invoice" or "Estimate"
    onSelectInvoice: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val allInvoices by viewModel.allInvoices.collectAsStateWithLifecycle()
    val filteredInvoices by viewModel.filteredInvoices.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()

    // Financial Metrics
    val totalSales = allInvoices.sumOf { it.grandTotal }
    val totalPaid = allInvoices.sumOf { it.advancePaid + (if (it.status == "Paid") it.grandTotal - it.advancePaid else 0.0) }
    val totalPending = allInvoices.sumOf { it.balanceDue }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Header Banner
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBlue)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "BillCraft Studio",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Billing & Invoice Center",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onCreateInvoice("Tax Invoice") },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_create_invoice"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = PrimaryBlue
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "New Invoice", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onCreateInvoice("Estimate") },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_create_estimate"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Estimate", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Stat Cards Horizontal Scroll
            item {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        StatCard(
                            title = "Total Billed",
                            value = AmountToWordsConverter.formatCurrency(totalSales),
                            icon = Icons.Default.MonetizationOn,
                            iconBgColor = PrimaryBlue,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                    item {
                        StatCard(
                            title = "Payments Received",
                            value = AmountToWordsConverter.formatCurrency(totalPaid),
                            icon = Icons.Default.CheckCircle,
                            iconBgColor = EmeraldPaid,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                    item {
                        StatCard(
                            title = "Pending Balance",
                            value = AmountToWordsConverter.formatCurrency(totalPending),
                            icon = Icons.Default.HourglassEmpty,
                            iconBgColor = AmberPending,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            }

            // Search Bar & Filter Chips
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_invoice_input"),
                        placeholder = { Text("Search by Invoice #, Customer, Company...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Slate700) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status Filters
                    val filters = listOf("All", "Paid", "Unpaid", "Partial", "Overdue", "Estimate")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters) { f ->
                            val isSelected = statusFilter == f
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateStatusFilter(f) },
                                label = { Text(f, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Invoice List Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Documents (${filteredInvoices.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Invoice List Items
            if (filteredInvoices.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = Slate700.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No invoices found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate700
                        )
                        Text(
                            text = "Try clearing search or create a new invoice.",
                            fontSize = 12.sp,
                            color = Slate700.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                items(filteredInvoices) { invoice ->
                    InvoiceListItem(
                        invoice = invoice,
                        onClick = { onSelectInvoice(invoice.id) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("invoice_item_${invoice.id}")
                    )
                }
            }
        }

        // Floating FAB for instant New Invoice
        FloatingActionButton(
            onClick = { onCreateInvoice("Tax Invoice") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_invoice"),
            containerColor = PrimaryBlue,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Invoice")
        }
    }
}

@Composable
fun InvoiceListItem(
    invoice: Invoice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateStr = df.format(Date(invoice.invoiceDate))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = invoice.invoiceNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = invoice.status)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = invoice.customerName + if (invoice.customerCompany.isNotBlank()) " • ${invoice.customerCompany}" else "",
                    fontSize = 13.sp,
                    color = Slate700
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Date: $dateStr",
                    fontSize = 11.sp,
                    color = Slate700.copy(alpha = 0.7f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = AmountToWordsConverter.formatCurrency(invoice.grandTotal, invoice.currencySymbol),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrimaryBlue
                )
                if (invoice.balanceDue > 0.0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Due: ${AmountToWordsConverter.formatCurrency(invoice.balanceDue, invoice.currencySymbol)}",
                        fontSize = 11.sp,
                        color = RoseOverdue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
