package com.example.util

import java.text.DecimalFormat
import kotlin.math.roundToLong

object AmountToWordsConverter {

    private val units = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    )

    private val tens = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    fun convertToWords(amount: Double, currencySymbol: String = "₹", language: String = "English"): String {
        if (amount <= 0.0) return "Zero Rupees Only"

        val wholePart = amount.toLong()
        val decimalPart = ((amount - wholePart) * 100).roundToLong()

        val currencyName = when (currencySymbol) {
            "$" -> "Dollars"
            "€" -> "Euros"
            "£" -> "Pounds"
            "AED" -> "Dirhams"
            else -> if (language == "Hindi") "रुपये (Rupees)" else "Rupees"
        }

        val subUnitName = when (currencySymbol) {
            "$" -> "Cents"
            "€" -> "Cents"
            "£" -> "Pence"
            "AED" -> "Fils"
            else -> if (language == "Hindi") "पैसे (Paise)" else "Paise"
        }

        val wholeWords = convertIndianFormat(wholePart)
        val decimalWords = if (decimalPart > 0) convertIndianFormat(decimalPart) else ""

        val result = StringBuilder()
        if (wholeWords.isNotEmpty()) {
            result.append(wholeWords).append(" ").append(currencyName)
        } else {
            result.append("Zero ").append(currencyName)
        }

        if (decimalWords.isNotEmpty()) {
            result.append(" and ").append(decimalWords).append(" ").append(subUnitName)
        }

        result.append(" Only")
        return result.toString()
    }

    private fun convertIndianFormat(n: Long): String {
        if (n == 0L) return ""
        if (n < 20) return units[n.toInt()]
        if (n < 100) return tens[(n / 10).toInt()] + (if (n % 10 != 0L) " " + units[(n % 10).toInt()] else "")
        if (n < 1000) return units[(n / 100).toInt()] + " Hundred" + (if (n % 100 != 0L) " " + convertIndianFormat(n % 100) else "")
        if (n < 100000) return convertIndianFormat(n / 1000) + " Thousand" + (if (n % 1000 != 0L) " " + convertIndianFormat(n % 1000) else "")
        if (n < 10000000) return convertIndianFormat(n / 100000) + " Lakh" + (if (n % 100000 != 0L) " " + convertIndianFormat(n % 100000) else "")
        return convertIndianFormat(n / 10000000) + " Crore" + (if (n % 10000000 != 0L) " " + convertIndianFormat(n % 10000000) else "")
    }

    fun formatCurrency(amount: Double, symbol: String = "₹"): String {
        val formatter = DecimalFormat("#,##,##0.00")
        return "$symbol ${formatter.format(amount)}"
    }
}
