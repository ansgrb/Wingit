package com.github.ansgrb.wingit


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.ansgrb.wingit.domain.GameController
import com.github.ansgrb.wingit.domain.GameStatus
import com.github.ansgrb.wingit.util.ChewyFontFamily
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import wingit.composeapp.generated.resources.Res
import wingit.composeapp.generated.resources.background
import wingit.composeapp.generated.resources.bee_sprite

const val THEWINGT_FRAME_SIZE = 80

@Composable
@Preview
fun App() {
    MaterialTheme {
        var screenWidth by remember { mutableStateOf(0) }
        var screenHeight by remember { mutableStateOf(0) }
        var game by remember { mutableStateOf(GameController()) }

        val spriteState = rememberSpriteState(
            totalFrames = 9,
            framesPerRow = 3
        )

        val spriteSpec = remember {
            SpriteSpec(
                screenWidth = screenWidth.toFloat(),
                default = SpriteSheet(
                    frameWidth = THEWINGT_FRAME_SIZE,
                    frameHeight = THEWINGT_FRAME_SIZE,
                    image = Res.drawable.bee_sprite
                )
            )
        }

        val currentFrame by spriteState.currentFrame.collectAsState()
        val sheetImage = spriteSpec.imageBitmap
        val animateAngle by animateFloatAsState(
            targetValue = when {
                game.theWingedVelocity > game.theWingedMaxVelocity / 1.1 -> 30f
                else -> 0f
            }
        )

        DisposableEffect(Unit) {
            onDispose {
                spriteState.stop()
                spriteState.cleanup() // cleanup the sprite state (coroutine scope)
            }
        }

//        LaunchedEffect(Unit) {
//            game.start()
//            spriteState.start()
//        }
        LaunchedEffect(game.status) {
            while (game.status == GameStatus.STARTED) {
                withFrameMillis {
                    game.update()
                }
                if (game.status == GameStatus.OVER) {
                    spriteState.stop()
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ){
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.background),
                contentDescription =  "background",
                contentScale = ContentScale.Crop
            )

            Canvas(
                modifier =  Modifier
                    .fillMaxSize()
                    .clickable {
                        if (game.status == GameStatus.STARTED) {
                            game.jump()
                        }
                    }
                    .onGloballyPositioned { // to get the size of the screen
                        val size = it.size
                        if (screenWidth != size.width || screenHeight != size.height) {
                            screenWidth = size.width
                            screenHeight = size.height
                            game = game.copy(
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )
                        }
                    }
            ) {
                game.update()
                rotate(
                    degrees = animateAngle,
                    pivot = Offset(
                        x = game.winged.x - game.theWingedRadius,
                        y = game.winged.y - game.theWingedRadius
                    )
                ) {
                    drawSpriteView(
                        spriteState = spriteState,
                        spriteSpec = spriteSpec,
                        currentFrame = currentFrame,
                        image = sheetImage,
                        offset = IntOffset(
                            x = (game.winged.x - game.theWingedRadius).toInt(),
                            y = (game.winged.y - game.theWingedRadius).toInt()
                        )
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 48.dp)
        ) {
            Text(
                text = "BEST: 0",
                fontFamily = ChewyFontFamily(),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            )
            Text(
                text = "0",
                fontFamily = ChewyFontFamily(),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            )
        }
        if (game.status == GameStatus.IDLE) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.Black.copy(alpha = 0.5f)
                    )
            ) {
                Button(
                    onClick = {
                        game.start()
                        spriteState.start()
                    },
                    shape = RoundedCornerShape(size = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "play",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 8.dp, start = 0.dp)
                            .size(50.dp)
                    )
                    Text(
                        text = "START",
                        fontFamily = ChewyFontFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    )
                }
            }
        }


        if (game.status == GameStatus.OVER) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "GAME OVER",
                    color = Color.White,
                    fontFamily = ChewyFontFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                )
                Text(
                    text = "SCORE: 0",
                    color = Color.White,
                    fontFamily = ChewyFontFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
                Button(
                    onClick = {
                        game.restart()
                        spriteState.start()
                    },
                    shape = RoundedCornerShape(size = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "play",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 8.dp, start = 0.dp)
                            .size(50.dp)
                    )
                    Text(
                        text = "RESTART",
                        fontFamily = ChewyFontFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    )
                }
            }
        }
    }
}