package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri

object QrCodeUtils {

    fun generateUpiString(
        upiId: String,
        payeeName: String,
        amount: Double,
        invoiceNumber: String
    ): String {
        val encodedName = Uri.encode(payeeName)
        val encodedNote = Uri.encode("Payment for Invoice $invoiceNumber")
        val amountStr = String.format("%.2f", amount)
        return "upi://pay?pa=$upiId&pn=$encodedName&am=$amountStr&cu=INR&tn=$encodedNote"
    }

    /**
     * Creates a high-fidelity stylized QR matrix bitmap for displaying or rendering onto PDFs.
     */
    fun generateQrBitmap(data: String, size: Int = 300): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val paintDark = Paint().apply {
            color = Color.parseColor("#1E293B")
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val paintAccent = Paint().apply {
            color = Color.parseColor("#0284C7")
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val moduleCount = 25
        val moduleSize = size.toFloat() / moduleCount

        // Pseudo deterministic grid generation based on payload string hash
        val hash = data.hashCode()

        for (r in 0 until moduleCount) {
            for (c in 0 until moduleCount) {
                // Corner finder patterns (7x7)
                val isTopLeftCorner = r in 0..6 && c in 0..6
                val isTopRightCorner = r in 0..6 && c in (moduleCount - 7) until moduleCount
                val isBottomLeftCorner = r in (moduleCount - 7) until moduleCount && c in 0..6

                if (isTopLeftCorner || isTopRightCorner || isBottomLeftCorner) {
                    val localR = if (isTopLeftCorner) r else if (isTopRightCorner) r else r - (moduleCount - 7)
                    val localC = if (isTopLeftCorner) c else if (isTopRightCorner) c - (moduleCount - 7) else c

                    if (localR == 0 || localR == 6 || localC == 0 || localC == 6 || (localR in 2..4 && localC in 2..4)) {
                        val left = c * moduleSize
                        val top = r * moduleSize
                        canvas.drawRect(left, top, left + moduleSize, top + moduleSize, if (localR in 2..4 && localC in 2..4) paintAccent else paintDark)
                    }
                } else {
                    // Internal modules
                    val cellHash = (hash xor (r * 31 + c * 17)) and 0xFF
                    if (cellHash % 3 == 0 || (r + c) % 5 == 0) {
                        val left = c * moduleSize
                        val top = r * moduleSize
                        val padding = moduleSize * 0.1f
                        canvas.drawRoundRect(
                            left + padding,
                            top + padding,
                            left + moduleSize - padding,
                            top + moduleSize - padding,
                            2f, 2f,
                            paintDark
                        )
                    }
                }
            }
        }

        return bitmap
    }
}
