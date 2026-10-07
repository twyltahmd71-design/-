package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BabylonGold
import com.example.ui.theme.BabylonGoldDark
import com.example.ui.theme.BabylonGoldLight
import com.example.ui.theme.EmeraldIraqi
import com.example.ui.theme.IraqiNavyBorder
import com.example.ui.theme.IraqiNavyCard
import com.example.ui.theme.IraqiNavyCardLight
import com.example.ui.theme.IraqiNavyDeep
import com.example.ui.theme.SkyAzure

@Composable
fun DeveloperScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val instagramUsername = "i00.07i"
    val instagramUrl = "https://instagram.com/i00.07i"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IraqiNavyDeep)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Developer Profile Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = IraqiNavyCard),
            border = BorderStroke(1.5.dp, BabylonGold.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Developer Avatar with golden ring
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(BabylonGold, BabylonGoldLight, BabylonGoldDark, BabylonGold)
                            )
                        )
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.developer_avatar_1791404578662),
                        contentDescription = "المطور أحمد جلال (أحمد تاشكن)",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name and Title
                Text(
                    text = "المطور أحمد جلال",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "الملقب: أحمد تاشكن",
                    style = MaterialTheme.typography.titleMedium,
                    color = BabylonGold,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = IraqiNavyDeep,
                    border = BorderStroke(1.dp, IraqiNavyBorder)
                ) {
                    Text(
                        text = "مبتكر تطبيق المترجم العراقي 🇮🇶",
                        style = MaterialTheme.typography.bodySmall,
                        color = SkyAzure,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Developer Statement Card (Exact quote requested by user)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("developer_statement_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = IraqiNavyCardLight),
            border = BorderStroke(1.dp, BabylonGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "اقتباس",
                        tint = BabylonGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "رسالة المطور للمستخدمين",
                        style = MaterialTheme.typography.titleMedium,
                        color = BabylonGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(
                    color = IraqiNavyBorder,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Exact text requested by user
                Text(
                    text = "انا المطور احمد جلال\nالملقب احمد تاشكن\nصنعت هذا الموقع بعد فشل تطبيق المترجم المعروف مترجم كوكل اتمنى تستفادون منه\nللتواصل انستا i00. 07i",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start
                )
            }
        }

        // Contact & Social Media Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = IraqiNavyCard),
            border = BorderStroke(1.dp, IraqiNavyBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "التواصل مع المطور:",
                    style = MaterialTheme.typography.titleMedium,
                    color = BabylonGold,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Instagram profile button
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl)).apply {
                                setPackage("com.instagram.android")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback to web browser
                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl))
                            context.startActivity(webIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("open_instagram_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE1306C),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Instagram",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "متابعة وتواصل على إنستغرام ($instagramUsername)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Copy username button
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Instagram", instagramUsername))
                        Toast.makeText(context, "تم نسخ حساب الإنستغرام: $instagramUsername", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("copy_instagram_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BabylonGold),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BabylonGold)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نسخ يوزر الإنستغرام (i00. 07i)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Features comparison / Why Iraqi Translator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = IraqiNavyCard),
            border = BorderStroke(1.dp, IraqiNavyBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BabylonGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "لماذا المترجم العراقي أفضل؟",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                FeaturePoint("فهم حقيقي للأمثال والمصطلحات العراقية (شكو ماكو، تدلل، لعد، فدوة).")
                FeaturePoint("ترجمة سياقية ذكية خالية من الترجمة الحرفية الركيكة لمترجم كوكل.")
                FeaturePoint("سجل محلي فوري يحفظ ترجماتك مع إمكانية البحث والتفضيل بدون إنترنت.")
                FeaturePoint("نطق صوتي تفاعلي وقراءة صحيحة لجميع اللغات العالمية.")
            }
        }

        // Share App Button
        OutlinedButton(
            onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "تطبيق المترجم العراقي 🇮🇶 مدعوم بالذكاء الاصطناعي لترجمة جميع اللغات واللهجة العراقية بدقة فائقة! من تطوير أحمد جلال (أحمد تاشكن) انستا: i00.07i"
                    )
                }
                context.startActivity(Intent.createChooser(shareIntent, "مشاركة تطبيق المترجم العراقي"))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("share_app_button"),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, IraqiNavyBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "مشاركة التطبيق",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("مشاركة التطبيق مع الأصدقاء", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "المترجم العراقي • إصدار 1.0\nصُنع بفخر وإتقان بأيادي عراقية",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun FeaturePoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = EmeraldIraqi,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray
        )
    }
}
