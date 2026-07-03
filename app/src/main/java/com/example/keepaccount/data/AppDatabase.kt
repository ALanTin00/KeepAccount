package com.example.keepaccount.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [BillRecordEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(BillTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun billRecordDao(): BillRecordDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "keep_account.db",
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE bill_records ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("UPDATE bill_records SET updatedAt = createdAt WHERE updatedAt = 0")
    }
}

class BillTypeConverters {
    @TypeConverter
    fun fromBillType(type: BillType): String = type.name

    @TypeConverter
    fun toBillType(value: String): BillType = BillType.valueOf(value)
}
