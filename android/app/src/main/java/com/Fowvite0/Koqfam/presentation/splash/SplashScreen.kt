package com.Fowvite0.Koqfam.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Fowvite0.Koqfam.R
import com.Fowvite0.Koqfam.core.di.ServiceLocator
import com.Fowvite0.Koqfam.core.theme.BoltIndigo
import com.Fowvite0.Koqfam.core.theme.BoltWhite
import com.Fowvite0.Koqfam.core.theme.CeremonialGold
import com.Fowvite0.Koqfam.core.theme.SurfaceRaised
import com.Fowvite0.Koqfam.core.theme.TextSecondary
import com.Fowvite0.Koqfam.core.theme.TextTertiary
import com.Fowvite0.Koqfam.core.ui.StormBackdrop

private val SplashOverlay = listOf(
    Color(0xCC050510),
    Color(0xF20A0A20),
    Color(0xFF050510)
)

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = ServiceLocator.factory())
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    SplashScreen(finished = finished, onFinished = onFinished)
}

@Composable
fun SplashScreen(
    finished: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    LaunchedEffect(Unit) {
        revealed = true
    }

    val transition = rememberInfiniteTransition(label = "splash-loop")
    val drift by transition.animateFloat(
        initialValue = -14f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud-drift"
    )
    val rimAlpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rim-breath"
    )
    val boltA by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bolt-a"
    )
    val boltB by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, delayMillis = 450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bolt-b"
    )
    val boltC by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, delayMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bolt-c"
    )
    val emblemScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.82f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow),
        label = "emblem-scale"
    )
    val emblemAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "emblem-alpha"
    )

    Box(modifier = modifier.fillMaxSize()) {
        StormBackdrop(overlay = SplashOverlay, artScale = 1.12f)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val bandHeight = size.height * 0.08f
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, Color(0x334169E1), Color.Transparent)
                ),
                topLeft = Offset(drift * 3f, size.height * 0.30f),
                size = Size(size.width, bandHeight)
            )
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, Color(0x267B9BFF), Color.Transparent)
                ),
                topLeft = Offset(-drift * 5f, size.height * 0.68f),
                size = Size(size.width, bandHeight * 1.4f)
            )
            drawBolt(size.width * 0.18f, size.height * 0.12f, size.width * 0.10f, boltA)
            drawBolt(size.width * 0.52f, size.height * 0.07f, size.width * 0.12f, boltB)
            drawBolt(size.width * 0.80f, size.height * 0.15f, size.width * 0.09f, boltC)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(260.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x40E8C06A), Color.Transparent),
                            radius = size.minDimension / 2f
                        ),
                        radius = size.minDimension / 2f
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = Color(0xCC10103A),
                    border = BorderStroke(1.dp, CeremonialGold.copy(alpha = rimAlpha * 0.5f)),
                    shadowElevation = 18.dp,
                    modifier = Modifier
                        .size(196.dp)
                        .scale(emblemScale)
                        .alpha(emblemAlpha)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = stringResource(R.string.cd_logo),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(148.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = stringResource(R.string.splash_title),
                style = MaterialTheme.typography.displayLarge,
                color = BoltWhite,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.splash_title_second),
                style = MaterialTheme.typography.headlineSmall.copy(letterSpacing = 10.sp),
                color = CeremonialGold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            LinearProgressIndicator(
                color = BoltIndigo,
                trackColor = SurfaceRaised,
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBolt(
    x: Float,
    y: Float,
    scale: Float,
    progress: Float
) {
    if (progress <= 0.02f) return
    val path = Path().apply {
        moveTo(x, y)
        lineTo(x - scale * 0.35f, y + scale * 0.9f)
        lineTo(x + scale * 0.12f, y + scale * 0.85f)
        lineTo(x - scale * 0.2f, y + scale * 1.9f)
        lineTo(x + scale * 0.5f, y + scale * 0.75f)
        lineTo(x + scale * 0.05f, y + scale * 0.8f)
        lineTo(x + scale * 0.3f, y)
    }
    drawPath(
        path = path,
        color = BoltWhite.copy(alpha = progress * 0.75f),
        style = Stroke(width = 2.2f)
    )
}
