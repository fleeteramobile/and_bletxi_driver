package com.bluetaxi.driver.gotohome

import android.content.Context
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class ExcelReader(private val context: Context) {

    fun readAddressesFromExcel(fileName: String): List<AddressData> {
        val addressList = mutableListOf<AddressData>()

        try {
            val inputStream: InputStream = context.assets.open(fileName)
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0) // Assuming data is in the first sheet

            val rowIterator = sheet.iterator()

            // Skip header row
            if (rowIterator.hasNext()) {
                rowIterator.next()
            }

            while (rowIterator.hasNext()) {
                val row = rowIterator.next()
                val cellIterator = row.iterator()

                var address = ""
                var latitude = 0.0
                var longitude = 0.0

                if (cellIterator.hasNext()) {
                    address = cellIterator.next().stringCellValue
                }
                if (cellIterator.hasNext()) {
                    latitude = cellIterator.next().numericCellValue
                }
                if (cellIterator.hasNext()) {
                    longitude = cellIterator.next().numericCellValue
                }

                addressList.add(AddressData(address, latitude, longitude))
            }

            workbook.close()
            inputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return addressList
    }
}
