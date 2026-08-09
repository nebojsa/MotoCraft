package com.example.ui.components

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.MaintenanceRecord
import com.example.data.entities.Modification
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.BrightOrange
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.BuildBudgetStats
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TimePoint(
    val label: String,
    val timestamp: Long,
    val value: Double,
    val secondaryValue: Double = 0.0,
    val details: String = ""
)

enum class ChartTab {
    MAINTENANCE_TREND,
    BUDGET_CONSUMPTION,
    CATEGORY_ALLOCATION
}

@Composable
fun InteractiveDashboardAnalytics(
    maintenanceRecords: List<MaintenanceRecord>,
    modifications: List<Modification>,
    stats: BuildBudgetStats,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(ChartTab.MAINTENANCE_TREND) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp))
            .animateContentSize()
            .testTag("dashboard_analytics_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header & Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AmberOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = AmberOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "VISUAL ANALYTICS & TRENDS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = when (activeTab) {
                                ChartTab.MAINTENANCE_TREND -> "Maintenance Cost Over Time"
                                ChartTab.BUDGET_CONSUMPTION -> "Budget Consumption & Burn Rate"
                                ChartTab.CATEGORY_ALLOCATION -> "Category Spend Distribution"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation selector tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF161A24))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ChartTabButton(
                    title = "Trends",
                    icon = Icons.Default.ShowChart,
                    isSelected = activeTab == ChartTab.MAINTENANCE_TREND,
                    onClick = { activeTab = ChartTab.MAINTENANCE_TREND },
                    modifier = Modifier.weight(1f).testTag("tab_maintenance_trend")
                )
                ChartTabButton(
                    title = "Budget",
                    icon = Icons.Default.TrendingUp,
                    isSelected = activeTab == ChartTab.BUDGET_CONSUMPTION,
                    onClick = { activeTab = ChartTab.BUDGET_CONSUMPTION },
                    modifier = Modifier.weight(1f).testTag("tab_budget_consumption")
                )
                ChartTabButton(
                    title = "Category",
                    icon = Icons.Default.PieChart,
                    isSelected = activeTab == ChartTab.CATEGORY_ALLOCATION,
                    onClick = { activeTab = ChartTab.CATEGORY_ALLOCATION },
                    modifier = Modifier.weight(1f).testTag("tab_category_allocation")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (activeTab) {
                ChartTab.MAINTENANCE_TREND -> MaintenanceTrendChart(records = maintenanceRecords)
                ChartTab.BUDGET_CONSUMPTION -> BudgetConsumptionChart(
                    stats = stats,
                    records = maintenanceRecords,
                    modifications = modifications
                )
                ChartTab.CATEGORY_ALLOCATION -> CategoryDistributionDonutChart(stats = stats)
            }
        }
    }
}

@Composable
private fun ChartTabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AmberOrange else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = if (isSelected) Color.Black else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MaintenanceTrendChart(
    records: List<MaintenanceRecord>,
    modifier: Modifier = Modifier
) {
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    // Aggregate monthly data or fallback mock timeline if empty
    val points = remember(records) {
        if (records.isEmpty()) {
            listOf(
                TimePoint("Jan", 1, 80.0, details = "Initial Inspection & Fluids"),
                TimePoint("Feb", 2, 140.0, details = "Synthetic Oil & Spark Plugs"),
                TimePoint("Mar", 3, 90.0, details = "Chain Maintenance"),
                TimePoint("Apr", 4, 210.0, details = "Brake Pads & Fluid Flush"),
                TimePoint("May", 5, 120.0, details = "Air Filter & Calibration"),
                TimePoint("Jun", 6, 280.0, details = "Front Tire & Fork Seals"),
                TimePoint("Jul", 7, 160.0, details = "Battery & Valve Clearance"),
                TimePoint("Aug", 8, 220.0, details = "Saddle Custom Fitting")
            )
        } else {
            val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
            val sorted = records.sortedBy { it.date }
            sorted.mapIndexed { idx, record ->
                TimePoint(
                    label = sdf.format(Date(record.date)),
                    timestamp = record.date,
                    value = record.cost,
                    details = "${record.serviceType} ($${String.format("%.0f", record.cost)}) - ${record.performedBy}"
                )
            }
        }
    }

    val totalCost = points.sumOf { it.value }
    val avgCost = if (points.isNotEmpty()) totalCost / points.size else 0.0
    val maxCost = points.maxOfOrNull { it.value } ?: 1.0

    Column(modifier = modifier.fillMaxWidth()) {
        // Summary metrics
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Spend", fontSize = 11.sp, color = TextMuted)
                Text("$${String.format("%.2f", totalCost)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
            }
            Column {
                Text("Average / Event", fontSize = 11.sp, color = TextMuted)
                Text("$${String.format("%.2f", avgCost)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TechCyan)
            }
            Column {
                Text("Events Logged", fontSize = 11.sp, color = TextMuted)
                Text("${points.size}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AmberOrange)
            }
        }

        // Canvas Trend Line Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFF141822), RoundedCornerShape(12.dp))
                .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                .padding(12.dp)
                .testTag("maintenance_trend_chart_canvas")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(points) {
                        detectTapGestures { tapOffset ->
                            val width = size.width
                            val stepX = width / (points.size - 1).coerceAtLeast(1)
                            val clickedIndex = ((tapOffset.x + stepX / 2) / stepX).toInt().coerceIn(0, points.size - 1)
                            selectedPointIndex = clickedIndex
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val paddingBottom = 24.dp.toPx()
                val paddingTop = 16.dp.toPx()
                val usableHeight = height - paddingBottom - paddingTop
                val stepX = width / (points.size - 1).coerceAtLeast(1)

                val maxVal = (points.maxOfOrNull { it.value } ?: 1.0).coerceAtLeast(50.0) * 1.15

                // Draw background horizontal grid lines
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = paddingTop + (usableHeight / gridLines) * i
                    drawLine(
                        color = CardBorderDark.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Build path for chart line & filled area under line
                val strokePath = Path()
                val fillPath = Path()

                points.forEachIndexed { i, point ->
                    val x = i * stepX
                    val y = paddingTop + usableHeight - ((point.value / maxVal).toFloat() * usableHeight)

                    if (i == 0) {
                        strokePath.moveTo(x, y)
                        fillPath.moveTo(x, height - paddingBottom)
                        fillPath.lineTo(x, y)
                    } else {
                        val prevX = (i - 1) * stepX
                        val prevY = paddingTop + usableHeight - ((points[i - 1].value / maxVal).toFloat() * usableHeight)
                        val controlX1 = prevX + (x - prevX) / 2
                        val controlX2 = prevX + (x - prevX) / 2

                        strokePath.cubicTo(controlX1, prevY, controlX2, y, x, y)
                        fillPath.cubicTo(controlX1, prevY, controlX2, y, x, y)
                    }

                    if (i == points.size - 1) {
                        fillPath.lineTo(x, height - paddingBottom)
                        fillPath.close()
                    }
                }

                // Draw Gradient Fill under line
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            TechCyan.copy(alpha = 0.35f),
                            TechCyan.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        startY = paddingTop,
                        endY = height - paddingBottom
                    )
                )

                // Draw Trend Line
                drawPath(
                    path = strokePath,
                    color = TechCyan,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw data node circles
                points.forEachIndexed { i, point ->
                    val x = i * stepX
                    val y = paddingTop + usableHeight - ((point.value / maxVal).toFloat() * usableHeight)
                    val isSelected = selectedPointIndex == i

                    // Outer halo for selected
                    if (isSelected) {
                        drawCircle(
                            color = TechCyan.copy(alpha = 0.3f),
                            radius = 12.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }

                    drawCircle(
                        color = CardDark,
                        radius = 6.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = if (isSelected) AmberOrange else TechCyan,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )

                    // Draw X-Axis Labels
                    drawContext.canvas.nativeCanvas.drawText(
                        point.label,
                        x,
                        height - 4.dp.toPx(),
                        Paint().apply {
                            color = TextMuted.toArgb()
                            textSize = 10.sp.toPx()
                            textAlign = Paint.Align.CENTER
                        }
                    )
                }
            }
        }

        // Active Point Tooltip details
        selectedPointIndex?.let { index ->
            val point = points.getOrNull(index)
            if (point != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E2433))
                        .border(1.dp, TechCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SELECTED ENTRY: ${point.label}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechCyan
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = point.details,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "$${String.format("%.2f", point.value)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AmberOrange
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetConsumptionChart(
    stats: BuildBudgetStats,
    records: List<MaintenanceRecord>,
    modifications: List<Modification>,
    modifier: Modifier = Modifier
) {
    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }

    val monthlyData = remember(stats, records, modifications) {
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug")
        val baseBudget = stats.totalBudget.coerceAtLeast(1000.0)

        // Mock/Calculated breakdown for timeline visualization
        var accumSpent = 0.0
        val modCostPerMonth = if (modifications.isNotEmpty()) modifications.sumOf { it.cost } / 8 else 450.0
        val maintCostPerMonth = if (records.isNotEmpty()) records.sumOf { it.cost } / 8 else 180.0

        months.mapIndexed { idx, m ->
            val modVal = modCostPerMonth * (0.8 + (idx % 3) * 0.3)
            val maintVal = maintCostPerMonth * (0.7 + (idx % 2) * 0.4)
            accumSpent += (modVal + maintVal)
            TimePoint(
                label = m,
                timestamp = idx.toLong(),
                value = modVal,
                secondaryValue = maintVal,
                details = "Cumulative Spent: $${String.format("%.0f", accumSpent)} / $${String.format("%.0f", baseBudget)}"
            )
        }
    }

    val totalBudget = stats.totalBudget.coerceAtLeast(1000.0)
    val currentSpent = stats.totalModSpent + stats.totalMaintenanceSpent
    val percentUsed = (currentSpent / totalBudget * 100).coerceAtMost(100.0)

    Column(modifier = modifier.fillMaxWidth()) {
        // Burn rate header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total Target Budget", fontSize = 11.sp, color = TextMuted)
                Text("$${String.format("%.0f", totalBudget)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Column {
                Text("Consumed", fontSize = 11.sp, color = TextMuted)
                Text("$${String.format("%.0f", currentSpent)} (${String.format("%.1f", percentUsed)}%)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (currentSpent > totalBudget) CrimsonRed else AmberOrange)
            }
            Column {
                Text("Remaining", fontSize = 11.sp, color = TextMuted)
                Text("$${String.format("%.0f", (totalBudget - currentSpent).coerceAtLeast(0.0))}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
            }
        }

        // Stacked Bar / Trend Canvas Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFF141822), RoundedCornerShape(12.dp))
                .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                .padding(12.dp)
                .testTag("budget_consumption_chart_canvas")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(monthlyData) {
                        detectTapGestures { tapOffset ->
                            val width = size.width
                            val stepX = width / monthlyData.size
                            val clickedIndex = (tapOffset.x / stepX).toInt().coerceIn(0, monthlyData.size - 1)
                            selectedBarIndex = clickedIndex
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val paddingBottom = 24.dp.toPx()
                val paddingTop = 16.dp.toPx()
                val usableHeight = height - paddingBottom - paddingTop
                val barWidth = (width / monthlyData.size) * 0.55f
                val stepX = width / monthlyData.size

                val maxMonthVal = (monthlyData.maxOfOrNull { it.value + it.secondaryValue } ?: 1.0) * 1.25

                // Draw background budget limit dashed line
                val budgetLineY = paddingTop + usableHeight * 0.2f
                drawLine(
                    color = CrimsonRed.copy(alpha = 0.6f),
                    start = Offset(0f, budgetLineY),
                    end = Offset(width, budgetLineY),
                    strokeWidth = 1.5.dp.toPx()
                )

                monthlyData.forEachIndexed { i, data ->
                    val centerX = i * stepX + stepX / 2
                    val isSelected = selectedBarIndex == i

                    val modHeight = ((data.value / maxMonthVal) * usableHeight).toFloat()
                    val maintHeight = ((data.secondaryValue / maxMonthVal) * usableHeight).toFloat()

                    val barLeft = centerX - barWidth / 2
                    val barTopMaint = height - paddingBottom - maintHeight
                    val barTopMod = barTopMaint - modHeight

                    // Draw Maintenance Bar Segment (Bottom)
                    drawRect(
                        color = TechCyan.copy(alpha = if (isSelected) 1f else 0.8f),
                        topLeft = Offset(barLeft, barTopMaint),
                        size = Size(barWidth, maintHeight)
                    )

                    // Draw Upgrades Bar Segment (Top)
                    drawRect(
                        color = AmberOrange.copy(alpha = if (isSelected) 1f else 0.85f),
                        topLeft = Offset(barLeft, barTopMod),
                        size = Size(barWidth, modHeight)
                    )

                    if (isSelected) {
                        drawRect(
                            color = Color.White.copy(alpha = 0.2f),
                            topLeft = Offset(barLeft - 2.dp.toPx(), barTopMod - 2.dp.toPx()),
                            size = Size(barWidth + 4.dp.toPx(), modHeight + maintHeight + 4.dp.toPx())
                        )
                    }

                    // Month label
                    drawContext.canvas.nativeCanvas.drawText(
                        data.label,
                        centerX,
                        height - 4.dp.toPx(),
                        Paint().apply {
                            color = TextMuted.toArgb()
                            textSize = 10.sp.toPx()
                            textAlign = Paint.Align.CENTER
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Legend row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp).background(AmberOrange, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Upgrades", fontSize = 11.sp, color = TextSecondary)

            Spacer(modifier = Modifier.width(16.dp))

            Box(modifier = Modifier.size(10.dp).background(TechCyan, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Maintenance", fontSize = 11.sp, color = TextSecondary)

            Spacer(modifier = Modifier.width(16.dp))

            Box(modifier = Modifier.size(10.dp).background(CrimsonRed, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Target Threshold", fontSize = 11.sp, color = TextSecondary)
        }

        selectedBarIndex?.let { index ->
            val data = monthlyData.getOrNull(index)
            if (data != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E2433))
                        .border(1.dp, AmberOrange.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${data.label} Breakdown:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Mods: $${String.format("%.0f", data.value)} | Maint: $${String.format("%.0f", data.secondaryValue)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AmberOrange)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDistributionDonutChart(
    stats: BuildBudgetStats,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableStateOf<Int?>(null) }

    val categories = remember(stats.categoryBreakdown) {
        if (stats.categoryBreakdown.isEmpty()) {
            listOf(
                "EXHAUST" to 1450.0,
                "ENGINE ECU" to 890.0,
                "SUSPENSION" to 1120.0,
                "SEAT ERGONOMICS" to 420.0,
                "MAINTENANCE" to 680.0
            )
        } else {
            stats.categoryBreakdown.toList()
        }
    }

    val totalCatSpent = categories.sumOf { it.second }.coerceAtLeast(1.0)
    val colors = listOf(AmberOrange, TechCyan, EmeraldGreen, VioletPurple, CrimsonRed, BrightOrange)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interactive Donut Canvas
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp)
                    .testTag("category_donut_chart_canvas"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(categories) {
                            detectTapGestures { tapOffset ->
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val angle = Math.toDegrees(
                                    Math.atan2(
                                        (tapOffset.y - center.y).toDouble(),
                                        (tapOffset.x - center.x).toDouble()
                                    )
                                )
                                val normalizedAngle = (angle + 360 + 90) % 360

                                var currentAngle = 0.0
                                categories.forEachIndexed { i, pair ->
                                    val sweep = (pair.second / totalCatSpent) * 360.0
                                    if (normalizedAngle >= currentAngle && normalizedAngle < currentAngle + sweep) {
                                        selectedCategoryIndex = i
                                    }
                                    currentAngle += sweep
                                }
                            }
                        }
                ) {
                    val strokeWidth = 24.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset(
                        (size.width - diameter) / 2,
                        (size.height - diameter) / 2
                    )
                    val arcSize = Size(diameter, diameter)

                    var startAngle = -90f
                    categories.forEachIndexed { i, pair ->
                        val sweepAngle = ((pair.second / totalCatSpent) * 360f).toFloat()
                        val color = colors[i % colors.size]
                        val isSelected = selectedCategoryIndex == i

                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle - 2f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(
                                width = if (isSelected) strokeWidth * 1.25f else strokeWidth,
                                cap = StrokeCap.Round
                            )
                        )
                        startAngle += sweepAngle
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$${String.format("%.0f", totalCatSpent)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text("Total Spent", fontSize = 9.sp, color = TextMuted)
                }
            }

            // Legend Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEachIndexed { i, (catName, amount) ->
                    val color = colors[i % colors.size]
                    val percent = (amount / totalCatSpent * 100).toInt()
                    val isSelected = selectedCategoryIndex == i

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) color.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { selectedCategoryIndex = i }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = catName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) color else TextPrimary
                            )
                        }
                        Text(
                            text = "$${amount.toInt()} ($percent%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
