package com.k3n3sh.gsecom.presentation.navigation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.k3n3sh.gsecom.MainActivity
import com.k3n3sh.gsecom.R
import com.k3n3sh.gsecom.domain.model.Product
import com.k3n3sh.gsecom.presentation.detail.ProductDetailScreen
import com.k3n3sh.gsecom.presentation.list.ProductListScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProductListRoute

// Name must match PRODUCT_ID_KEY
@Serializable
data class ProductDetailRoute(val productId: Long)

const val ProductDeepLinkBase: String = "gsecom://product"

// gsecom://product/{productId}
fun productDeepLink(productId: Long): Uri {
    return "$ProductDeepLinkBase/$productId".toUri()
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ProductListRoute,
        modifier = modifier,
    ) {
        composable<ProductListRoute> { entry ->
            ProductListScreen(
                onProductClick = { productId ->
                    // Ignore double taps
                    if (entry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(ProductDetailRoute(productId))
                    }
                },
            )
        }
        composable<ProductDetailRoute>(
            deepLinks = listOf(navDeepLink<ProductDetailRoute>(basePath = ProductDeepLinkBase)),
        ) {
            ProductDetailScreen(onBack = { navController.navigateUp() })
        }
    }
}

private const val ChannelId = "products"

// Demo: tapping it opens the product via the deep link
fun showProductNotification(context: Context, product: Product) {
    val manager = context.getSystemService(NotificationManager::class.java)
    val channel = NotificationChannel(
        ChannelId,
        context.getString(R.string.notification_channel_products),
        NotificationManager.IMPORTANCE_DEFAULT,
    )
    manager.createNotificationChannel(channel)

    val intent = Intent(Intent.ACTION_VIEW, productDeepLink(product.id), context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    val pendingIntent = PendingIntent.getActivity(
        context,
        product.id.toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    val notification = Notification.Builder(context, ChannelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(product.title)
        .setContentText(context.getString(R.string.notification_text))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()
    manager.notify(product.id.toInt(), notification)
}
