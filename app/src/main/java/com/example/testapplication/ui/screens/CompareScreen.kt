package com.example.testapplication.ui.screens

import java.util.Locale
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.testapplication.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.testapplication.model.Product
import kotlin.math.roundToInt

@Composable
fun CompareScreen(
    isDarkMode: Boolean,
    products: MutableList<Product>,
    onAddClick: () -> Unit,
    onEditClick: (Product) -> Unit
) {
    val sortedProducts = remember(products.toList()) {
        products.sortedBy { it.pricePerBaseUnit }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (products.isEmpty()) {
            Spacer(modifier = Modifier.height(60.dp))
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF2E7D32).copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                stringResource(R.string.compare_empty_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                stringResource(R.string.compare_empty_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onAddClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.compare_add_first), fontWeight = FontWeight.Medium)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.compare_products_count, products.size),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color(0xFF333333)
                )
                Text(
                    stringResource(R.string.compare_ordered_by),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sortedProducts, key = { it.id }) { product ->
                    ProductCard(
                        isDarkMode = isDarkMode,
                        product = product,
                        position = sortedProducts.indexOf(product),
                        bestPricePerBaseUnit = sortedProducts.firstOrNull()?.pricePerBaseUnit ?: 0.0,
                        onDelete = { productToDelete ->
                            products.removeAll { it.id == productToDelete.id }
                        },
                        onEdit = { onEditClick(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    isDarkMode: Boolean,
    product: Product,
    position: Int,
    bestPricePerBaseUnit: Double,
    onDelete: (Product) -> Unit,
    onEdit: () -> Unit
) {
    val isBestOption = position == 0
    val cardBgColor = when {
        isBestOption -> if (isDarkMode) Color(0xFF1B3821) else Color(0xFFE8F5E9)
        position == 1 -> if (isDarkMode) Color(0xFF1A2E44) else Color(0xFFE3F2FD)
        else -> if (isDarkMode) Color(0xFF252525) else Color.White
    }
    
    val accentColor = when {
        isBestOption -> Color(0xFF2E7D32)
        position == 1 -> Color(0xFF1976D2)
        else -> Color(0xFF757575)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isBestOption) 6.dp else 2.dp
        ),
        border = if (isBestOption || position == 1) 
            BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f)) 
            else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (isBestOption) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF4CAF50))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                stringResource(R.string.compare_best_option),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Text(
                        product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF333333)
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "#${position + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.compare_edit_product),
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { onDelete(product) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.compare_delete_product),
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val unitRes = when(product.unit) {
                    "kg" -> R.string.unit_kg
                    "g" -> R.string.unit_g
                    "l" -> R.string.unit_l
                    "ml" -> R.string.unit_ml
                    "ud" -> R.string.unit_units
                    "pack" -> R.string.unit_pack
                    "box" -> R.string.unit_box
                    else -> R.string.unit_units
                }
                val unitName = stringResource(unitRes)

                val baseUnitRes = when(product.baseUnit) {
                    "kg" -> R.string.unit_kg
                    "l" -> R.string.unit_l
                    "g" -> R.string.unit_g
                    "ml" -> R.string.unit_ml
                    "ud" -> R.string.unit_units
                    "pack" -> R.string.unit_pack
                    "box" -> R.string.unit_box
                    else -> R.string.unit_units
                }
                val baseUnitName = stringResource(baseUnitRes)

                Column {
                    val quantityText = if (product.unitsPerPackage > 1) {
                        "${product.unitsPerPackage} x ${product.quantityPerUnit} $unitName (${product.totalQuantity} $unitName)"
                    } else {
                        "${product.quantityPerUnit} $unitName"
                    }
                    Text(
                        quantityText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        "€${String.format(Locale.getDefault(), "%.2f", product.price)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDarkMode) Color.White else Color(0xFF1A1A1A)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        stringResource(R.string.compare_price_per, baseUnitName),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        "€${String.format(Locale.getDefault(), "%.4f", product.pricePerBaseUnit)}/$baseUnitName",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                }
            }
            
            if (position > 0 && bestPricePerBaseUnit > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                val difference = product.pricePerBaseUnit - bestPricePerBaseUnit
                val percentage = (difference / bestPricePerBaseUnit * 100).roundToInt()
                
                if (percentage > 0) {
                    Text(
                        stringResource(R.string.compare_more_expensive, percentage),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF44336),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
