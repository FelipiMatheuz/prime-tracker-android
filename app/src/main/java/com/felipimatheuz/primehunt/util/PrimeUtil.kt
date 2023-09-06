package com.felipimatheuz.primehunt.util

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.sets.*
import kotlin.math.roundToInt


fun toggleLoading(target: View, isLoading: Boolean) {
    target.visibility = if (isLoading) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun updateStatusColor(
    warframe: CharSequence,
    item1: CharSequence,
    item2: CharSequence
): Int {
    val totalPercent = if (item2.isNotBlank()) {
        (getPercent(warframe) + getPercent(item1) + getPercent(item2)) / 3
    } else {
        (getPercent(warframe) + getPercent(item1)) / 2
    }
    return if (totalPercent == 0) {
        R.color.status_zero
    } else if (totalPercent <= 50) {
        R.color.status_low
    } else if (totalPercent < 100) {
        R.color.status_high
    } else {
        R.color.status_complete
    }
}

fun updateStatusColor(
    item: CharSequence
): Int {
    val percent = getPercent(item)
    return if (percent == 0) {
        R.color.status_zero
    } else if (percent <= 50) {
        R.color.status_low
    } else if (percent < 100) {
        R.color.status_high
    } else {
        R.color.status_complete
    }
}

fun updateCardBackground(cardView: CardView, statusColor: Int) {
    val colorFrom: Int = cardView.cardBackgroundColor.defaultColor
    val colorTo: Int = ContextCompat.getColor(cardView.context, statusColor)
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
    colorAnimation.duration = 1000
    colorAnimation.addUpdateListener { animator -> cardView.setCardBackgroundColor(animator.animatedValue as Int) }
    colorAnimation.start()
}

private fun getPercent(percentString: CharSequence) = percentString.toString().replace("%", "").toInt()

fun formatPercentText(textView: TextView, target: PrimeItem) {
    val total = target.components.size + 1
    var obtained = target.components.count {
        it.obtained
    }
    if (target.blueprint) {
        obtained++
    }
    val percent = (obtained.toFloat() / total.toFloat()) * 100
    textView.text = percent.roundToInt().toString().plus("%")
}

fun getStatusText(textView: TextView, status: PrimeStatus) {
    val statusText = when (status) {
        PrimeStatus.VAULT -> R.string.status_vault
        PrimeStatus.ACTIVE -> R.string.status_active
        PrimeStatus.BARO -> R.string.status_baro
        PrimeStatus.RESURGENCE -> R.string.status_resurgence
    }
    textView.text = textView.context.getString(statusText)
}

fun formatCardText(textView: TextView, itemName: String, isSet: Boolean) {
    val template = if (isSet) {
        R.string.prime_set_template
    } else {
        R.string.prime_template
    }
    textView.text = textView.context.getString(template)
        .replace("ITEM", itemName)
}

fun formatCardRelicText(textView: TextView, relicName: String) {
    textView.text = textView.context.getString(R.string.relic_template).replace("ITEM", relicName)
}

fun formatItemPartText(textView: TextView, part: ItemPart, quantity: Int?) {
    val partText = when (part) {
        ItemPart.NEUROPTICS -> R.string.comp_neuroptics
        ItemPart.CHASSIS -> R.string.comp_chassis
        ItemPart.SYSTEMS -> R.string.comp_systems
        ItemPart.BLADE -> R.string.comp_blade
        ItemPart.BARREL -> R.string.comp_barrel
        ItemPart.DISC -> R.string.comp_disc
        ItemPart.HANDLE -> R.string.comp_handle
        ItemPart.ORNAMENT -> R.string.comp_ornament
        ItemPart.RECEIVER -> R.string.comp_receiver
        ItemPart.STOCK -> R.string.comp_stock
        ItemPart.LINK -> R.string.comp_link
        ItemPart.GAUNTLET -> R.string.comp_gauntlet
        ItemPart.CEREBRUM -> R.string.comp_cerebrum
        ItemPart.CARAPACE -> R.string.comp_carapace
        ItemPart.POUCH -> R.string.comp_pouch
        ItemPart.STARS -> R.string.comp_stars
        ItemPart.HILT -> R.string.comp_hilt
        ItemPart.HARNESS -> R.string.comp_harness
        ItemPart.CIRCUIT -> R.string.comp_systems
        ItemPart.WINGS -> R.string.comp_wings
        ItemPart.BAND -> R.string.comp_band
        ItemPart.BUCKLE -> R.string.comp_buckle
        ItemPart.HEAD -> R.string.comp_head
        ItemPart.GRIP -> R.string.comp_grip
        ItemPart.STRING -> R.string.comp_string
        ItemPart.LLIMB -> R.string.comp_llinb
        ItemPart.ULIMB -> R.string.comp_ulimb
        ItemPart.GUARD -> R.string.comp_guard
        ItemPart.BOOT -> R.string.comp_boot
        ItemPart.CHAIN -> R.string.comp_chain
        ItemPart.BRONCO -> R.string.bronco_prime
        ItemPart.LEX -> R.string.lex_prime
        ItemPart.VASTO -> R.string.vasto_prime
    }
    val quantityText = if (quantity != null && quantity > 1) {
        " ${quantity}x"
    } else {
        ""
    }
    textView.text = textView.context.getString(partText).plus(quantityText)

    textView.setCompoundDrawablesWithIntrinsicBounds(
        null,
        getItemPartIcon(textView.context, part),
        null,
        null
    )
}

private fun getItemPartIcon(context: Context, part: ItemPart): Drawable? {
    val partDrawable = when (part) {
        ItemPart.NEUROPTICS, ItemPart.CEREBRUM -> R.drawable.prime_neuroptics
        ItemPart.CHASSIS, ItemPart.CARAPACE -> R.drawable.prime_chassis
        ItemPart.SYSTEMS -> R.drawable.prime_systems
        ItemPart.BARREL -> R.drawable.prime_barrel
        ItemPart.RECEIVER -> R.drawable.prime_receiver
        ItemPart.STOCK, ItemPart.STRING, ItemPart.CHAIN -> R.drawable.prime_stock
        ItemPart.BLADE, ItemPart.DISC, ItemPart.STARS, ItemPart.HEAD, ItemPart.ULIMB, ItemPart.LLIMB -> R.drawable.prime_blade
        ItemPart.HANDLE, ItemPart.GAUNTLET, ItemPart.HILT -> R.drawable.prime_handle
        ItemPart.LINK, ItemPart.ORNAMENT, ItemPart.BUCKLE -> R.drawable.prime_link
        ItemPart.POUCH, ItemPart.BAND, ItemPart.GRIP -> R.drawable.prime_grip
        ItemPart.GUARD, ItemPart.BOOT -> R.drawable.prime_guard
        ItemPart.HARNESS -> R.drawable.prime_harness
        ItemPart.CIRCUIT -> R.drawable.prime_circuit
        ItemPart.WINGS -> R.drawable.prime_wings
        ItemPart.BRONCO, ItemPart.LEX, ItemPart.VASTO -> R.drawable.pistol
    }
    return AppCompatResources.getDrawable(context, partDrawable)
}

fun formatRelicListText(textView: TextView, itemName: String, itemComp: String) {
    val searchText = textView.context.getString(R.string.prime_template)
        .replace("ITEM", itemName).plus(" ").plus(itemComp)
    val resultSearch = apiRelic.getRelicPerItemComp(searchText)
    if (resultSearch.isNotEmpty()) {
        val spanBuilder = SpannableStringBuilder()
        for (result in resultSearch.sortedBy { it.vaulted }) {
            val relicName = SpannableString(result.name.subSequence(0, result.name.lastIndexOf(" ")))
            if (result.vaulted) {
                relicName.setSpan(StrikethroughSpan(), 0, relicName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            val colorRes = getColorForeground(result.rewards, searchText)
            colorRes?.let { textView.context.getColor(it) }?.let {
                relicName.setSpan(
                    ForegroundColorSpan(it),
                    0,
                    relicName.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            spanBuilder.append(relicName).append("\n")
        }
        textView.setText(spanBuilder.subSequence(0, spanBuilder.lastIndexOf("\n")), TextView.BufferType.SPANNABLE)
    } else {
        textView.visibility = View.INVISIBLE
    }
}

private fun getColorForeground(rewards: List<Reward>, searchText: String): Int? {
    val result = rewards.filter { it.item.name.contains(searchText, true) }
    return if (result.isEmpty()) {
        null
    } else {
        when (result[0].chance) {
            25.33f -> R.color.common
            11f -> R.color.uncommon
            2f -> R.color.rare
            else -> null
        }
    }
}

fun updateCompStatus(obtainedList: List<Boolean>): Int {
    val numberObtained = obtainedList.count { it }
    return if (numberObtained == 0) {
        0
    } else if (numberObtained < obtainedList.size) {
        R.drawable.progress_circle
    } else {
        R.drawable.check_circle
    }
}

fun formatRelicText(textView: TextView, relicName: String, quantity: Int, vaulted: Boolean) {
    textView.paintFlags = if (vaulted) {
        Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        Paint.HINTING_ON
    }
    textView.isEnabled = quantity > 0
    textView.text = relicName
}

fun formatRelicRewardsText(textView: TextView, rewards: List<Reward>) {
    val spanBuilder = SpannableStringBuilder()
    for (reward in rewards) {
        val itemName = SpannableString(formatRelicItemReward(textView.context, reward.item.name))
        val colorRes = when (reward.chance) {
            25.33f -> R.color.common
            11f -> R.color.uncommon
            2f -> R.color.rare
            else -> null
        }
        colorRes?.let { textView.context.getColor(it) }?.let {
            itemName.setSpan(
                ForegroundColorSpan(it),
                0,
                itemName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        spanBuilder.append(itemName).append("\n")
    }
    textView.setText(spanBuilder.subSequence(0, spanBuilder.lastIndexOf("\n")), TextView.BufferType.SPANNABLE)
}

fun formatRelicItemReward(context: Context, name: String): String {
    val textWords = name.split(" ")
    val itemName = StringBuilder()
    if (textWords.contains("Prime")) {
        for (words in textWords) {
            if (words.contains("Prime")) {
                break
            } else {
                itemName.append(words).append(" ")
            }
        }
    }

    if (itemName.toString().isNotEmpty()) {
        val hasBlueprint = textWords.contains("Blueprint")
        val compName = if (textWords.contains("Blueprint")) {
            textWords[textWords.size - 2]
        } else {
            if (textWords.contains("Limb")) {
                textWords[textWords.size - 2] + " " + textWords[textWords.size - 1]
            } else {
                textWords[textWords.size - 1]
            }
        }
        var formattedText = context.getString(R.string.relic_item_template)
        formattedText = formattedText.replace("ITEM", itemName.toString().substring(0, itemName.lastIndexOf(" ")))
            .replace("COMP", translateComponent(context, compName))
        formattedText = formattedText.replace(
            "BP", if (hasBlueprint) {
                "(${context.getString(R.string.comp_blueprint)})"
            } else {
                ""
            }
        )
        return formattedText.trim()
    } else {
        return context.getString(R.string.forma_blueprint)
    }
}

fun translateComponent(context: Context, s: String): String {
    val compTranslate = when (s) {
        "Blueprint" -> context.getString(R.string.comp_blueprint)
        "Blade" -> context.getString(R.string.comp_blade)
        "Neuroptics" -> context.getString(R.string.comp_neuroptics)
        "Chassis" -> context.getString(R.string.comp_chassis)
        "Systems" -> context.getString(R.string.comp_systems)
        "Barrel" -> context.getString(R.string.comp_barrel)
        "Disc" -> context.getString(R.string.comp_disc)
        "Handle" -> context.getString(R.string.comp_handle)
        "Ornament" -> context.getString(R.string.comp_ornament)
        "Receiver" -> context.getString(R.string.comp_receiver)
        "Stock" -> context.getString(R.string.comp_stock)
        "Link" -> context.getString(R.string.comp_link)
        "Cerebrum" -> context.getString(R.string.comp_cerebrum)
        "Carapace" -> context.getString(R.string.comp_carapace)
        "Gauntlet" -> context.getString(R.string.comp_gauntlet)
        "Pouch" -> context.getString(R.string.comp_pouch)
        "Stars" -> context.getString(R.string.comp_stars)
        "Hilt" -> context.getString(R.string.comp_hilt)
        "Harness" -> context.getString(R.string.comp_harness)
        "Wings" -> context.getString(R.string.comp_wings)
        "Band" -> context.getString(R.string.comp_band)
        "Buckle" -> context.getString(R.string.comp_buckle)
        "Head" -> context.getString(R.string.comp_head)
        "Grip" -> context.getString(R.string.comp_grip)
        "String" -> context.getString(R.string.comp_string)
        "Lower Limb" -> context.getString(R.string.comp_llinb)
        "Upper Limb" -> context.getString(R.string.comp_ulimb)
        "Guard" -> context.getString(R.string.comp_guard)
        "Boot" -> context.getString(R.string.comp_boot)
        "Chain" -> context.getString(R.string.comp_chain)
        else -> return ""
    }
    return ": $compTranslate"
}

fun isItemCompleted(primeItem: PrimeItem): Int {
    return if (primeItem.blueprint && primeItem.components.all { pi -> pi.obtained }) {
        2
    } else if (primeItem.blueprint || primeItem.components.any { pi -> pi.obtained }) {
        1
    } else {
        0
    }
}
