package com.example.testapplication.ui.screens

import java.util.Locale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.testapplication.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testapplication.model.Product
import com.example.testapplication.model.Offer
import com.example.testapplication.model.OfferType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    isDarkMode: Boolean,
    existingProduct: Product? = null,
    onProductAction: (Product) -> Unit
) {
    var name by remember { mutableStateOf(existingProduct?.name ?: "") }
    var price by remember { mutableStateOf(existingProduct?.price?.let { String.format(Locale.US, "%.2f", it) } ?: "") }
    var unitsPerPackage by remember { mutableStateOf(existingProduct?.unitsPerPackage?.toString() ?: "1") }
    var quantityPerUnit by remember { mutableStateOf(existingProduct?.quantityPerUnit?.let { if(it % 1 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
    var offerType by remember { mutableStateOf(existingProduct?.offer?.type ?: OfferType.NONE) }
    var offerValue1 by remember { mutableStateOf(existingProduct?.offer?.value1?.let { if(it != 0.0) (if(it % 1 == 0.0) it.toInt().toString() else it.toString()) else "" } ?: "") }
    var offerValue2 by remember { mutableStateOf(existingProduct?.offer?.value2?.let { if(it != 0.0) (if(it % 1 == 0.0) it.toInt().toString() else it.toString()) else "" } ?: "") }
    val unitOptions = listOf(
        "kg" to R.string.unit_kg,
        "g" to R.string.unit_g,
        "l" to R.string.unit_l,
        "ml" to R.string.unit_ml,
        "ud" to R.string.unit_units,
        "pack" to R.string.unit_pack,
        "box" to R.string.unit_box
    )
    var selectedUnitId by remember { mutableStateOf(existingProduct?.unit ?: "kg") }
    
    val isEditing = existingProduct != null
    val defaultProductName = stringResource(R.string.product_placeholder_name)

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = if (isDarkMode) Color.White else Color(0xFF1A1A1A),
        unfocusedTextColor = if (isDarkMode) Color.White else Color(0xFF1A1A1A),
        focusedBorderColor = Color(0xFF2E7D32),
        unfocusedBorderColor = if (isDarkMode) Color(0xFF424242) else Color(0xFFBDBDBD),
        focusedLabelColor = Color(0xFF2E7D32),
        unfocusedLabelColor = if (isDarkMode) Color.LightGray else Color(0xFF757575)
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column {
            Text(
                if (isEditing) stringResource(R.string.edit_product_title) else stringResource(R.string.add_product_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stringResource(R.string.product_data_desc),
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF616161)
            )
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.product_name)) },
                placeholder = { Text(stringResource(R.string.product_name_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                singleLine = true
            )
            
            OutlinedTextField(
                value = price,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' || c == ',' }) price = it.replace(',', '.') },
                label = { Text(stringResource(R.string.product_price)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(Icons.Filled.Euro, contentDescription = null, tint = Color(0xFF2E7D32)) },
                colors = textFieldColors,
                singleLine = true
            )

            OutlinedTextField(
                value = unitsPerPackage,
                onValueChange = { if (it.all { c -> c.isDigit() }) unitsPerPackage = it },
                label = { Text(stringResource(R.string.product_units_per_package)) },
                placeholder = { Text(stringResource(R.string.product_units_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(Icons.Filled.Layers, contentDescription = null, tint = Color(0xFF2E7D32)) },
                colors = textFieldColors,
                singleLine = true
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quantityPerUnit,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' || c == ',' }) quantityPerUnit = it.replace(',', '.') },
                    label = { Text(stringResource(R.string.product_quantity_per_unit)) },
                    placeholder = { Text(stringResource(R.string.product_quantity_placeholder)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = textFieldColors,
                    singleLine = true
                )
                
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.width(130.dp)
                ) {
                    OutlinedTextField(
                        value = stringResource(unitOptions.find { it.first == selectedUnitId }?.second ?: R.string.unit_kg),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.product_unit_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = textFieldColors,
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                        shape = RoundedCornerShape(14.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        unitOptions.forEach { (id, resId) ->
                            DropdownMenuItem(
                                text = { Text(stringResource(resId)) },
                                onClick = {
                                    selectedUnitId = id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Offer Section
            var offerExpanded by remember { mutableStateOf(false) }
            val offerOptions = listOf(
                OfferType.NONE to R.string.offer_none,
                OfferType.PERCENTAGE_DISCOUNT to R.string.offer_pct_discount,
                OfferType.BUY_X_PAY_Y to R.string.offer_buy_x_pay_y,
                OfferType.NTH_UNIT_DISCOUNT to R.string.offer_nth_unit_pct,
                OfferType.FIXED_PRICE_FOR_X to R.string.offer_fixed_price,
                OfferType.EXTRA_QUANTITY to R.string.offer_extra_qty
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = if (isDarkMode) Color(0xFF424242) else Color(0xFFEEEEEE))
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocalOffer, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.offer_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                ExposedDropdownMenuBox(
                    expanded = offerExpanded,
                    onExpandedChange = { offerExpanded = !offerExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = stringResource(offerOptions.find { it.first == offerType }?.second ?: R.string.offer_none),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.offer_title)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = offerExpanded) },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                        shape = RoundedCornerShape(14.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = offerExpanded,
                        onDismissRequest = { offerExpanded = false }
                    ) {
                        offerOptions.forEach { (type, resId) ->
                            DropdownMenuItem(
                                text = { Text(stringResource(resId)) },
                                onClick = {
                                    offerType = type
                                    offerExpanded = false
                                }
                            )
                        }
                    }
                }

                if (offerType != OfferType.NONE) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val label1 = when (offerType) {
                            OfferType.PERCENTAGE_DISCOUNT -> R.string.offer_value_pct
                            OfferType.BUY_X_PAY_Y -> R.string.offer_value_buy_x
                            OfferType.NTH_UNIT_DISCOUNT -> R.string.offer_value_nth_unit
                            OfferType.FIXED_PRICE_FOR_X -> R.string.offer_value_fixed_qty
                            OfferType.EXTRA_QUANTITY -> R.string.offer_value_extra_qty
                            else -> R.string.offer_none
                        }
                        
                        OutlinedTextField(
                            value = offerValue1,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' || c == ',' }) offerValue1 = it.replace(',', '.') },
                            label = { Text(stringResource(label1)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )

                        if (offerType == OfferType.BUY_X_PAY_Y || offerType == OfferType.FIXED_PRICE_FOR_X || offerType == OfferType.NTH_UNIT_DISCOUNT) {
                            val label2 = when (offerType) {
                                OfferType.BUY_X_PAY_Y -> R.string.offer_value_pay_y
                                OfferType.FIXED_PRICE_FOR_X -> R.string.offer_value_fixed_price
                                OfferType.NTH_UNIT_DISCOUNT -> R.string.offer_value_2nd_disc
                                else -> R.string.offer_none
                            }
                            OutlinedTextField(
                                value = offerValue2,
                                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' || c == ',' }) offerValue2 = it.replace(',', '.') },
                                label = { Text(stringResource(label2)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = textFieldColors,
                                singleLine = true
                            )
                        }
                    }
                }
            }
            
            val priceVal = price.toDoubleOrNull() ?: 0.0
            val unitsVal = unitsPerPackage.toIntOrNull() ?: 1
            val quantVal = quantityPerUnit.toDoubleOrNull() ?: 0.0
            
            if (quantVal > 0 && priceVal > 0) {
                val tempProduct = Product(
                    name = name,
                    price = priceVal,
                    unitsPerPackage = unitsVal,
                    quantityPerUnit = quantVal,
                    unit = selectedUnitId,
                    offer = Offer(
                        type = offerType,
                        value1 = offerValue1.toDoubleOrNull() ?: 0.0,
                        value2 = offerValue2.toDoubleOrNull() ?: 0.0
                    )
                )
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDarkMode) Color(0xFF2E7D32).copy(alpha = 0.15f) else Color(0xFF2E7D32).copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.product_calculation_title), style = MaterialTheme.typography.labelMedium, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                             Column {
                                 val unitRes = when(tempProduct.unit) {
                                     "kg" -> R.string.unit_kg
                                     "g" -> R.string.unit_g
                                     "l" -> R.string.unit_l
                                     "ml" -> R.string.unit_ml
                                     "ud" -> R.string.unit_units
                                     "pack" -> R.string.unit_pack
                                     "box" -> R.string.unit_box
                                     else -> R.string.unit_units
                                 }
                                 Text(stringResource(R.string.product_total_label), style = MaterialTheme.typography.bodySmall, color = if (isDarkMode) Color.LightGray else Color(0xFF616161))
                                 Text("${tempProduct.totalQuantity} ${stringResource(unitRes)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color.White else Color.Black)
                             }
                            Column(horizontalAlignment = Alignment.End) {
                                val baseUnitRes = when(tempProduct.baseUnit) {
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
                                Text(stringResource(R.string.compare_price_per, baseUnitName), style = MaterialTheme.typography.bodySmall, color = if (isDarkMode) Color.LightGray else Color(0xFF616161))
                                Text(
                                    "€${String.format(Locale.getDefault(), "%.4f", tempProduct.pricePerBaseUnit)}/$baseUnitName",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2E7D32)
                                )
                                if (tempProduct.savingPercentage > 0) {
                                    Text(
                                        stringResource(R.string.offer_savings, tempProduct.savingPercentage),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = {
                val priceValue = price.toDoubleOrNull()
                val unitsValue = unitsPerPackage.toIntOrNull() ?: 1
                val quantValue = quantityPerUnit.toDoubleOrNull()
                
                if (priceValue != null && quantValue != null) {
                    onProductAction(
                        Product(
                            id = existingProduct?.id ?: System.currentTimeMillis(),
                            name = if (name.isBlank()) defaultProductName else name.trim(),
                            price = priceValue,
                            unitsPerPackage = unitsValue,
                            quantityPerUnit = quantValue,
                            unit = selectedUnitId,
                            offer = Offer(
                                type = offerType,
                                value1 = offerValue1.toDoubleOrNull() ?: 0.0,
                                value2 = offerValue2.toDoubleOrNull() ?: 0.0
                            )
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            enabled = price.isNotBlank() && quantityPerUnit.isNotBlank(),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(if (isEditing) Icons.Filled.CheckCircle else Icons.Filled.AddCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                if (isEditing) stringResource(R.string.product_save_changes) else stringResource(R.string.product_add_button),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }
    }
}
