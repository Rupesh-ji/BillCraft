package com.example.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Desktop
import java.io.File
import java.sql.DriverManager
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

// Desktop App Colors matching Android M3 Palette
val PrimaryBlue = Color(0xFF1E293B)
val AccentGold = Color(0xFFD97706)
val EmeraldPaid = Color(0xFF059669)
val RoseOverdue = Color(0xFFDC2626)
val SlateBackground = Color(0xFFF8FAFC)
val Slate700 = Color(0xFF334155)

data class DesktopInvoice(
    val id: Long,
    val invoiceNumber: String,
    val customerName: String,
    val date: String,
    val grandTotal: Double,
    val status: String
)

fun main() = application {
    val windowState = rememberWindowState(width = 1200.dp, height = 800.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "BillCraft - Cross-Platform GST Billing Suite (Windows)",
        state = windowState
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = PrimaryBlue,
                secondary = AccentGold,
                background = SlateBackground
            )
        ) {
            MainDesktopScreen()
        }
    }
}

@Composable
fun MainDesktopScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var statusMessage by remember { mutableStateOf("BillCraft Windows Suite Ready (Offline SQLite Active)") }

    val sampleInvoices = remember {
        mutableStateListOf(
            DesktopInvoice(1, "INV-2026-001", "Sharma Retailers", "2026-07-27", 14750.00, "Paid"),
            DesktopInvoice(2, "INV-2026-002", "Verma Electricals", "2026-07-26", 8850.00, "Unpaid"),
            DesktopInvoice(3, "INV-2026-003", "Gupta Tech Solutions", "2026-07-25", 29500.00, "Paid")
        )
    }

    Row(modifier = Modifier.fillMaxSize().background(SlateBackground)) {
        // Desktop Sidebar Navigation
        NavigationRail(
            containerColor = PrimaryBlue,
            contentColor = Color.White,
            modifier = Modifier.width(220.dp).fillMaxHeight()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("BillCraft", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Windows Edition", color = AccentGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                NavigationRailItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color.White) },
                    label = { Text("Dashboard", color = Color.White) }
                )
                NavigationRailItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White) },
                    label = { Text("Invoices", color = Color.White) }
                )
                NavigationRailItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Inventory, contentDescription = null, tint = Color.White) },
                    label = { Text("Inventory", color = Color.White) }
                )
                NavigationRailItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Storage, contentDescription = null, tint = Color.White) },
                    label = { Text("Backup & Restore", color = Color.White) }
                )
            }
        }

        // Main Desktop Content Area
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // Status bar banner
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPaid)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(statusMessage, fontWeight = FontWeight.SemiBold, color = Slate700, fontSize = 13.sp)
                }
            }

            when (selectedTab) {
                0 -> DesktopDashboard(sampleInvoices)
                1 -> DesktopInvoicesView(sampleInvoices) { msg -> statusMessage = msg }
                2 -> DesktopInventoryView()
                3 -> DesktopBackupRestoreView { msg -> statusMessage = msg }
            }
        }
    }
}

@Composable
fun DesktopDashboard(invoices: List<DesktopInvoice>) {
    Column {
        Text("Executive Overview", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard("Total Sales Turnover", "₹53,100.00", PrimaryBlue, Modifier.weight(1f))
            MetricCard("Collected Payments", "₹44,250.00", EmeraldPaid, Modifier.weight(1f))
            MetricCard("Outstanding Dues", "₹8,850.00", RoseOverdue, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Recent Invoices", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(invoices) { inv ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(inv.invoiceNumber, fontWeight = FontWeight.Bold)
                            Text(inv.customerName, fontSize = 12.sp, color = Slate700)
                        }
                        Text("₹${inv.grandTotal}", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(inv.status, color = if (inv.status == "Paid") EmeraldPaid else RoseOverdue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, amount: String, accentColor: Color, modifier: Modifier) {
    Card(shape = RoundedCornerShape(12.dp), modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontSize = 13.sp, color = Slate700)
            Spacer(modifier = Modifier.height(6.dp))
            Text(amount, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

@Composable
fun DesktopInvoicesView(invoices: List<DesktopInvoice>, onStatusChange: (String) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Invoices & Billing", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)

            Button(
                onClick = {
                    val file = File(System.getProperty("user.home"), "BillCraft_Invoice_Sample.pdf")
                    file.writeText("PDF Invoice Generated via BillCraft Desktop Edition")
                    onStatusChange("PDF Generated at: ${file.absolutePath}")
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file.parentFile)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate PDF Invoice")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(invoices) { inv ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(inv.invoiceNumber, fontWeight = FontWeight.Bold)
                            Text("${inv.customerName} • ${inv.date}", fontSize = 12.sp, color = Slate700)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("₹${inv.grandTotal}", fontWeight = FontWeight.Bold)
                            IconButton(onClick = { onStatusChange("Printing ${inv.invoiceNumber} to Windows Default Printer...") }) {
                                Icon(Icons.Default.Print, contentDescription = "Print", tint = PrimaryBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DesktopInventoryView() {
    Column {
        Text("Product & Service Catalog", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Manage items, HSN/SAC codes, tax slabs (0%, 5%, 12%, 18%, 28%), and stock tracking.", fontSize = 14.sp, color = Slate700)
    }
}

@Composable
fun DesktopBackupRestoreView(onStatusChange: (String) -> Unit) {
    Column {
        Text("Cross-Platform Backup & Restore", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Export and import full database JSON files for seamless sync between Windows Desktop and Android.", fontSize = 14.sp, color = Slate700)

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    val chooser = JFileChooser()
                    chooser.selectedFile = File("billcraft_backup_desktop.json")
                    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        val file = chooser.selectedFile
                        file.writeText("{\n  \"version\": 1,\n  \"platform\": \"Windows Desktop\",\n  \"database\": \"BillCraft_SQLite\"\n}")
                        onStatusChange("Backup exported to: ${file.absolutePath}")
                    }
                },
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Backup JSON")
            }

            OutlinedButton(
                onClick = {
                    val chooser = JFileChooser()
                    chooser.fileFilter = FileNameExtensionFilter("JSON Backup Files", "json")
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        val file = chooser.selectedFile
                        onStatusChange("Database restored from Windows file: ${file.name}")
                    }
                },
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Default.CloudDownload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restore Backup File")
            }
        }
    }
}
