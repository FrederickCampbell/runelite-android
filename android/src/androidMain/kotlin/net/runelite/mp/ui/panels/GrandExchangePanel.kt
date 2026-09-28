package net.runelite.mp.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.runelite.client.game.ItemManager
import net.runelite.http.api.item.ItemPrice
import net.runelite.mp.ui.RlPalette
import net.runelite.mp.ui.bridge.RuneLiteAccess

/**
 * Grand Exchange — search-only port. The desktop panel also has an Offers tab driven
 * by `GrandExchangeOfferChanged` events, but on Android the user can read offers from
 * the in-game widget overlay; what they can't easily do is search-by-name without
 * the GE booth open. So we surface only that.
 *
 * Result rows show the item's [ItemManager.getItemPrice] value (which respects the
 * Wiki Price config flag) — same price the desktop GE panel renders.
 */
@Composable
internal fun GrandExchangePanel()
{
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<ItemPrice>>(emptyList()) }
    var busy by remember { mutableStateOf(false) }

    // Debounced search — wait 350ms after the last keystroke before hitting
    // ItemManager. Keeps the UI snappy and avoids scanning the 30k-item map on
    // every character.
    LaunchedEffect(query)
    {
        val q = query.trim()
        if (q.isBlank() || q.length < 2)
        {
            results = emptyList()
            return@LaunchedEffect
        }
        busy = true
        delay(350)
        results = withContext(Dispatchers.Default) { GrandExchangeBridge.search(q) }
        busy = false
    }

    PanelScaffold(
        title = "Grand Exchange",
        subtitle = if (busy) "Searching…" else "${results.size} match(es)",
    ) {
        PanelTextInput(value = query, placeholder = "Search item by name", onChange = { query = it })
        Spacer(Modifier.size(8.dp))
        when
        {
            query.isBlank() ->
                Text(
                    "Start typing an item name (≥2 characters).",
                    color = RlPalette.TextSecondary, fontSize = 11.sp,
                )
            results.isEmpty() && !busy ->
                Text("No items match \"$query\".", color = RlPalette.TextSecondary, fontSize = 11.sp)
            else -> {
                for (r in results.take(80))
                {
                    GeRow(r)
                    PanelDivider()
                }
                if (results.size > 80)
                {
                    Spacer(Modifier.size(4.dp))
                    Text(
                        "(+${results.size - 80} more — refine your query)",
                        color = RlPalette.TextSecondary,
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun GeRow(ip: ItemPrice)
{
    val live = remember(ip.id) { GrandExchangeBridge.livePrice(ip.id) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                ip.name,
                color = RlPalette.TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text("id ${ip.id}", color = RlPalette.TextSecondary, fontSize = 10.sp)
        }
        Spacer(Modifier.size(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${"%,d".format(live)} gp",
                color = Color(0xFFFFC857),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (ip.wikiPrice > 0 && ip.wikiPrice != ip.price)
            {
                Text("wiki: ${"%,d".format(ip.wikiPrice)}", color = RlPalette.TextSecondary, fontSize = 10.sp)
            }
        }
    }
}

internal object GrandExchangeBridge
{
    fun search(query: String): List<ItemPrice>
    {
        val mgr = RuneLiteAccess.instance(ItemManager::class.java) ?: return emptyList()
        return try { mgr.search(query) } catch (t: Throwable) { emptyList() }
    }

    fun livePrice(itemId: Int): Long
    {
        val mgr = RuneLiteAccess.instance(ItemManager::class.java) ?: return 0
        return try { mgr.getItemPrice(itemId) } catch (t: Throwable) { 0 }
    }
}
