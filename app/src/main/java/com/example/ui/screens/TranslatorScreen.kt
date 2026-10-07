package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Language
import com.example.ui.TranslatorViewModel
import com.example.ui.components.LanguageSelectorSheet
import com.example.ui.theme.BabylonGold
import com.example.ui.theme.BabylonGoldDark
import com.example.ui.theme.BabylonGoldLight
import com.example.ui.theme.EmeraldIraqi
import com.example.ui.theme.IraqiNavyBorder
import com.example.ui.theme.IraqiNavyCard
import com.example.ui.theme.IraqiNavyCardLight
import com.example.ui.theme.IraqiNavyDeep
import com.example.ui.theme.SkyAzure

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslatorScreen(
    viewModel: TranslatorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val inputText by viewModel.inputText.collectAsState()
    val sourceLang by viewModel.sourceLanguage.collectAsState()
    val targetLang by viewModel.targetLanguage.collectAsState()
    val isIraqiMode by viewModel.isIraqiDialectMode.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()
    val result by viewModel.translationResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showSourceSelector by remember { mutableStateOf(false) }
    var showTargetSelector by remember { mutableStateOf(false) }
    var showFullscreenDialog by remember { mutableStateOf(false) }

    // Speech-to-Text recognizer
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val spokenText = activityResult.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.setInputText(spokenText)
                viewModel.translate()
            }
        }
    }

    val quickPhrases = listOf(
        "شكو ماكو؟",
        "عاشت إيدك يا غالي",
        "تدلل يا بعد روحي",
        "لعد وين رايحين هسه؟",
        "خوش ولد ومعدل",
        "حيل مشتاقلك",
        "بيش هذا تكدر تسويلي مجال؟",
        "شلونك شخبارك عساك بخير"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IraqiNavyDeep)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // App Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = IraqiNavyCard),
            border = BorderStroke(1.dp, IraqiNavyBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(BabylonGold, BabylonGoldDark))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🇮🇶", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "المترجم العراقي",
                            style = MaterialTheme.typography.titleLarge,
                            color = BabylonGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "مدعوم بالذكاء الاصطناعي • فهم عميق للهجات",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldIraqi.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, EmeraldIraqi.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldIraqi)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI نشط",
                            color = EmeraldIraqi,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Iraqi Mode Toggle Chip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = isIraqiMode,
                onClick = { viewModel.toggleIraqiMode() },
                label = {
                    Text(
                        text = if (isIraqiMode) "🇮🇶 وضع اللهجة العراقية (مفعل)" else "العربية الفصحى فقط",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "اللهجة العراقية",
                        tint = if (isIraqiMode) BabylonGold else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BabylonGold.copy(alpha = 0.15f),
                    selectedLabelColor = BabylonGold,
                    selectedLeadingIconColor = BabylonGold
                ),
                border = BorderStroke(1.dp, if (isIraqiMode) BabylonGold else IraqiNavyBorder),
                modifier = Modifier.testTag("iraqi_dialect_toggle_chip")
            )

            Text(
                text = "دقة لغوية بشرية",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Language Selector Row
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = IraqiNavyCard),
            border = BorderStroke(1.dp, IraqiNavyBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Source Language Button
                Surface(
                    onClick = { showSourceSelector = true },
                    shape = RoundedCornerShape(12.dp),
                    color = IraqiNavyCardLight,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("source_language_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = sourceLang.flag, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = sourceLang.nameAr,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }

                // Swap Button
                IconButton(
                    onClick = { viewModel.swapLanguages() },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(44.dp)
                        .background(BabylonGold.copy(alpha = 0.15f), CircleShape)
                        .testTag("swap_languages_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "تبديل اللغات",
                        tint = BabylonGold
                    )
                }

                // Target Language Button
                Surface(
                    onClick = { showTargetSelector = true },
                    shape = RoundedCornerShape(12.dp),
                    color = IraqiNavyCardLight,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("target_language_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = targetLang.flag, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = targetLang.nameAr,
                            style = MaterialTheme.typography.titleMedium,
                            color = BabylonGold,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Source Text Input Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = IraqiNavyCard),
            border = BorderStroke(1.dp, IraqiNavyBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "النص المراد ترجمته (${sourceLang.nameAr})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row {
                        // Paste button
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                                if (!clip.isNullOrBlank()) {
                                    viewModel.setInputText(clip)
                                    Toast.makeText(context, "تم لصق النص", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("paste_text_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "لصق",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Speech Input (Mic)
                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(
                                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                        )
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-IQ")
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث الآن باللغة أو اللهجة التي تريدها...")
                                    }
                                    speechLauncher.launch(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "التعرف الصوتي غير متوفر على هذا الجهاز", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("voice_input_mic_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "تحدث بالصوت",
                                tint = BabylonGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Clear button
                        if (inputText.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.clearInput() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("clear_input_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "مسح",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { viewModel.setInputText(it) },
                    placeholder = {
                        Text(
                            text = "اكتب أي نص أو جملة للترجمة فوراً...\nمثال: شكو ماكو، تدلل، عاشت ايدك، Hello my friend...",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("translation_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BabylonGold,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = IraqiNavyDeep.copy(alpha = 0.5f),
                        unfocusedContainerColor = IraqiNavyDeep.copy(alpha = 0.5f)
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (inputText.isNotBlank()) {
                        IconButton(
                            onClick = { viewModel.speak(inputText, sourceLang.code) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("speak_source_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "استماع للنص الأصلي",
                                tint = SkyAzure,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(32.dp))
                    }

                    Text(
                        text = "${inputText.length} حرف",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Translate Button
        Button(
            onClick = { viewModel.translate() },
            enabled = !isTranslating && inputText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("submit_translate_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BabylonGold,
                contentColor = IraqiNavyDeep,
                disabledContainerColor = BabylonGold.copy(alpha = 0.4f),
                disabledContentColor = IraqiNavyDeep.copy(alpha = 0.6f)
            )
        ) {
            if (isTranslating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = IraqiNavyDeep,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "جاري الترجمة بالذكاء الاصطناعي...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ترجمة بواسطة الذكاء الاصطناعي ⚡",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Error message if any
        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = Color(0xFFFCA5A5),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Translation Result Card
        AnimatedVisibility(
            visible = result != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            result?.let { res ->
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("translation_result_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = IraqiNavyCard),
                    border = BorderStroke(1.5.dp, BabylonGold.copy(alpha = 0.7f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = res.targetLanguage.flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = res.targetLanguage.nameAr,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = BabylonGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row {
                                // Fullscreen Zoom button
                                IconButton(
                                    onClick = { showFullscreenDialog = true },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("zoom_translation_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = "تكبير النص",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Audio TTS button
                                IconButton(
                                    onClick = { viewModel.speak(res.translatedText, res.targetLanguage.code) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("speak_result_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "استماع للنطق",
                                        tint = SkyAzure
                                    )
                                }

                                // Copy button
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Iraqi Translation", res.translatedText)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "تم نسخ الترجمة بنجاح!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("copy_result_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "نسخ الترجمة",
                                        tint = EmeraldIraqi
                                    )
                                }

                                // Share button
                                IconButton(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "مترجم عبر تطبيق المترجم العراقي 🇮🇶:\n\nالنص: ${res.sourceText}\nالترجمة: ${res.translatedText}"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة الترجمة"))
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("share_result_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "مشاركة الترجمة",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            color = IraqiNavyBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        // Main Translated Text
                        Text(
                            text = res.translatedText,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            lineHeight = 32.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Pronunciation guide if present
                        if (!res.pronunciation.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = IraqiNavyDeep,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RecordVoiceOver,
                                        contentDescription = "طريقة النطق",
                                        tint = BabylonGoldLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "طريقة النطق: ${res.pronunciation}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BabylonGoldLight
                                    )
                                }
                            }
                        }

                        // Dialect / Cultural note if present
                        if (!res.dialectNote.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = IraqiNavyCardLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "ملاحظة",
                                        tint = SkyAzure,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = res.dialectNote ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SkyAzure
                                    )
                                }
                            }
                        }

                        // Alternatives list
                        if (res.alternatives.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "بدائل وصيغ أخرى:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                res.alternatives.forEach { alt ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = IraqiNavyDeep,
                                        border = BorderStroke(0.5.dp, IraqiNavyBorder),
                                        modifier = Modifier.clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Alt", alt))
                                            Toast.makeText(context, "تم نسخ الصيغة البديلة", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Text(
                                            text = alt,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.LightGray,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Phrases Section
        Text(
            text = "🇮🇶 عبارات عراقية شائعة للتجربة الفورية:",
            style = MaterialTheme.typography.titleSmall,
            color = BabylonGold,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(quickPhrases) { phrase ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = IraqiNavyCard,
                    border = BorderStroke(1.dp, IraqiNavyBorder),
                    modifier = Modifier.clickable {
                        viewModel.setInputText(phrase)
                        viewModel.translate()
                    }
                ) {
                    Text(
                        text = phrase,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Source Language Sheet
    if (showSourceSelector) {
        LanguageSelectorSheet(
            title = "اختر لغة النص الأصلي",
            languages = Language.allSourceLanguages,
            selectedLanguage = sourceLang,
            onLanguageSelected = { viewModel.setSourceLanguage(it) },
            onDismiss = { showSourceSelector = false }
        )
    }

    // Target Language Sheet
    if (showTargetSelector) {
        LanguageSelectorSheet(
            title = "اختر اللغة المراد الترجمة إليها",
            languages = Language.allTargetLanguages,
            selectedLanguage = targetLang,
            onLanguageSelected = { viewModel.setTargetLanguage(it) },
            onDismiss = { showTargetSelector = false }
        )
    }

    // Fullscreen View Dialog
    if (showFullscreenDialog && result != null) {
        Dialog(onDismissRequest = { showFullscreenDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                color = IraqiNavyDeep,
                border = BorderStroke(2.dp, BabylonGold)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${result?.targetLanguage?.flag} ${result?.targetLanguage?.nameAr}",
                        style = MaterialTheme.typography.titleMedium,
                        color = BabylonGold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = result?.translatedText ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 36.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showFullscreenDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = BabylonGold, contentColor = IraqiNavyDeep),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("إغلاق", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
