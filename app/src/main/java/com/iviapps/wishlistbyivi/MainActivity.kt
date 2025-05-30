package com.iviapps.wishlistbyivi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.iviapps.wishlistbyivi.ui.screens.AdminScreen
import com.iviapps.wishlistbyivi.ui.screens.LoginScreen
import com.iviapps.wishlistbyivi.ui.screens.MainScreen
import com.iviapps.wishlistbyivi.ui.theme.WishListByIviTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WishListByIviTheme {
                var isLoggedIn by remember { mutableStateOf(false) }
                var isAdmin by remember { mutableStateOf(false) }

                when {
                    !isLoggedIn -> LoginScreen { admin ->
                        isLoggedIn = true
                        isAdmin = admin
                    }

                    isAdmin -> AdminScreen {
                        isLoggedIn = false
                        isAdmin = false
                    }

                    else -> MainScreen {
                        isLoggedIn = false
                        isAdmin = false
                    }
                }
            }
        }
    }
}
