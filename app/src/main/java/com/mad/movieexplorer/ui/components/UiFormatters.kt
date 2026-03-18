package com.mad.movieexplorer.ui.components

import java.text.NumberFormat
import java.util.Locale

private val currencyFormatter = NumberFormat.getCurrencyInstance(
    Locale.forLanguageTag("en-IN")
).apply {
    maximumFractionDigits = 0
}

fun formatCurrency(amount: Double): String = currencyFormatter.format(amount)

fun formatRating(rating: Double): String = String.format(Locale.US, "%.1f", rating)
