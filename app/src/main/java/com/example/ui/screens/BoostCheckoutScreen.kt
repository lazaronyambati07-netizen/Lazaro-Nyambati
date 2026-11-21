package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.BoostValidator
import com.example.model.BoostPackage
import com.example.model.SocialPlatform
import com.example.model.StkPushState
import com.example.ui.BoostUiState
import com.example.ui.theme.LocalBoostColors

@Composable
fun BoostCheckoutScreen(
    uiState: BoostUiState,
    onSelectPlatform: (SocialPlatform) -> Unit,
    onSelectPackage: (BoostPackage) -> Unit,
    onTargetLinkChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSubmitStkPush: () -> Unit,
    onDismissStatus: () -> Unit,
    onOpenServerSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boostColors = LocalBoostColors.current
    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 760.dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = if (isWide) 1040.dp else 600.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            HeroBannerCard(
                serverBaseUrl = uiState.serverBaseUrl,
                onOpenServerSettings = onOpenServerSettings
            )

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1.05f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PackageSelectionSection(
                            selectedPlatform = uiState.selectedPlatform,
                            packages = uiState.packages,
                            selectedPackage = uiState.selectedPackage,
                            onSelectPlatform = onSelectPlatform,
                            onSelectPackage = onSelectPackage
                        )
                    }

                    Column(
                        modifier = Modifier.weight(0.95f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CheckoutFormCard(
                            uiState = uiState,
                            mpesaGreen = boostColors.mpesaGreen,
                            onTargetLinkChange = onTargetLinkChange,
                            onPhoneChange = onPhoneChange,
                            onSubmitStkPush = onSubmitStkPush
                        )
                        StkStatusBanner(
                            status = uiState.stkPushState,
                            onDismiss = onDismissStatus,
                            onOpenServerSettings = onOpenServerSettings
                        )
                    }
                }
            } else {
                PackageSelectionSection(
                    selectedPlatform = uiState.selectedPlatform,
                    packages = uiState.packages,
                    selectedPackage = uiState.selectedPackage,
                    onSelectPlatform = onSelectPlatform,
                    onSelectPackage = onSelectPackage
                )

                CheckoutFormCard(
                    uiState = uiState,
                    mpesaGreen = boostColors.mpesaGreen,
                    onTargetLinkChange = onTargetLinkChange,
                    onPhoneChange = onPhoneChange,
                    onSubmitStkPush = onSubmitStkPush
                )

                StkStatusBanner(
                    status = uiState.stkPushState,
                    onDismiss = onDismissStatus,
                    onOpenServerSettings = onOpenServerSettings
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HeroBannerCard(
    serverBaseUrl: String,
    onOpenServerSettings: () -> Unit
) {
    val endpoint = BoostValidator.buildStkEndpointUrl(serverBaseUrl)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 152.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_boost_hero),
                contentDescription = stringResource(R.string.hero_title),
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xE80F172A),
                                Color(0xBF1E1B4B),
                                Color(0x8A0F172A)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0x3322C55E),
                        shape = RoundedCornerShape(999.dp),
                        border = BorderStroke(1.dp, Color(0x6622C55E))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = null,
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "M-PESA STK PUSH",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF86EFAC)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable(onClick = onOpenServerSettings)
                            .minimumInteractiveComponentSize()
                            .testTag("server_settings_button"),
                        color = Color(0x33FFFFFF),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = stringResource(R.string.server_settings_title),
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = endpoint.removePrefix("http://").removePrefix("https://"),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 150.dp)
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.hero_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                Text(
                    text = stringResource(R.string.hero_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFE2E8F0)
                )
            }
        }
    }
}

@Composable
private fun PackageSelectionSection(
    selectedPlatform: SocialPlatform,
    packages: List<BoostPackage>,
    selectedPackage: BoostPackage,
    onSelectPlatform: (SocialPlatform) -> Unit,
    onSelectPackage: (BoostPackage) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = stringResource(R.string.step_choose_package),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SocialPlatform.entries.forEach { platform ->
                val isSelected = platform == selectedPlatform
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectPlatform(platform) },
                    label = {
                        Text(
                            text = platform.label,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = platformIcon(platform),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("platform_chip_${platform.name.lowercase()}")
                )
            }
        }

        val rows = packages.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { pkg ->
                        PackageOptionCard(
                            pkg = pkg,
                            isSelected = pkg.id == selectedPackage.id,
                            onClick = { onSelectPackage(pkg) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PackageOptionCard(
    pkg: BoostPackage,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boostColors = LocalBoostColors.current
    val borderColor = if (isSelected) boostColors.mpesaGreen else MaterialTheme.colorScheme.outline
    val containerColor = if (isSelected) {
        boostColors.mpesaContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .heightIn(min = 124.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .testTag("package_card_${pkg.id}"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isSelected) {
                        boostColors.mpesaGreen.copy(alpha = 0.16f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = platformIcon(pkg.platform),
                            contentDescription = null,
                            tint = if (isSelected) boostColors.onMpesaContainer else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = pkg.platform.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) boostColors.onMpesaContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Selected",
                        tint = boostColors.mpesaGreen,
                        modifier = Modifier.size(20.dp)
                    )
                } else if (pkg.badge != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = pkg.badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = pkg.shortTitle,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = pkg.deliveryHint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "KES ${pkg.amountKes}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = boostColors.mpesaGreen
            )
        }
    }
}

@Composable
private fun CheckoutFormCard(
    uiState: BoostUiState,
    mpesaGreen: Color,
    onTargetLinkChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSubmitStkPush: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val isWaiting = uiState.stkPushState is StkPushState.Waiting

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.step_target_link),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = uiState.targetLink,
                    onValueChange = onTargetLinkChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("target_link_input"),
                    placeholder = { Text(stringResource(R.string.hint_target_link)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Link,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (uiState.targetLink.isNotEmpty()) {
                            IconButton(
                                onClick = { onTargetLinkChange("") },
                                modifier = Modifier.testTag("clear_link_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = stringResource(R.string.clear_text)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    clipboard.getText()?.text?.let { pasted ->
                                        onTargetLinkChange(pasted.trim())
                                    }
                                },
                                modifier = Modifier.testTag("paste_link_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentPaste,
                                    contentDescription = stringResource(R.string.paste_link)
                                )
                            }
                        }
                    },
                    isError = uiState.targetLinkError != null,
                    supportingText = uiState.targetLinkError?.let { err -> { Text(err) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next
                    ),
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.step_mpesa_phone),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = uiState.phone,
                    onValueChange = onPhoneChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mpesa_phone_input"),
                    placeholder = { Text(stringResource(R.string.hint_mpesa_phone)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.PhoneAndroid,
                            contentDescription = null
                        )
                    },
                    isError = uiState.phoneError != null,
                    supportingText = {
                        Text(uiState.phoneError ?: stringResource(R.string.phone_supporting_text))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onSubmitStkPush()
                        }
                    ),
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = mpesaGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.selectedPackage.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KES ${uiState.selectedPackage.amountKes}",
                        style = MaterialTheme.typography.labelLarge,
                        color = mpesaGreen
                    )
                }
            }

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSubmitStkPush()
                },
                enabled = !isWaiting,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp)
                    .testTag("pay_stk_button"),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = mpesaGreen,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    disabledContentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
            ) {
                if (isWaiting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.cta_sending_prompt),
                        style = MaterialTheme.typography.labelLarge
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.cta_pay_format, uiState.selectedPackage.amountKes),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun StkStatusBanner(
    status: StkPushState,
    onDismiss: () -> Unit,
    onOpenServerSettings: () -> Unit
) {
    val boostColors = LocalBoostColors.current

    AnimatedVisibility(
        visible = status !is StkPushState.Idle,
        enter = fadeIn(spring(stiffness = Spring.StiffnessMediumLow)) + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        when (status) {
            is StkPushState.Idle -> Unit
            is StkPushState.Waiting -> {
                StatusSurface(
                    containerColor = boostColors.waitContainer,
                    contentColor = boostColors.onWaitContainer,
                    icon = Icons.Outlined.Bolt,
                    message = stringResource(R.string.status_waiting_format, status.phone),
                    extraTag = null,
                    showSpinner = true,
                    onDismiss = null,
                    actionButton = null
                )
            }
            is StkPushState.Success -> {
                StatusSurface(
                    containerColor = boostColors.mpesaContainer,
                    contentColor = boostColors.onMpesaContainer,
                    icon = Icons.Outlined.CheckCircle,
                    message = status.message,
                    extraTag = status.checkoutRequestId?.let { "Ref: $it" },
                    showSpinner = false,
                    onDismiss = onDismiss,
                    actionButton = null
                )
            }
            is StkPushState.Error -> {
                val combinedMsg = buildString {
                    append(stringResource(R.string.status_error_prefix, status.message))
                    if (status.isNetworkError) {
                        append("\n")
                        append(stringResource(R.string.status_error_server_hint))
                    }
                }
                StatusSurface(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    icon = Icons.Outlined.ErrorOutline,
                    message = combinedMsg,
                    extraTag = null,
                    showSpinner = false,
                    onDismiss = onDismiss,
                    actionButton = if (status.isNetworkError) {
                        {
                            OutlinedButton(
                                onClick = onOpenServerSettings,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onErrorContainer),
                                modifier = Modifier.testTag("error_open_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Badilisha Server URL",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusSurface(
    containerColor: Color,
    contentColor: Color,
    icon: ImageVector,
    message: String,
    extraTag: String?,
    showSpinner: Boolean,
    onDismiss: (() -> Unit)?,
    actionButton: (@Composable () -> Unit)?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("status_banner"),
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (showSpinner) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(20.dp),
                        color = contentColor,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier
                            .padding(top = 1.dp)
                            .size(22.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor
                    )
                    if (!extraTag.isNullOrBlank()) {
                        Text(
                            text = extraTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor.copy(alpha = 0.85f)
                        )
                    }
                }

                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Funga",
                            tint = contentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (actionButton != null) {
                actionButton()
            }
        }
    }
}

private fun platformIcon(platform: SocialPlatform): ImageVector {
    return when (platform) {
        SocialPlatform.ALL -> Icons.Rounded.Bolt
        SocialPlatform.TIKTOK -> Icons.Rounded.MusicNote
        SocialPlatform.INSTAGRAM -> Icons.Rounded.PhotoCamera
        SocialPlatform.YOUTUBE -> Icons.Rounded.PlayCircle
    }
}
