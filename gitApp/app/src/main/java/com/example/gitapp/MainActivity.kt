package com.example.gitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gitapp.ui.component.AuthScreen
import com.example.gitapp.ui.component.DetailInfoScreen
import com.example.gitapp.ui.component.RepositoriesListScreen
import com.example.gitapp.ui.theme.GitAppTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitAppTheme {
                AppNavigation()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    GitAppTheme {
        AppNavigation()
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { innerPadding ->
        NavHost(
            navController,
            "auth",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("auth") {
                AuthScreen(navController)
            }

            composable("repos") {
                RepositoriesListScreen(navController)
            }

            composable(
                route = "details/{owner}/{repo}",
                arguments = listOf(
                    navArgument("owner") { type = NavType.StringType },
                    navArgument("repo") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val owner = backStackEntry.arguments?.getString("owner") ?: ""
                val repo = backStackEntry.arguments?.getString("repo") ?: ""

                DetailInfoScreen(
                    navController = navController,
                    owner = owner,
                    repo = repo
                )
            }
        }
    }
}

