package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.TranslationEntity
import com.example.ui.TranslatorViewModel
import com.example.ui.theme.BabylonGold
import com.example.ui.theme.EmeraldIraqi
import com.example.ui.theme.IraqiNavyBorder
import com.example.ui.theme.IraqiNavyCard
import com.example.ui.theme.IraqiNavyCardLight
import com.example.ui.theme.IraqiNavyDeep
import com.example.ui.theme.SkyAzure
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: TranslatorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val historyItems by viewModel.historyItems.collectAsState()
    val searchQuery by viewModel.historySearchQuery.collectAsState()
    val favoritesOnly by viewModel.historyFilterFavoritesOnly.collectAsState()

    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IraqiNavyDeep)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "سجل الترجمات",
                    style = MaterialTheme.typography.titleLarge,
                    color = BabylonGold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${historyItems.size} ترجمة محفوظة محلياً",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (historyItems.isNotEmpty()) {
                IconButton(
                    onClick = { showClearConfirmDialog = true },
                    modifier = Modifier.testTag("clear_all_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "مسح الكل",
                        tint = Color(0xFFF87171)
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setHistorySearchQuery(it) },
            placeholder = { Text("ابحث في سجل الترجمات...", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "بحث",
                    tint = BabylonGold
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setHistorySearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "مسح البحث",
                            tint = Color.Gray
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .testTag("search_history_input"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BabylonGold,
                unfocusedBorderColor = IraqiNavyBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = IraqiNavyCard,
                unfocusedContainerColor = IraqiNavyCard
            )
        )

        // Filter Chips (All vs Favorites)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !favoritesOnly,
                onClick = { viewModel.setHistoryFilterFavoritesOnly(false) },
                label = { Text("جميع الترجمات") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BabylonGold.copy(alpha = 0.2f),
                    selectedLabelColor = BabylonGold
                ),
                border = BorderStroke(1.dp, if (!favoritesOnly) BabylonGold else IraqiNavyBorder),
                modifier = Modifier.testTag("filter_all_history_chip")
            )

            FilterChip(
                selected = favoritesOnly,
                onClick = { viewModel.setHistoryFilterFavoritesOnly(true) },
                label = { Text("المفضلة ⭐") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BabylonGold.copy(alpha = 0.2f),
                    selectedLabelColor = BabylonGold
                ),
                border = BorderStroke(1.dp, if (favoritesOnly) BabylonGold else IraqiNavyBorder),
                modifier = Modifier.testTag("filter_favorites_history_chip")
            )
        }

        // List or Empty State
        if (historyItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(IraqiNavyCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = BabylonGold,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (favoritesOnly) "لا توجد ترجمات في المفضلة" else "سجل الترجمات فارغ",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (favoritesOnly) "انقر على علامة النجمة ⭐ بجانب أي ترجمة لإضافتها للمفضلة."
                        else "عند ترجمة أي نص، سيتم حفظه تلقائياً هنا في سجلك الخاص للوصول إليه حتى بدون إنترنت.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(historyItems, key = { it.id }) { item ->
                    HistoryItemCard(
                        item = item,
                        onItemClick = { viewModel.loadHistoryItem(item) },
                        onToggleFavorite = { viewModel.toggleFavorite(item.id, item.isFavorite) },
                        onDelete = { viewModel.deleteHistoryItem(item.id) },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Translation", item.translatedText))
                            Toast.makeText(context, "تم نسخ الترجمة", Toast.LENGTH_SHORT).show()
                        },
                        onSpeak = {
                            viewModel.speak(item.translatedText, item.targetLanguageCode)
                        }
                    )
                }
            }
        }
    }

    // Confirmation dialog for clearing history
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = "مسح سجل الترجمات بالكامل؟",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "هل أنت متأكد من رغبتك في حذف جميع الترجمات المحفوظة في السجل؟ لا يمكن التراجع عن هذا الإجراء.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearConfirmDialog = false
                        Toast.makeText(context, "تم مسح السجل بنجاح", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("مسح الكل", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("إلغاء", color = Color.LightGray)
                }
            },
            containerColor = IraqiNavyCard
        )
    }
}

@Composable
fun HistoryItemCard(
    item: TranslationEntity,
    onItemClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onSpeak: () -> Unit
) {
    val dateStr = remember(item.timestamp) {
        val sdf = SimpleDateFormat("yyyy/MM/dd • HH:mm", Locale.getDefault())
        sdf.format(Date(item.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .testTag("history_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IraqiNavyCard),
        border = BorderStroke(1.dp, IraqiNavyBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with Languages and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.sourceLanguageName,
                        style = MaterialTheme.typography.labelMedium,
                        color = SkyAzure,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.targetLanguageName,
                        style = MaterialTheme.typography.labelMedium,
                        color = BabylonGold,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "المفضلة",
                            tint = if (item.isFavorite) BabylonGold else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                color = IraqiNavyBorder.copy(alpha = 0.5f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Source Text
            Text(
                text = item.sourceText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Translated Text
            Text(
                text = item.translatedText,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 3
            )

            // Dialect Note if present
            if (!item.dialectNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💡 ${item.dialectNote}",
                    style = MaterialTheme.typography.bodySmall,
                    color = BabylonGold.copy(alpha = 0.8f)
                )
            }

            // Actions row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IraqiNavyCardLight,
                    modifier = Modifier.clickable { onItemClick() }
                ) {
                    Text(
                        text = "اضغط للفتح في المترجم ↗",
                        style = MaterialTheme.typography.bodySmall,
                        color = SkyAzure,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row {
                    IconButton(onClick = onSpeak, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "استماع",
                            tint = SkyAzure,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "نسخ",
                            tint = EmeraldIraqi,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "حذف",
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
