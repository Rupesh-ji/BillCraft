package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.BillBottomNavigation
import com.example.ui.screens.BusinessProfileScreen
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.InvoiceDetailScreen
import com.example.ui.screens.InvoiceEditorScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.BillCraftTheme
import com.example.ui.viewmodel.BillViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BillCraftTheme {
                BillCraftApp()
            }
        }
    }
}

@Composable
fun BillCraftApp() {
    val navController = rememberNavController()
    val viewModel: BillViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "invoices"

    val showBottomBar = currentRoute in listOf("invoices", "customers", "inventory", "reports", "business")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BillBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("invoices") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "invoices",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Dashboard Invoices
            composable("invoices") {
                DashboardScreen(
                    viewModel = viewModel,
                    onCreateInvoice = { type ->
                        navController.navigate("editor?type=$type")
                    },
                    onSelectInvoice = { invoiceId ->
                        navController.navigate("detail/$invoiceId")
                    }
                )
            }

            // Customers Directory
            composable("customers") {
                CustomersScreen(viewModel = viewModel)
            }

            // Product Inventory
            composable("inventory") {
                InventoryScreen(viewModel = viewModel)
            }

            // Reports & Analytics
            composable("reports") {
                ReportsScreen(viewModel = viewModel)
            }

            // Business Profile
            composable("business") {
                BusinessProfileScreen(viewModel = viewModel)
            }

            // Invoice Creator / Editor
            composable(
                route = "editor?type={type}&invoiceId={invoiceId}",
                arguments = listOf(
                    navArgument("type") {
                        type = NavType.StringType
                        defaultValue = "Tax Invoice"
                    },
                    navArgument("invoiceId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type") ?: "Tax Invoice"
                val invoiceIdStr = backStackEntry.arguments?.getString("invoiceId")
                val invoiceId = invoiceIdStr?.toLongOrNull()

                InvoiceEditorScreen(
                    viewModel = viewModel,
                    initialInvoiceType = type,
                    existingInvoiceId = invoiceId,
                    onBack = { navController.popBackStack() },
                    onSavedAndPreview = { newId ->
                        navController.navigate("detail/$newId") {
                            popUpTo("invoices")
                        }
                    }
                )
            }

            // Invoice Detail & PDF Preview
            composable(
                route = "detail/{invoiceId}",
                arguments = listOf(
                    navArgument("invoiceId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val invoiceId = backStackEntry.arguments?.getLong("invoiceId") ?: 0L
                InvoiceDetailScreen(
                    invoiceId = invoiceId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEdit = { id ->
                        navController.navigate("editor?invoiceId=$id")
                    }
                )
            }
        }
    }
}
