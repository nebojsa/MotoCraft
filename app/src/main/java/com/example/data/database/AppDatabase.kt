package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.MotoDao
import com.example.data.dao.SeatOrderDao
import com.example.data.entities.BuildProject
import com.example.data.entities.MaintenanceRecord
import com.example.data.entities.MarketplaceItem
import com.example.data.entities.MaterialType
import com.example.data.entities.ModCategory
import com.example.data.entities.ModStatus
import com.example.data.entities.Modification
import com.example.data.entities.Motorcycle
import com.example.data.entities.OrderStatus
import com.example.data.entities.PartCondition
import com.example.data.entities.PaymentStatus
import com.example.data.entities.SeatMaterial
import com.example.data.entities.SeatOrder
import com.example.data.entities.ServiceReminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Motorcycle::class,
        Modification::class,
        MarketplaceItem::class,
        MaintenanceRecord::class,
        ServiceReminder::class,
        SeatMaterial::class,
        BuildProject::class,
        SeatOrder::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun motoDao(): MotoDao
    abstract fun seatOrderDao(): SeatOrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "motocraft_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.motoDao(), database.seatOrderDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: MotoDao, seatOrderDao: SeatOrderDao? = null) {
            // 1. Motorcycles
            val bike1Id = dao.insertMotorcycle(
                Motorcycle(
                    name = "Yamaha YZF-R1M",
                    model = "YZF-R1M",
                    year = 2023,
                    odometerKm = 12450,
                    totalBudget = 18000.0,
                    engineSpec = "998cc Crossplane CP4",
                    isPrimary = true,
                    notes = "Track-focused apex machine"
                )
            )

            val bike2Id = dao.insertMotorcycle(
                Motorcycle(
                    name = "Honda CB650R Custom",
                    model = "CB650R Cafe",
                    year = 2022,
                    odometerKm = 8900,
                    totalBudget = 9500.0,
                    engineSpec = "649cc DOHC Inline-4",
                    isPrimary = false,
                    notes = "Neo-Sports Cafe Custom Build"
                )
            )

            // 2. Modifications for Bike 1
            dao.insertModification(
                Modification(
                    motorcycleId = bike1Id,
                    title = "Akrapovič Full Titanium Evolution System",
                    category = ModCategory.EXHAUST,
                    brand = "Akrapovič",
                    cost = 2650.0,
                    status = ModStatus.INSTALLED,
                    hpGain = 14.2,
                    torqueGainNm = 9.5,
                    weightReductionKg = 4.8,
                    notes = "Includes carbon fiber heat shield and dB killer"
                )
            )
            dao.insertModification(
                Modification(
                    motorcycleId = bike1Id,
                    title = "Woolich ECU Flash & Dyno Custom Mapping",
                    category = ModCategory.ENGINE_ECU,
                    brand = "Woolich Racing",
                    cost = 750.0,
                    status = ModStatus.INSTALLED,
                    hpGain = 11.0,
                    torqueGainNm = 7.2,
                    weightReductionKg = 0.0,
                    notes = "Decat map, top speed limiter removal, custom throttle maps"
                )
            )
            dao.insertModification(
                Modification(
                    motorcycleId = bike1Id,
                    title = "Öhlins TTX GP Rear Monoshock",
                    category = ModCategory.SUSPENSION,
                    brand = "Öhlins",
                    cost = 1850.0,
                    status = ModStatus.INSTALLED,
                    hpGain = 0.0,
                    torqueGainNm = 0.0,
                    weightReductionKg = 1.2,
                    notes = "Set up for 85kg rider weight with gear"
                )
            )
            dao.insertModification(
                Modification(
                    motorcycleId = bike1Id,
                    title = "Brembo RCS19 Corsa Corta Radial Master Cylinder",
                    category = ModCategory.BRAKES,
                    brand = "Brembo",
                    cost = 420.0,
                    status = ModStatus.INSTALLED,
                    hpGain = 0.0,
                    torqueGainNm = 0.0,
                    weightReductionKg = 0.4,
                    notes = "Paired with Z04 race brake pads"
                )
            )
            dao.insertModification(
                Modification(
                    motorcycleId = bike1Id,
                    title = "Custom Track Gel & Alcantara Saddle Restructure",
                    category = ModCategory.SEAT_ERGONOMICS,
                    brand = "SeatCraft Custom",
                    cost = 480.0,
                    status = ModStatus.INSTALLED,
                    hpGain = 0.0,
                    torqueGainNm = 0.0,
                    weightReductionKg = 0.3,
                    notes = "Integrated anatomical gel insert & high density firm foam"
                )
            )
            dao.insertModification(
                Modification(
                    motorcycleId = bike1Id,
                    title = "Full Carbon Fiber Bodywork & Aerodynamic Winglets",
                    category = ModCategory.AESTHETIC_CARBON,
                    brand = "FullSix Carbon",
                    cost = 2100.0,
                    status = ModStatus.IN_PROGRESS,
                    hpGain = 0.0,
                    torqueGainNm = 0.0,
                    weightReductionKg = 3.5,
                    notes = "Autoclave pre-preg glossy carbon fiber"
                )
            )
            dao.insertModification(
                Modification(
                    motorcycleId = bike1Id,
                    title = "Translogic Pit Lane Limiter & Auto-Blipper Module",
                    category = ModCategory.ELECTRONICS,
                    brand = "Translogic",
                    cost = 620.0,
                    status = ModStatus.PLANNED,
                    hpGain = 0.0,
                    torqueGainNm = 0.0,
                    weightReductionKg = 0.0,
                    notes = "Plug and play harness for smooth seamless downshifts"
                )
            )

            // Mods for Bike 2
            dao.insertModification(
                Modification(
                    motorcycleId = bike2Id,
                    title = "Tuck & Roll Hand-Stitched Leather Cafe Saddle",
                    category = ModCategory.SEAT_ERGONOMICS,
                    brand = "MotoCraft Studio",
                    cost = 380.0,
                    status = ModStatus.INSTALLED,
                    hpGain = 0.0,
                    torqueGainNm = 0.0,
                    weightReductionKg = 0.8,
                    notes = "Cognac brown Italian full-grain leather"
                )
            )

            // 3. Marketplace Items
            dao.insertMarketplaceItem(
                MarketplaceItem(
                    title = "SC-Project S1 Titanium Full Exhaust",
                    category = "Exhaust",
                    price = 1150.0,
                    condition = PartCondition.LIKE_NEW,
                    fitment = "Yamaha YZF-R6 (2017-2023)",
                    description = "Used for only 300 km. Ultra lightweight titanium construction with carbon fiber end cap.",
                    sellerName = "Alex_Rider99",
                    sellerContact = "alex.r6@motocraft.app",
                    isUserListing = false,
                    isSaved = true
                )
            )
            dao.insertMarketplaceItem(
                MarketplaceItem(
                    title = "25mm Medical Grade Seat Gel Insert Pad (Large)",
                    category = "Seat & Materials",
                    price = 65.0,
                    condition = PartCondition.NEW,
                    fitment = "Universal Fit (Front & Rear Saddles)",
                    description = "Eliminates pressure points and vibration fatigue on long trips. Easy to trim with utility knife.",
                    sellerName = "SeatCraft_Official",
                    sellerContact = "sales@seatcraft.app",
                    isUserListing = true,
                    isSaved = false
                )
            )
            dao.insertMarketplaceItem(
                MarketplaceItem(
                    title = "Ohlins Steering Damper SD 008",
                    category = "Suspension",
                    price = 340.0,
                    condition = PartCondition.USED,
                    fitment = "Universal Side Mount / Ducati Panigale",
                    description = "Fully adjustable stroke damper in perfect working order. Rebound smooth.",
                    sellerName = "Marco_RaceLab",
                    sellerContact = "marco@racelab.io",
                    isUserListing = false,
                    isSaved = true
                )
            )
            dao.insertMarketplaceItem(
                MarketplaceItem(
                    title = "Marine Grade Carbon Pattern Vinyl (2 Sq Meters)",
                    category = "Seat & Materials",
                    price = 45.0,
                    condition = PartCondition.NEW,
                    fitment = "Custom Upholstery & Crafting",
                    description = "Heavy duty UV resistant and waterproof motorcycle seat material. High stretch flexibility.",
                    sellerName = "CustomSeats_Shop",
                    sellerContact = "info@customseats.com",
                    isUserListing = false,
                    isSaved = false
                )
            )

            // 4. Maintenance Records
            dao.insertMaintenanceRecord(
                MaintenanceRecord(
                    motorcycleId = bike1Id,
                    serviceType = "Motul 300V 10W40 Synthetic Oil & OEM Filter",
                    mileage = 12000,
                    description = "Replaced drain washer & torqued to 43 Nm",
                    date = System.currentTimeMillis() - 86400000L * 14,
                    cost = 135.0,
                    performedBy = "Owner"
                )
            )
            dao.insertMaintenanceRecord(
                MaintenanceRecord(
                    motorcycleId = bike1Id,
                    serviceType = "Brake Fluid Flush (Motul RBF 660 Factory Line)",
                    mileage = 10500,
                    description = "Fully bled front Brembo calipers and rear master",
                    date = System.currentTimeMillis() - 86400000L * 30,
                    cost = 55.0,
                    performedBy = "Owner"
                )
            )

            // 5. Service Reminders
            dao.insertServiceReminder(
                ServiceReminder(
                    motorcycleId = bike1Id,
                    title = "Engine Oil & Filter Change",
                    intervalKm = 4000,
                    lastServiceKm = 12000,
                    isCompleted = false,
                    notes = "Use 10W-40 Synthetic + OEM Filter"
                )
            )
            dao.insertServiceReminder(
                ServiceReminder(
                    motorcycleId = bike1Id,
                    title = "Chain Clean, Lube & Slack Adjust",
                    intervalKm = 800,
                    lastServiceKm = 11500, // 11500 + 800 = 12300. Current is 12450 -> Overdue by 150 km
                    isCompleted = false,
                    notes = "Keep slack between 25-35mm"
                )
            )
            dao.insertServiceReminder(
                ServiceReminder(
                    motorcycleId = bike1Id,
                    title = "Spark Plugs & Valve Clearance Inspection",
                    intervalKm = 12000,
                    lastServiceKm = 0,
                    isCompleted = false,
                    notes = "Check intake/exhaust shim clearances"
                )
            )

            // 6. Seat Materials Inventory
            dao.insertSeatMaterial(
                SeatMaterial(
                    name = "40mm Bonded High-Density Base Foam",
                    type = MaterialType.HIGH_DENSITY_FOAM,
                    texture = "Solid Cell Foam",
                    colorOption = "Natural Yellow",
                    quantityOnHand = 4.0,
                    unit = "slabs",
                    unitCost = 35.0,
                    colorOrGrade = "Firm 80-Density",
                    reorderLevel = 2.0,
                    dimensions = "50cm x 40cm x 40mm",
                    assignedProject = "Track Day Comfort Seat Base",
                    notes = "Provides solid support preventing bottoming out"
                )
            )
            dao.insertSeatMaterial(
                SeatMaterial(
                    name = "20mm Visco Elastic Memory Foam Overlay",
                    type = MaterialType.MEMORY_FOAM,
                    texture = "Open Cell Visco",
                    colorOption = "Light Blue",
                    quantityOnHand = 6.0,
                    unit = "slabs",
                    unitCost = 28.0,
                    colorOrGrade = "Plush Ergonomic Contour",
                    reorderLevel = 3.0,
                    dimensions = "50cm x 40cm x 20mm",
                    assignedProject = "Touring Comfort Saddle",
                    notes = "Molds to rider weight distribution"
                )
            )
            dao.insertSeatMaterial(
                SeatMaterial(
                    name = "Medical Grade 25mm Anatomical Gel Pad",
                    type = MaterialType.GEL_PAD,
                    texture = "Honeycomb Grid",
                    colorOption = "Translucent Blue",
                    quantityOnHand = 1.0, // Low inventory warning
                    unit = "pads",
                    unitCost = 42.0,
                    colorOrGrade = "Medium Firm Gel",
                    reorderLevel = 2.0,
                    dimensions = "25cm x 20cm x 25mm",
                    assignedProject = "R1M Custom Saddle",
                    notes = "Vibration damper & tailbone relief"
                )
            )
            dao.insertSeatMaterial(
                SeatMaterial(
                    name = "Black Diamond-Stitched Marine Vinyl",
                    type = MaterialType.MARINE_VINYL,
                    texture = "Diamond Tuck & Roll",
                    colorOption = "Jet Black",
                    quantityOnHand = 10.0,
                    unit = "sq ft",
                    unitCost = 14.0,
                    colorOrGrade = "UV & Waterproof Black",
                    reorderLevel = 5.0,
                    dimensions = "Continuous Roll 1.4m width",
                    assignedProject = "Cafe Racer Saddle",
                    notes = "Double gold stitch line contrast"
                )
            )
            dao.insertSeatMaterial(
                SeatMaterial(
                    name = "Italian Cognac Full-Grain Leather",
                    type = MaterialType.GENUINE_LEATHER,
                    texture = "Vintage Distressed Grain",
                    colorOption = "Espresso Cognac",
                    quantityOnHand = 8.5,
                    unit = "sq ft",
                    unitCost = 38.0,
                    colorOrGrade = "Vintage Cognac Tan",
                    reorderLevel = 4.0,
                    dimensions = "Selected Hide 1.2mm thickness",
                    assignedProject = "CB650R Custom Restructure",
                    notes = "Treated with hydrophobic beeswax finish"
                )
            )
            dao.insertSeatMaterial(
                SeatMaterial(
                    name = "Perforated High-Grip Alcantara Suede",
                    type = MaterialType.ALCANTARA,
                    texture = "Perforated Racing Grid",
                    colorOption = "Charcoal Grey",
                    quantityOnHand = 5.0,
                    unit = "sq ft",
                    unitCost = 45.0,
                    colorOrGrade = "Automotive Microfiber",
                    reorderLevel = 2.0,
                    dimensions = "1m x 0.8m",
                    assignedProject = "Track Day Comfort Seat Base",
                    notes = "High friction anti-slip surface for sport cornering"
                )
            )
            dao.insertSeatMaterial(
                SeatMaterial(
                    name = "Bonded Nylon Heavy Duty Thread (#69)",
                    type = MaterialType.STITCHING_THREAD,
                    quantityOnHand = 3.0,
                    unit = "spools",
                    unitCost = 18.0,
                    colorOrGrade = "High-Tensile Amber Orange",
                    reorderLevel = 1.0,
                    dimensions = "1500 Yards / Spool",
                    assignedProject = "General Workshop Stock",
                    notes = "Weatherproof rot-resistant thread"
                )
            )
            dao.insertSeatMaterial(
                SeatMaterial(
                    name = "3M High Tack Spray Adhesive",
                    type = MaterialType.ADHESIVE_SPRAY,
                    quantityOnHand = 5.0,
                    unit = "cans",
                    unitCost = 16.5,
                    colorOrGrade = "Quick Bonding Clear",
                    reorderLevel = 2.0,
                    dimensions = "500ml Can",
                    assignedProject = "Foam Layering & Vinyl Contact",
                    notes = "Fast drying foam-to-gel and pan bonding"
                )
            )

            // 7. Build Projects
            dao.insertBuildProject(
                BuildProject(
                    motorcycleId = bike1Id,
                    name = "Track Day Apex Build",
                    targetBudget = 12000.0,
                    targetCompletionDate = "September 2026",
                    status = "Active",
                    notes = "High speed ergonomics, suspension & power delivery upgrades"
                )
            )
            // 8. Seat Orders
            seatOrderDao?.let { sDao ->
                sDao.insertSeatOrder(
                    SeatOrder(
                        customerName = "Marcus Vance",
                        customerPhone = "+1 (555) 381-9201",
                        motorcycleModel = "Yamaha YZF-R1M",
                        bikerHeightCm = 182.0,
                        bikerWeightKg = 88.0,
                        bikerInseamCm = 84.0,
                        ridingPosture = "Sport / Track",
                        coverMaterialName = "Perforated Anti-Slip Marine Vinyl",
                        coverTexture = "Hexagon Diamond Stitched",
                        colorOption = "Jet Black with Racing Blue Stitching",
                        foamThicknessMm = 45.0,
                        hasGelPad = true,
                        gelPadAreaSqCm = 350.0,
                        baseMaterialCost = 120.0,
                        laborCost = 150.0,
                        depositAmount = 100.0,
                        orderStatus = OrderStatus.IN_PROGRESS,
                        paymentStatus = PaymentStatus.DEPOSIT_PAID,
                        notes = "Custom high speed ergonomic recessed center channel"
                    )
                )
                sDao.insertSeatOrder(
                    SeatOrder(
                        customerName = "Elena Rostova",
                        customerPhone = "+1 (555) 742-1088",
                        motorcycleModel = "BMW R1250 GS Adventure",
                        bikerHeightCm = 172.0,
                        bikerWeightKg = 74.0,
                        bikerInseamCm = 78.0,
                        ridingPosture = "Touring / Adventure",
                        coverMaterialName = "Italian Full-Grain Cognac Leather",
                        coverTexture = "Vintage Distressed Tuck & Roll",
                        colorOption = "Espresso Cognac",
                        foamThicknessMm = 50.0,
                        hasGelPad = true,
                        gelPadAreaSqCm = 400.0,
                        baseMaterialCost = 160.0,
                        laborCost = 180.0,
                        depositAmount = 340.0,
                        orderStatus = OrderStatus.READY_FOR_FITTING,
                        paymentStatus = PaymentStatus.PAID_IN_FULL,
                        notes = "Low profile cut -15mm for ground reach comfort"
                    )
                )
            }
        }
    }
}
