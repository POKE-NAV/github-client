package com.example.gitapp.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gitapp.R
import com.example.gitapp.ui.viewModelState.auth.AuthAction
import com.example.gitapp.ui.viewModelState.auth.AuthState
import com.example.gitapp.ui.viewModelState.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val token by viewModel.token.collectAsState()
    val actions = viewModel.actions

    LaunchedEffect(Unit) {
        actions.collect { action ->
            when (action) {
                is AuthAction.NavigateToMain -> {
                    navController.navigate("repos") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
//                is AuthAction.ShowToast -> {
//                    android.widget.Toast.makeText(
//                        context,
//                        action.message,
//                        android.widget.Toast.LENGTH_SHORT
//                    ).show()
//                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkAuthState()
    }

    val errorMessage = if (state is AuthState.Error) {
        (state as AuthState.Error).message
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color.White, CircleShape)
        ) {
            Image(
                painter = painterResource(R.drawable.github_icon_logo),
                contentDescription = "GitHub Logo",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.auth_title),
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = token,
            onValueChange = viewModel::updateToken,
            label = { Text(stringResource(com.example.gitapp.R.string.token_label), color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = state !is AuthState.Loading,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = VisualTransformation.None,
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        val scope = rememberCoroutineScope()

        Button(
            onClick = viewModel::onSingInClick,
            enabled = state !is AuthState.Loading && token.isNotEmpty(),
            colors = ButtonColors(Color.Green, Color.White, Color.Gray,Color.White ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (state is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(R.string.sign_in_button), fontSize = 15.sp )
            }
        }
    }
}