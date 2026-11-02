package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ClaimStatus
import com.example.data.model.GameEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GameClaim AI", appName)
  }

  @Test
  fun `verify game entity creation and claim statuses`() {
    val game = GameEntity(
      id = "test_game_1",
      title = "Cyberpunk Odyssey",
      description = "Free action RPG",
      platform = "Epic Games",
      storeUrl = "https://store.epicgames.com/",
      thumbnailUrl = "https://example.com/thumb.jpg",
      originalPrice = 59.99,
      worthString = "$59.99",
      endDate = "2026-12-31"
    )

    assertEquals("Cyberpunk Odyssey", game.title)
    assertEquals(59.99, game.originalPrice, 0.01)
    assertEquals("Successfully Claimed", ClaimStatus.CLAIMED.displayName)
    assertTrue(game.isWorthIt)
  }
}
