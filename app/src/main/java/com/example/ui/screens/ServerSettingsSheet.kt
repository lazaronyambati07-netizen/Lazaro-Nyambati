package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.BoostValidator
import com.example.data.local.SettingsDataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerSettingsSheet(
    currentBaseUrl: String,
    onDismiss: () -> Unit,
    onSaveUrl: (String) -> Boolean
) {
    var draftUrl by remember(currentBaseUrl) { mutableStateOf(currentBaseUrl) }
    var urlError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.server_settings_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(R.string.server_settings_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = draftUrl,
                onValueChange = {
                    draftUrl = it
                    urlError = null
                },
                label = { Text(stringResource(R.string.server_url_label)) },
                placeholder = { Text("https://boostke-api.onrender.com") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Dns,
                        contentDescription = null
                    )
                },
                isError = urlError != null,
                supportingText = {
                    Text(
                        urlError ?: "Endpoint: ${BoostValidator.buildStkEndpointUrl(draftUrl)}"
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("server_url_input")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = {
                        draftUrl = SettingsDataStore.DEFAULT_EMULATOR_BASE_URL
                        urlError = null
                    },
                    label = { Text("Emulator (10.0.2.2:3000)") },
                    modifier = Modifier.testTag("preset_emulator_chip")
                )
                AssistChip(
                    onClick = {
                        draftUrl = SettingsDataStore.DEFAULT_LOCALHOST_BASE_URL
                        urlError = null
                    },
                    label = { Text("Localhost (:3000)") },
                    modifier = Modifier.testTag("preset_localhost_chip")
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "JSON Body: { phone, amount, packageName, link }",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Button(
                onClick = {
                    val saved = onSaveUrl(draftUrl)
                    if (!saved) {
                        urlError = "Weka URL sahihi inayoanza na http:// au https://"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_server_url_button")
            ) {
                Text(stringResource(R.string.server_url_save))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
