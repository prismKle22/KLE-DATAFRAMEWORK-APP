package com.samsung.prism.android.app.aidatacapture.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileOperations {
    private var sharedPrefsManager: SharedPrefsManager? = null
    fun write(fname: String, bm: Bitmap, context: Context): Boolean {
        return try {
            val fpath = Constants.SDCARD_PATH + fname + ".pdf"
            sharedPrefsManager = SharedPrefsManager(context)
            val file = File(fpath)
            if (!file.exists()) {
                file.createNewFile()
            }
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val document = Document()
            PdfWriter.getInstance(
                document,
                FileOutputStream(file.absoluteFile)
            )
            document.open()
            val sign = Chunk(                        )
            val stream = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 50, stream)
            val myImg = Image.getInstance(stream.toByteArray())
            myImg.alignment = Image.ALIGN_RIGHT
            myImg.scaleAbsolute(150f, 100f)
            val headingStyle = Font(Font.FontFamily.TIMES_ROMAN, 14.0f, Font.BOLD)
            val bodyStyle = Font(Font.FontFamily.TIMES_ROMAN,10.0f)

            //Heading of the Consent
            val heading = Paragraph("Consent Form\n\n", headingStyle)
            heading.alignment = Paragraph.ALIGN_CENTER
            document.add(heading)

            //Body of the Consent
            val body = Paragraph(context.getString(R.string.consent_form), bodyStyle)
            body.alignment = Paragraph.ALIGN_JUSTIFIED
            document.add(body)

            //Current Date
            val currentDate = Date()
            val tableName = PdfPTable(2)
            tableName.widthPercentage = 100f
            tableName.addCell(
                getCell(
                    "Name: " + sharedPrefsManager!!.getStringValue(
                        SharedPrefsConstants.CONSENT_NAME,
                        "Test Name"
                    ), PdfPCell.ALIGN_LEFT
                )
            )
            tableName.addCell(getCell("Signature", PdfPCell.ALIGN_RIGHT))
            val table = PdfPTable(1)
            table.widthPercentage = 100f
            table.addCell(getCell(context.getString(R.string.date_file) + formatter.format(currentDate), PdfPCell.ALIGN_LEFT))
            document.add(tableName)
            document.add(table)
            document.add(myImg)
            document.close()
            Log.d("Suceess", "Sucess")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: DocumentException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            false
        }
    }

    fun getCell(text: String?, alignment: Int): PdfPCell {
        val cell = PdfPCell(Phrase(text))
        cell.paddingTop = 20f
        cell.paddingRight = 70f
        cell.horizontalAlignment = alignment
        cell.border = PdfPCell.NO_BORDER
        return cell
    }
}