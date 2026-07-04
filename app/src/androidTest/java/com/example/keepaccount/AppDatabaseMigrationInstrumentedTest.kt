package com.example.keepaccount

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.keepaccount.data.AppDatabase
import com.example.keepaccount.data.MIGRATION_1_2
import com.example.keepaccount.data.MIGRATION_2_3
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationInstrumentedTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @After
    fun tearDown() {
        context.deleteDatabase(TEST_DATABASE)
    }

    @Test
    fun migrationFromOneToThree_preservesRecordsAndConvertsCategoryToId() {
        context.deleteDatabase(TEST_DATABASE)
        createVersionOneDatabase()

        val database = Room.databaseBuilder(context, AppDatabase::class.java, TEST_DATABASE)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .allowMainThreadQueries()
            .build()

        database.openHelper.writableDatabase.query(
            "SELECT category, amountCents, createdAt, updatedAt FROM bill_records ORDER BY id ASC",
        ).use { cursor ->
            assertEquals(2, cursor.count)
            cursor.moveToFirst()
            assertEquals(1, cursor.getInt(0))
            assertEquals(1_200L, cursor.getLong(1))
            assertEquals(1_000L, cursor.getLong(2))
            assertEquals(1_000L, cursor.getLong(3))

            cursor.moveToNext()
            assertEquals(0, cursor.getInt(0))
            assertEquals(2_300L, cursor.getLong(1))
            assertEquals(2_000L, cursor.getLong(2))
            assertEquals(2_000L, cursor.getLong(3))
        }

        database.close()
    }

    private fun createVersionOneDatabase() {
        val databaseFile = context.getDatabasePath(TEST_DATABASE)
        databaseFile.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(databaseFile, null).use { database ->
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS bill_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    type TEXT NOT NULL,
                    category TEXT NOT NULL,
                    amountCents INTEGER NOT NULL,
                    note TEXT NOT NULL,
                    occurredAt INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            database.execSQL(
                """
                INSERT INTO bill_records (
                    id,
                    type,
                    category,
                    amountCents,
                    note,
                    occurredAt,
                    createdAt
                ) VALUES (?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                arrayOf(
                    1, "EXPENSE", "餐饮", 1_200, "午餐", 1_000, 1_000,
                    2, "EXPENSE", "自定义旧分类", 2_300, "旧数据", 2_000, 2_000,
                ),
            )
            database.version = 1
        }
    }

    private companion object {
        const val TEST_DATABASE = "migration-test.db"
    }
}
