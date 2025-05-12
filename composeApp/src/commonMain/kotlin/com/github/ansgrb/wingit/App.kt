package com.github.ansgrb.wingit


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.ansgrb.wingit.domain.GameController
import com.github.ansgrb.wingit.domain.GameStatus
import com.github.ansgrb.wingit.util.ChewyFontFamily
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import wingit.composeapp.generated.resources.Res
import wingit.composeapp.generated.resources.background

@Composable
@Preview
fun App() {
    MaterialTheme {
        var screenWidth by remember { mutableStateOf(0) }
        var screenHeight by remember { mutableStateOf(0) }
        var game = remember(screenHeight, screenWidth) {
            GameController(
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )
        }

        LaunchedEffect(Unit) {
            game.start()
        }
        LaunchedEffect(game.status) {
            when (game.status) {
                GameStatus.IDLE -> {}
                GameStatus.STARTED -> {
                    withFrameMillis {
                        game.update()
                    }
                }
                GameStatus.PAUSED -> {}
                GameStatus.OVER -> {}
                else -> {}
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
                        when (game.status) {
                            GameStatus.IDLE -> game.start()
                            GameStatus.STARTED -> game.jump()
                            GameStatus.PAUSED -> game.resume()
                            GameStatus.OVER -> game.start()
                            else -> {}
                        }
                    }
                    .onGloballyPositioned { // to get the size of the screen
                        if (screenWidth == 0) {
                            screenWidth = it.size.width
                        } else if (screenHeight == 0) {
                            screenHeight = it.size.height
                        }
                    }
            ) {
                game.update()
                drawCircle(
                    color = Color.Red,
                    radius = game.winged.radius,
                    center = Offset( // TODO: add a center value to the winged class
                        x = game.winged.x,
                        y = game.winged.y
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


    }
}