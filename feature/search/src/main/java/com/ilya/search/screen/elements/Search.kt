package com.ilya.search.screen.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ilya.search.R
import com.ilya.theme.LocalColorScheme

@Composable
fun SearchBar(
    onSearch: (String) -> Unit,
    heightOffset: Float,
    heightOffsetLimit: Float,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var inputStateValue by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier
            .background(
                when (heightOffset) {
                    heightOffsetLimit -> LocalColorScheme.current.secondary
                    else -> LocalColorScheme.current.primary
                }
            )
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputStateValue,
            onValueChange = { inputStateValue = it },
            leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
            trailingIcon = {
                if (inputStateValue.isNotBlank()) {
                    IconButton(
                        onClick = {
                            inputStateValue = ""
                            onSearch(inputStateValue)
                        }
                    ) {
                        Icon(imageVector = Icons.Outlined.Close, contentDescription = null)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch(inputStateValue)
                keyboardController?.hide()
            }),
            placeholder = { Text(text = stringResource(id = R.string.search)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = LocalColorScheme.current.valueTextFieldColor,
                unfocusedTextColor = LocalColorScheme.current.valueTextFieldColor,
                focusedContainerColor = LocalColorScheme.current.containerTextFieldColor,
                unfocusedContainerColor = LocalColorScheme.current.containerTextFieldColor,
                focusedTrailingIconColor = LocalColorScheme.current.selectedIconColor,
                unfocusedTrailingIconColor = LocalColorScheme.current.selectedIconColor,
                focusedLeadingIconColor = LocalColorScheme.current.selectedIconColor,
                unfocusedLeadingIconColor = LocalColorScheme.current.selectedIconColor,
                focusedPlaceholderColor = LocalColorScheme.current.placeholderTextFieldColor,
                unfocusedPlaceholderColor = LocalColorScheme.current.placeholderTextFieldColor,
                focusedBorderColor = LocalColorScheme.current.focusedIndicatorTextFieldColor,
                unfocusedBorderColor = LocalColorScheme.current.unfocusedIndicatorTextFieldColor,
                cursorColor = LocalColorScheme.current.primaryTextColor
            )
        )
    }
}
