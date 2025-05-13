package com.github.ansgrb.wingit


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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

        LaunchedEffect(Unit) {
            game.start()
            spriteState.start()
        }
        LaunchedEffect(game.status) {
            while (game.status == GameStatus.STARTED) {
                withFrameMillis {
                    game.update()
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
                drawSpriteView(
                    spriteState = spriteState,
                    spriteSpec = spriteSpec,
                    currentFrame = currentFrame,
                    image = sheetImage,
                    offset = IntOffset(
                        x = game.winged.x.toInt(),
                        y = game.winged.y.toInt()
                    )
                )
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
            }
        }
    }
}