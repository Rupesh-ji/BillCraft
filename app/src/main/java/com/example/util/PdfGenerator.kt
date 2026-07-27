package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.model.BusinessProfile
import com.example.data.model.InvoiceWithLines
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    /**
     * Generates a high-quality PDF file for an Invoice and saves it to cacheDir.
     * Returns the File reference.
     */
    fun generateInvoicePdf(
        context: Context,
        invoiceWithLines: InvoiceWithLines,
        profile: BusinessProfile
    ): File {
        val invoice = invoiceWithLines.invoice
        val lines = invoiceWithLines.lines

        val pdfDocument = PdfDocument()

        // Standard A4 Size in points: 595 x 842
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Theme colors
        val primaryColor = parseHexColor(invoice.themeColorHex, Color.parseColor("#1E293B"))
        val accentColor = parseHexColor("#0284C7", Color.parseColor("#0284C7"))
        val lightBgColor = parseHexColor("#F8FAFC", Color.parseColor("#F8FAFC"))
        val textDark = Color.parseColor("#0F172A")
        val textMuted = Color.parseColor("#64748B")
        val borderLight = Color.parseColor("#E2E8F0")

        // Paints
        val paintText = Paint().apply {
            isAntiAlias = true
            color = textDark
            textSize = 10f
            typeface = Typeface.DEFAULT
        }

        val paintBold = Paint().apply {
            isAntiAlias = true
            color = textDark
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
        }

        val paintHeader = Paint().apply {
            isAntiAlias = true
            color = primaryColor
            style = Paint.Style.FILL
        }

        val paintLine = Paint().apply {
            color = borderLight
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        val paintBg = Paint().apply {
            color = lightBgColor
            style = Paint.Style.FILL
        }

        var currentY = 0f

        // 1. TOP HEADER BANNER
        if (invoice.templateName == "Modern" || invoice.templateName == "Colorful") {
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 90f, paintHeader)

            // Header Business Name
            val pTitle = Paint(paintBold).apply {
                color = Color.WHITE
                textSize = 20f
            }
            canvas.drawText(profile.companyName, 24f, 40f, pTitle)

            val pTag = Paint(paintText).apply {
                color = Color.parseColor("#E2E8F0")
                textSize = 9f
            }
            canvas.drawText(profile.tagline, 24f, 56f, pTag)

            // Invoice Type Badge on Right
            val pInvoiceTitle = Paint(paintBold).apply {
                color = Color.WHITE
                textSize = 16f
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText(invoice.invoiceType.uppercase(Locale.ROOT), pageWidth - 24f, 42f, pInvoiceTitle)

            val pInvNum = Paint(pTag).apply { textAlign = Paint.Align.RIGHT }
            canvas.drawText("#${invoice.invoiceNumber}", pageWidth - 24f, 58f, pInvNum)

            currentY = 110f
        } else {
            // Classic / Minimal
            currentY = 40f
            val pTitle = Paint(paintBold).apply {
                color = primaryColor
                textSize = 22f
            }
            canvas.drawText(profile.companyName, 24f, currentY, pTitle)
            currentY += 16f

            canvas.drawText(profile.tagline, 24f, currentY, paintText)
            currentY += 20f

            // Line divider
            canvas.drawLine(24f, currentY, pageWidth - 24f, currentY, paintLine)
            currentY += 15f
        }

        // 2. BUSINESS DETAILS & INVOICE META
        val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateStr = df.format(Date(invoice.invoiceDate))
        val dueStr = df.format(Date(invoice.dueDate))

        // Left Box: Business Details
        val busX = 24f
        canvas.drawText("FROM:", busX, currentY, paintBold)
        var busY = currentY + 14f
        canvas.drawText(profile.streetAddress, busX, busY, paintText)
        busY += 12f
        canvas.drawText(profile.cityStatePincode, busX, busY, paintText)
        busY += 12f
        canvas.drawText("Phone: ${profile.phoneNumbers}", busX, busY, paintText)
        busY += 12f
        if (profile.gstin.isNotBlank()) {
            canvas.drawText("GSTIN: ${profile.gstin}", busX, busY, paintBold)
            busY += 12f
        }

        // Right Box: Invoice Meta Info
        val metaX = pageWidth - 200f
        var metaY = currentY
        canvas.drawText("INVOICE DETAILS:", metaX, metaY, paintBold)
        metaY += 14f
        canvas.drawText("Invoice No: ${invoice.invoiceNumber}", metaX, metaY, paintText)
        metaY += 12f
        canvas.drawText("Date: $dateStr", metaX, metaY, paintText)
        metaY += 12f
        canvas.drawText("Due Date: $dueStr", metaX, metaY, paintText)
        metaY += 12f
        canvas.drawText("Place of Supply: ${invoice.placeOfSupply}", metaX, metaY, paintText)
        metaY += 12f
        if (invoice.poNumber.isNotBlank()) {
            canvas.drawText("P.O. No: ${invoice.poNumber}", metaX, metaY, paintText)
            metaY += 12f
        }

        currentY = maxOf(busY, metaY) + 15f

        // Divider
        canvas.drawLine(24f, currentY, pageWidth - 24f, currentY, paintLine)
        currentY += 15f

        // 3. CUSTOMER BILL TO & SHIP TO
        val custX = 24f
        canvas.drawText("BILLED TO:", custX, currentY, paintBold)
        var custY = currentY + 14f
        canvas.drawText(invoice.customerName, custX, custY, paintBold)
        custY += 12f
        if (invoice.customerCompany.isNotBlank()) {
            canvas.drawText(invoice.customerCompany, custX, custY, paintText)
            custY += 12f
        }
        if (invoice.customerBillingAddress.isNotBlank()) {
            val addrLines = invoice.customerBillingAddress.split("\n")
            for (al in addrLines) {
                canvas.drawText(al, custX, custY, paintText)
                custY += 12f
            }
        }
        if (invoice.customerPhone.isNotBlank()) {
            canvas.drawText("Phone: ${invoice.customerPhone}", custX, custY, paintText)
            custY += 12f
        }
        if (invoice.customerGstin.isNotBlank()) {
            canvas.drawText("GSTIN: ${invoice.customerGstin}", custX, custY, paintBold)
            custY += 12f
        }

        // Right Box: Shipping Address if available
        if (invoice.customerShippingAddress.isNotBlank()) {
            val shipX = pageWidth - 200f
            var shipY = currentY
            canvas.drawText("SHIPPED TO:", shipX, shipY, paintBold)
            shipY += 14f
            val shipLines = invoice.customerShippingAddress.split("\n")
            for (sl in shipLines) {
                canvas.drawText(sl, shipX, shipY, paintText)
                shipY += 12f
            }
        }

        currentY = maxOf(custY, currentY + 50f) + 15f

        // 4. ITEMS TABLE
        val tableTop = currentY
        val tableHeaderHeight = 24f
        canvas.drawRect(24f, tableTop, pageWidth - 24f, tableTop + tableHeaderHeight, paintHeader)

        val thText = Paint(paintBold).apply {
            color = Color.WHITE
            textSize = 9f
        }

        // Table Columns
        canvas.drawText("S.N.", 32f, tableTop + 16f, thText)
        canvas.drawText("ITEM / SERVICE", 65f, tableTop + 16f, thText)
        canvas.drawText("HSN/SAC", 270f, tableTop + 16f, thText)

        val rAlign = Paint(thText).apply { textAlign = Paint.Align.RIGHT }
        canvas.drawText("QTY", 340f, tableTop + 16f, rAlign)
        canvas.drawText("RATE", 410f, tableTop + 16f, rAlign)
        canvas.drawText("TAX %", 480f, tableTop + 16f, rAlign)
        canvas.drawText("AMOUNT", pageWidth - 32f, tableTop + 16f, rAlign)

        currentY = tableTop + tableHeaderHeight + 12f

        // Draw Row Items
        val sym = invoice.currencySymbol
        for (line in lines) {
            canvas.drawText("${line.itemSNo}", 32f, currentY, paintText)
            
            // Truncate item name if long
            val itemNameStr = if (line.itemName.length > 30) line.itemName.substring(0, 28) + ".." else line.itemName
            canvas.drawText(itemNameStr, 65f, currentY, paintBold)
            canvas.drawText(line.hsnSacCode, 270f, currentY, paintText)

            val pRight = Paint(paintText).apply { textAlign = Paint.Align.RIGHT }
            canvas.drawText("${line.quantity} ${line.unit}", 340f, currentY, pRight)
            canvas.drawText("$sym ${String.format("%.2f", line.rate)}", 410f, currentY, pRight)
            canvas.drawText("${line.taxRatePercent}%", 480f, currentY, pRight)

            val pRightBold = Paint(paintBold).apply { textAlign = Paint.Align.RIGHT }
            canvas.drawText("$sym ${String.format("%.2f", line.lineTotal)}", pageWidth - 32f, currentY, pRightBold)

            currentY += 18f
            canvas.drawLine(24f, currentY - 6f, pageWidth - 24f, currentY - 6f, paintLine)
        }

        currentY += 10f

        // 5. TOTALS & TAX BREAKDOWN (BOTTOM RIGHT)
        val totalsX = pageWidth - 220f
        val totalsWidth = 196f
        var totY = currentY

        // Subtotal
        drawTotalRow(canvas, "Subtotal:", "$sym ${String.format("%.2f", invoice.subtotal)}", totalsX, totY, paintText, paintBold)
        totY += 14f

        if (invoice.itemDiscountTotal > 0.0 || invoice.overallDiscountFlat > 0.0) {
            val disc = invoice.itemDiscountTotal + invoice.overallDiscountFlat
            drawTotalRow(canvas, "Discount:", "- $sym ${String.format("%.2f", disc)}", totalsX, totY, paintText, paintBold)
            totY += 14f
        }

        if (!invoice.isInterstate) {
            drawTotalRow(canvas, "CGST:", "$sym ${String.format("%.2f", invoice.cgstAmount)}", totalsX, totY, paintText, paintBold)
            totY += 14f
            drawTotalRow(canvas, "SGST:", "$sym ${String.format("%.2f", invoice.sgstAmount)}", totalsX, totY, paintText, paintBold)
            totY += 14f
        } else {
            drawTotalRow(canvas, "IGST:", "$sym ${String.format("%.2f", invoice.igstAmount)}", totalsX, totY, paintText, paintBold)
            totY += 14f
        }

        if (invoice.shippingCharges > 0.0) {
            drawTotalRow(canvas, "Shipping:", "$sym ${String.format("%.2f", invoice.shippingCharges)}", totalsX, totY, paintText, paintBold)
            totY += 14f
        }

        if (invoice.roundOff != 0.0) {
            drawTotalRow(canvas, "Round Off:", "$sym ${String.format("%.2f", invoice.roundOff)}", totalsX, totY, paintText, paintBold)
            totY += 14f
        }

        // Grand Total Banner
        canvas.drawRect(totalsX, totY, totalsX + totalsWidth, totY + 24f, paintHeader)
        val pGrandLabel = Paint(paintBold).apply {
            color = Color.WHITE
            textSize = 11f
        }
        val pGrandVal = Paint(paintBold).apply {
            color = Color.WHITE
            textSize = 12f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("GRAND TOTAL:", totalsX + 8f, totY + 16f, pGrandLabel)
        canvas.drawText("$sym ${String.format("%.2f", invoice.grandTotal)}", totalsX + totalsWidth - 8f, totY + 16f, pGrandVal)

        totY += 30f

        if (invoice.advancePaid > 0.0) {
            drawTotalRow(canvas, "Advance Paid:", "$sym ${String.format("%.2f", invoice.advancePaid)}", totalsX, totY, paintText, paintBold)
            totY += 14f
            drawTotalRow(canvas, "Balance Due:", "$sym ${String.format("%.2f", invoice.balanceDue)}", totalsX, totY, paintBold, paintBold)
            totY += 14f
        }

        // 6. LEFT BOTTOM: AMOUNT IN WORDS & UPI QR CODE & BANK DETAILS
        var leftY = currentY
        val amountInWords = AmountToWordsConverter.convertToWords(invoice.grandTotal, invoice.currencySymbol, invoice.language)
        
        canvas.drawText("AMOUNT IN WORDS:", 24f, leftY, paintBold)
        leftY += 14f
        
        // Multi-line wrap amount in words
        val wordsPaint = Paint(paintText).apply { textSize = 9f }
        canvas.drawText(amountInWords, 24f, leftY, wordsPaint)
        leftY += 20f

        // UPI QR CODE & BANK DETAILS
        if (profile.upiId.isNotBlank() || profile.bankAccountNumber.isNotBlank()) {
            canvas.drawRoundRect(RectF(24f, leftY, 320f, leftY + 110f), 8f, 8f, paintBg)

            if (profile.upiId.isNotBlank()) {
                val upiPayload = QrCodeUtils.generateUpiString(profile.upiId, profile.companyName, invoice.balanceDue.takeIf { it > 0 } ?: invoice.grandTotal, invoice.invoiceNumber)
                val qrBitmap = QrCodeUtils.generateQrBitmap(upiPayload, 90)
                canvas.drawBitmap(qrBitmap, 32f, leftY + 10f, null)
            }

            val bX = if (profile.upiId.isNotBlank()) 135f else 36f
            var bY = leftY + 22f
            val pBankTitle = Paint(paintBold).apply { textSize = 9f; color = primaryColor }
            canvas.drawText("PAYMENT DETAILS", bX, bY, pBankTitle)
            bY += 14f
            val pBank = Paint(paintText).apply { textSize = 8f }
            if (profile.bankNameBranch.isNotBlank()) {
                canvas.drawText(profile.bankNameBranch, bX, bY, pBank)
                bY += 11f
            }
            if (profile.bankAccountNumber.isNotBlank()) {
                canvas.drawText("A/C: ${profile.bankAccountNumber}", bX, bY, pBank)
                bY += 11f
            }
            if (profile.bankIfsc.isNotBlank()) {
                canvas.drawText("IFSC: ${profile.bankIfsc}", bX, bY, pBank)
                bY += 11f
            }
            if (profile.upiId.isNotBlank()) {
                canvas.drawText("UPI ID: ${profile.upiId}", bX, bY, pBank)
            }
        }

        // 7. FOOTER: TERMS & SIGNATURE LINE
        val footerY = pageHeight - 70f
        canvas.drawLine(24f, footerY - 10f, pageWidth - 24f, footerY - 10f, paintLine)

        val termX = 24f
        canvas.drawText("TERMS & CONDITIONS:", termX, footerY, paintBold)
        var termY = footerY + 12f
        val termLines = (invoice.termsAndConditions.ifBlank { profile.defaultTerms }).split("\n")
        for (tl in termLines.take(3)) {
            canvas.drawText(tl, termX, termY, Paint(paintText).apply { textSize = 8f })
            termY += 10f
        }

        // Signature on right
        val sigX = pageWidth - 160f
        canvas.drawText("For ${profile.companyName}", sigX, footerY, paintBold)
        canvas.drawText("Authorized Signatory", sigX, footerY + 45f, paintBold)

        pdfDocument.finishPage(page)

        // Write to cache file
        val pdfFile = File(context.cacheDir, "Invoice_${invoice.invoiceNumber}.pdf")
        val outputStream = FileOutputStream(pdfFile)
        pdfDocument.writeTo(outputStream)
        outputStream.close()
        pdfDocument.close()

        return pdfFile
    }

    private fun drawTotalRow(
        canvas: Canvas,
        label: String,
        valStr: String,
        x: Float,
        y: Float,
        labelPaint: Paint,
        valPaint: Paint
    ) {
        val rVal = Paint(valPaint).apply { textAlign = Paint.Align.RIGHT }
        canvas.drawText(label, x, y, labelPaint)
        canvas.drawText(valStr, x + 196f, y, rVal)
    }

    private fun parseHexColor(hex: String, fallback: Int): Int {
        return try {
            Color.parseColor(hex)
        } catch (e: Exception) {
            fallback
        }
    }
}
