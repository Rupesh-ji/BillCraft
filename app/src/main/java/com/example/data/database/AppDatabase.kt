package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BusinessProfileDao
import com.example.data.dao.CustomerDao
import com.example.data.dao.InvoiceDao
import com.example.data.dao.ProductDao
import com.example.data.model.BusinessProfile
import com.example.data.model.Customer
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItemLine
import com.example.data.model.ProductItem

@Database(
    entities = [
        BusinessProfile::class,
        Customer::class,
        ProductItem::class,
        Invoice::class,
        InvoiceItemLine::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun businessProfileDao(): BusinessProfileDao
    abstract fun customerDao(): CustomerDao
    abstract fun productDao(): ProductDao
    abstract fun invoiceDao(): InvoiceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "billcraft_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
