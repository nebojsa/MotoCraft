package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.MarketplaceItem
import com.example.data.entities.Motorcycle
import com.example.data.entities.PartCondition
import com.example.ui.components.BadgeChip
import com.example.ui.components.PartCompatibilitySearchCard
import com.example.ui.components.PartCompatibilitySearchDialog
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPurple
import com.example.util.OnlinePaymentCheckoutDialog

@Composable
fun MarketplaceScreen(
    items: List<MarketplaceItem>,
    motorcycle: Motorcycle? = null,
    onAddListingClicked: () -> Unit,
    onToggleSave: (MarketplaceItem) -> Unit,
    onDeleteItem: (MarketplaceItem) -> Unit,
    onBuyNowClicked: (MarketplaceItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = All, 1 = Saved, 2 = My Listings
    var activeContactSellerItem by remember { mutableStateOf<MarketplaceItem?>(null) }
    var activeCompatibilityCheckItem by remember { mutableStateOf<MarketplaceItem?>(null) }
    var activeBuyNowItem by remember { mutableStateOf<MarketplaceItem?>(null) }

    val defaultBikeName = motorcycle?.let { "${it.year} ${it.name} ${it.model}".trim() } ?: "Yamaha MT-09 2023"

    val categories = listOf("Exhaust", "Suspension", "Brakes", "Seat & Materials", "ECU & Tuners")

    val filteredItems = items.filter { item ->
        val matchesTab = when (selectedTab) {
            1 -> item.isSaved
            2 -> item.isUserListing
            else -> true
        }
        val matchesCategory = selectedCategoryFilter == null || item.category.equals(selectedCategoryFilter, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.fitment.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)

        matchesTab && matchesCategory && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddListingClicked,
                containerColor = AmberOrange,
                contentColor = Color.Black,
                modifier = Modifier.testTag("fab_create_listing")
            ) {
                Icon(Icons.Default.Add, contentDescription = "List Part for Sale")
            }
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .testTag("marketplace_parts_grid")
        ) {
            // Search Bar & Fitment Verifier Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search parts, exhaust, seat foam, fitment...", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = AmberOrange) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardDark,
                            unfocusedContainerColor = CardDark,
                            focusedBorderColor = AmberOrange,
                            unfocusedBorderColor = CardBorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("marketplace_search_input")
                    )

                    PartCompatibilitySearchCard(defaultBikeModel = defaultBikeName)

                    // Tabs Row
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = SurfaceDark,
                        contentColor = AmberOrange,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = AmberOrange
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Community Parts", color = if (selectedTab == 0) AmberOrange else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Saved Parts", color = if (selectedTab == 1) AmberOrange else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("My Listings", color = if (selectedTab == 2) AmberOrange else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                    }

                    // Categories Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChipPill(
                                label = "All Categories",
                                isSelected = selectedCategoryFilter == null,
                                onClick = { selectedCategoryFilter = null }
                            )
                        }
                        items(categories) { cat ->
                            FilterChipPill(
                                label = cat,
                                isSelected = selectedCategoryFilter == cat,
                                onClick = { selectedCategoryFilter = cat }
                            )
                        }
                    }
                }
            }

            if (filteredItems.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No marketplace parts found.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { item ->
                    MarketplaceGridPartCard(
                        item = item,
                        onToggleSave = { onToggleSave(item) },
                        onContactSeller = { activeContactSellerItem = item },
                        onVerifyFitment = { activeCompatibilityCheckItem = item },
                        onBuyNow = {
                            activeBuyNowItem = item
                            onBuyNowClicked(item)
                        },
                        onDelete = { onDeleteItem(item) }
                    )
                }
            }
        }
    }

    // Google Part Fitment Check Dialog
    activeCompatibilityCheckItem?.let { item ->
        PartCompatibilitySearchDialog(
            bikeModel = defaultBikeName,
            partName = "${item.title} (${item.fitment})",
            onDismiss = { activeCompatibilityCheckItem = null }
        )
    }

    // Online Payment Checkout Dialog for "Buy Now"
    activeBuyNowItem?.let { item ->
        OnlinePaymentCheckoutDialog(
            itemTitle = item.title,
            amountToPay = item.price,
            customerEmail = item.sellerContact.ifBlank { "rider@motocraft.app" },
            onPaymentSuccess = { result ->
                // Payment success logic handled inside dialog
            },
            onDismiss = { activeBuyNowItem = null }
        )
    }

    // Contact Seller Dialog
    activeContactSellerItem?.let { item ->
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { activeContactSellerItem = null },
            containerColor = CardDark,
            title = {
                Text(text = "Contact Seller", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(text = item.title, color = AmberOrange, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Seller: ${item.sellerName}", color = TextPrimary)
                    Text(text = "Contact Email: ${item.sellerContact}", color = TechCyan)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fitment: ${item.fitment}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${item.sellerContact}")
                            putExtra(Intent.EXTRA_SUBJECT, "Inquiry on MotoCraft Marketplace: ${item.title}")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                        activeContactSellerItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberOrange)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send Email Inquiry", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeContactSellerItem = null }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun MarketplaceGridPartCard(
    item: MarketplaceItem,
    onToggleSave: () -> Unit,
    onContactSeller: () -> Unit,
    onVerifyFitment: () -> Unit,
    onBuyNow: () -> Unit,
    onDelete: () -> Unit
) {
    val conditionColor = when (item.condition) {
        PartCondition.NEW -> EmeraldGreen
        PartCondition.LIKE_NEW -> TechCyan
        PartCondition.USED -> AmberOrange
        PartCondition.REFURBISHED -> VioletPurple
    }

    val (categoryIcon, categoryBrush) = when (item.category.lowercase()) {
        "exhaust" -> Pair(
            Icons.Default.TwoWheeler,
            Brush.horizontalGradient(listOf(Color(0xFFE65100), Color(0xFFFFB74D)))
        )
        "suspension" -> Pair(
            Icons.Default.Build,
            Brush.horizontalGradient(listOf(Color(0xFF00838F), Color(0xFF00E5FF)))
        )
        "brakes" -> Pair(
            Icons.Default.Speed,
            Brush.horizontalGradient(listOf(Color(0xFFB71C1C), Color(0xFFFF5252)))
        )
        "seat & materials" -> Pair(
            Icons.Default.Handyman,
            Brush.horizontalGradient(listOf(Color(0xFF4A148C), Color(0xFFAB47BC)))
        )
        else -> Pair(
            Icons.Default.ShoppingBag,
            Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF66BB6A)))
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
            .testTag("part_grid_card_${item.id}")
    ) {
        Column {
            // Visual Part Banner Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(categoryBrush)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(6.dp)
                    ) {
                        Icon(categoryIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = if (item.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Part",
                            tint = if (item.isSaved) AmberOrange else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Price Badge at Bottom of Banner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "$${String.format("%.2f", item.price)}",
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldGreen,
                        fontSize = 13.sp
                    )
                }
            }

            // Part Details
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgeChip(text = item.condition.name.replace("_", " "), color = conditionColor)
                    BadgeChip(text = item.category, color = VioletPurple)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Fitment: ${item.fitment}",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Buy Now Action Button (Primary)
                Button(
                    onClick = onBuyNow,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("buy_now_grid_btn_${item.id}")
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("BUY NOW", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Secondary Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onVerifyFitment,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TechCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Fitment", color = TechCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onContactSeller,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Contact Seller", tint = AmberOrange, modifier = Modifier.size(16.dp))
                    }

                    if (item.isUserListing) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Listing", tint = CrimsonRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
