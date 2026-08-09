package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.AppDatabase
import com.example.data.repository.MotoRepository
import com.example.data.entities.MaintenanceRecord
import com.example.ui.components.AddBikeDialog
import com.example.ui.components.AddListingDialog
import com.example.ui.components.AddMaintenanceDialog
import com.example.ui.components.AddMaterialDialog
import com.example.ui.components.AddModDialog
import com.example.ui.components.AddProjectDialog
import com.example.ui.components.AddReminderDialog
import com.example.ui.components.GlobalSearchBarCard
import com.example.util.InvoiceGeneratorDialog
import com.example.util.OnlinePaymentCheckoutDialog
import com.example.ui.components.MotoBottomNavBar
import com.example.ui.components.MotoTopBar
import com.example.ui.components.NavTab
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MaintenanceScreen
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.ModsScreen
import com.example.ui.screens.SeatCraftScreen
import com.example.ui.theme.CarbonDark
import com.example.ui.theme.MotoCraftTheme
import com.example.ui.viewmodel.MotoViewModel
import com.example.ui.viewmodel.MotoViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MotoViewModel by viewModels {
        val database = AppDatabase.getDatabase(this)
        val repository = MotoRepository(database.motoDao(), database.seatOrderDao())
        MotoViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MotoCraftTheme {
                MotoAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MotoAppContent(viewModel: MotoViewModel) {
    var selectedTab by remember { mutableStateOf(NavTab.DASHBOARD) }

    // Dialog state controllers
    var showAddBikeDialog by remember { mutableStateOf(false) }
    var showAddModDialog by remember { mutableStateOf(false) }
    var showAddListingDialog by remember { mutableStateOf(false) }
    var showAddMaintDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var showAddMaterialDialog by remember { mutableStateOf(false) }
    var showAddProjectDialog by remember { mutableStateOf(false) }
    var showInvoiceDialog by remember { mutableStateOf(false) }
    var selectedRecordForInvoice by remember { mutableStateOf<MaintenanceRecord?>(null) }
    var showOnlinePaymentDialog by remember { mutableStateOf(false) }
    var selectedRecordForPayment by remember { mutableStateOf<MaintenanceRecord?>(null) }
    var globalSearchQuery by remember { mutableStateOf("") }

    // Reactive database states
    val motorcycles by viewModel.motorcycles.collectAsStateWithLifecycle()
    val selectedBike by viewModel.selectedMotorcycle.collectAsStateWithLifecycle()
    val mods by viewModel.modifications.collectAsStateWithLifecycle()
    val marketplaceItems by viewModel.marketplaceItems.collectAsStateWithLifecycle()
    val maintenanceRecords by viewModel.maintenanceRecords.collectAsStateWithLifecycle()
    val serviceReminders by viewModel.serviceReminders.collectAsStateWithLifecycle()
    val seatMaterials by viewModel.seatMaterials.collectAsStateWithLifecycle()
    val buildProjects by viewModel.buildProjects.collectAsStateWithLifecycle()
    val budgetStats by viewModel.budgetStats.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MotoTopBar(
                motorcycles = motorcycles,
                selectedBike = selectedBike,
                onBikeSelected = { bikeId -> viewModel.selectBike(bikeId) },
                onAddBikeClicked = { showAddBikeDialog = true }
            )
        },
        bottomBar = {
            MotoBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = CarbonDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CarbonDark)
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                GlobalSearchBarCard(
                    query = globalSearchQuery,
                    onQueryChange = { globalSearchQuery = it },
                    buildProjects = buildProjects,
                    modifications = mods,
                    marketplaceItems = marketplaceItems,
                    maintenanceRecords = maintenanceRecords,
                    onBuyMarketplaceItem = { item ->
                        selectedRecordForPayment = MaintenanceRecord(
                            serviceType = item.title,
                            cost = item.price,
                            mileage = 0,
                            description = "Marketplace purchase: ${item.description}"
                        )
                        showOnlinePaymentDialog = true
                    },
                    onPayMaintenanceRecord = { record ->
                        selectedRecordForPayment = record
                        showOnlinePaymentDialog = true
                    },
                    onGenerateInvoiceRecord = { record ->
                        selectedRecordForInvoice = record
                        showInvoiceDialog = true
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                when (selectedTab) {
                NavTab.DASHBOARD -> DashboardScreen(
                    motorcycle = selectedBike,
                    stats = budgetStats,
                    buildProjects = buildProjects,
                    maintenanceRecords = maintenanceRecords,
                    modifications = mods,
                    onAddProjectClicked = { showAddProjectDialog = true },
                    onAddModClicked = { showAddModDialog = true }
                )

                NavTab.MODS -> ModsScreen(
                    modifications = mods,
                    motorcycle = selectedBike,
                    onAddModClicked = { showAddModDialog = true },
                    onUpdateStatus = { mod, newStatus -> viewModel.updateModStatus(mod, newStatus) },
                    onDeleteMod = { mod -> viewModel.deleteModification(mod) }
                )

                NavTab.MARKETPLACE -> MarketplaceScreen(
                    items = marketplaceItems,
                    motorcycle = selectedBike,
                    onAddListingClicked = { showAddListingDialog = true },
                    onToggleSave = { item -> viewModel.toggleSaveItem(item) },
                    onDeleteItem = { item -> viewModel.deleteMarketplaceItem(item) }
                )

                NavTab.MAINTENANCE -> MaintenanceScreen(
                    motorcycle = selectedBike,
                    maintenanceRecords = maintenanceRecords,
                    reminders = serviceReminders,
                    onAddLogClicked = { showAddMaintDialog = true },
                    onAddReminderClicked = { showAddReminderDialog = true },
                    onCompleteReminder = { reminder -> viewModel.completeReminder(reminder) },
                    onDeleteLog = { record -> viewModel.deleteMaintenanceRecord(record) },
                    onGenerateInvoice = { record ->
                        selectedRecordForInvoice = record
                        showInvoiceDialog = true
                    },
                    onPayOnline = { record ->
                        selectedRecordForPayment = record
                        showOnlinePaymentDialog = true
                    }
                )

                NavTab.SEAT_CRAFT -> SeatCraftScreen(
                    materials = seatMaterials,
                    onCalculateSpec = { height, weight, inseam, style, length, width ->
                        viewModel.calculateSeatRestructure(height, weight, inseam, style, length, width)
                    },
                    onAddMaterialClicked = { showAddMaterialDialog = true },
                    onAdjustQuantity = { mat, delta -> viewModel.adjustMaterialQuantity(mat, delta) },
                    onDeleteMaterial = { mat -> viewModel.deleteSeatMaterial(mat) }
                )
            }
        }
    }
}

    // Dialogs
    if (showAddBikeDialog) {
        AddBikeDialog(
            onDismiss = { showAddBikeDialog = false },
            onConfirm = { name, model, year, odo, budget, spec ->
                viewModel.addMotorcycle(name, model, year, odo, budget, spec)
                showAddBikeDialog = false
            }
        )
    }

    if (showAddModDialog) {
        AddModDialog(
            onDismiss = { showAddModDialog = false },
            onConfirm = { title, category, brand, cost, status, hp, torque, weight, notes ->
                viewModel.addModification(title, category, brand, cost, status, hp, torque, weight, notes)
                showAddModDialog = false
            }
        )
    }

    if (showAddListingDialog) {
        AddListingDialog(
            onDismiss = { showAddListingDialog = false },
            onConfirm = { title, category, price, condition, fitment, desc, contact ->
                viewModel.addMarketplaceListing(title, category, price, condition, fitment, desc, contact)
                showAddListingDialog = false
            }
        )
    }

    if (showAddMaintDialog) {
        AddMaintenanceDialog(
            onDismiss = { showAddMaintDialog = false },
            onConfirm = { serviceType, odo, cost, notes, partName, partCost ->
                viewModel.addMaintenanceRecord(
                    serviceType = serviceType,
                    mileage = odo,
                    cost = cost,
                    description = notes,
                    linkedPartName = partName,
                    linkedPartCost = partCost
                )
                showAddMaintDialog = false
            }
        )
    }

    if (showAddReminderDialog) {
        AddReminderDialog(
            onDismiss = { showAddReminderDialog = false },
            onConfirm = { title, interval, notes ->
                viewModel.addServiceReminder(title, interval, notes)
                showAddReminderDialog = false
            }
        )
    }

    if (showAddMaterialDialog) {
        AddMaterialDialog(
            onDismiss = { showAddMaterialDialog = false },
            onConfirm = { name, type, texture, colorOption, qty, unit, cost, color, reorder, dims, project ->
                viewModel.addSeatMaterial(name, type, texture, colorOption, qty, unit, cost, color, reorder, dims, project)
                showAddMaterialDialog = false
            }
        )
    }

    if (showAddProjectDialog) {
        AddProjectDialog(
            onDismiss = { showAddProjectDialog = false },
            onConfirm = { name, budget, date, notes ->
                viewModel.addBuildProject(name, budget, date, notes)
                showAddProjectDialog = false
            }
        )
    }

    if (showInvoiceDialog) {
        InvoiceGeneratorDialog(
            initialRecord = selectedRecordForInvoice,
            bikeName = selectedBike?.name ?: "Custom Motorcycle",
            onDismiss = { showInvoiceDialog = false }
        )
    }

    if (showOnlinePaymentDialog) {
        val totalAmount = selectedRecordForPayment?.let { it.cost + it.linkedPartCost } ?: 150.0
        val serviceTitle = selectedRecordForPayment?.serviceType ?: "Motorcycle Workshop Service"
        OnlinePaymentCheckoutDialog(
            itemTitle = serviceTitle,
            amountToPay = totalAmount,
            customerEmail = "rider@motocraft.app",
            onPaymentSuccess = { result ->
                // Payment processed successfully
            },
            onDismiss = { showOnlinePaymentDialog = false }
        )
    }
}
