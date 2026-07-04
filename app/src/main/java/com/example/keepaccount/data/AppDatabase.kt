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
    version = 3,
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
                    .addMigrations(MIGRATION_2_3)
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

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE bill_records_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                type TEXT NOT NULL,
                category INTEGER NOT NULL,
                amountCents INTEGER NOT NULL,
                note TEXT NOT NULL,
                occurredAt INTEGER NOT NULL,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO bill_records_new (
                id,
                type,
                category,
                amountCents,
                note,
                occurredAt,
                createdAt,
                updatedAt
            )
            SELECT
                id,
                type,
                CASE category
                    WHEN '餐饮' THEN 1
                    WHEN '交通' THEN 2
                    WHEN '购物' THEN 3
                    WHEN '生活缴费' THEN 4
                    WHEN '医疗' THEN 5
                    WHEN '服饰' THEN 6
                    WHEN '娱乐' THEN 7
                    WHEN '服务' THEN 8
                    WHEN '教育' THEN 9
                    WHEN '运动' THEN 10
                    WHEN '旅行' THEN 11
                    WHEN '宠物' THEN 12
                    WHEN '保险' THEN 13
                    WHEN '公益' THEN 14
                    WHEN '工资' THEN 101
                    WHEN '奖金' THEN 102
                    WHEN '退款' THEN 103
                    WHEN '投资' THEN 104
                    WHEN '其他' THEN 105
                    ELSE 0
                END,
                amountCents,
                note,
                occurredAt,
                createdAt,
                updatedAt
            FROM bill_records
            """.trimIndent(),
        )
        db.execSQL("DROP TABLE bill_records")
        db.execSQL("ALTER TABLE bill_records_new RENAME TO bill_records")
    }
}

class BillTypeConverters {
    @TypeConverter
    fun fromBillType(type: BillType): String = type.name

    @TypeConverter
    fun toBillType(value: String): BillType = BillType.valueOf(value)
}
