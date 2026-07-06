package com.example.keepaccount.data

import android.content.Context
import java.io.File
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BillBackupManager(
    context: Context,
) {
    private val appContext = context.applicationContext

    val backupDirectory: File
        get() = File(appContext.getExternalFilesDir(null) ?: appContext.filesDir, BACKUP_DIRECTORY_NAME)

    val backupFile: File
        get() = File(backupDirectory, BACKUP_FILE_NAME)

    fun export(records: List<BillRecordEntity>): BackupExportResult {
        val directory = backupDirectory
        if (!directory.exists() && !directory.mkdirs()) {
            throw BackupException("备份目录创建失败：${directory.absolutePath}")
        }

        val file = backupFile
        val backup = BillBackupFile(
            version = CURRENT_BACKUP_VERSION,
            exportedAt = System.currentTimeMillis(),
            records = records.map { it.toBackupRecord() },
        )
        runCatching {
            file.writeText(backup.toJson().toString(JSON_INDENT_SPACES), Charsets.UTF_8)
        }.getOrElse {
            throw BackupException("备份文件写入失败：${it.localizedMessage ?: "未知错误"}")
        }

        return BackupExportResult(
            filePath = file.absolutePath,
            directoryPath = directory.absolutePath,
            recordCount = records.size,
        )
    }

    fun readRecords(): List<BillRecordEntity> {
        val file = backupFile
        if (!file.exists()) {
            throw BackupException("没有找到备份文件，请先把 $BACKUP_FILE_NAME 放到 ${backupDirectory.absolutePath}")
        }

        val content = runCatching {
            file.readText(Charsets.UTF_8)
        }.getOrElse {
            throw BackupException("备份文件读取失败：${it.localizedMessage ?: "未知错误"}")
        }

        val backup = runCatching {
            BillBackupFile.fromJson(JSONObject(content))
        }.getOrElse {
            val message = when (it) {
                is BackupException -> it.message ?: "备份文件无效"
                is JSONException -> "备份文件不是合法 JSON"
                else -> it.localizedMessage ?: "备份文件无效"
            }
            throw BackupException(message)
        }

        return backup.records.map { it.toEntity() }
    }

    companion object {
        const val BACKUP_DIRECTORY_NAME = "backup"
        const val BACKUP_FILE_NAME = "keep_account_backup.json"
        const val CURRENT_BACKUP_VERSION = 1
        private const val JSON_INDENT_SPACES = 2
    }
}

data class BackupExportResult(
    val filePath: String,
    val directoryPath: String,
    val recordCount: Int,
)

data class BillBackupFile(
    val version: Int,
    val exportedAt: Long,
    val records: List<BillBackupRecord>,
) {
    fun toJson(): JSONObject =
        JSONObject()
            .put("version", version)
            .put("exportedAt", exportedAt)
            .put(
                "records",
                JSONArray().apply {
                    records.forEach { put(it.toJson()) }
                },
            )

    companion object {
        fun fromJson(json: JSONObject): BillBackupFile {
            val version = json.requireInt("version")
            if (version < 1) {
                throw BackupException("备份文件版本无效")
            }
            if (version > BillBackupManager.CURRENT_BACKUP_VERSION) {
                throw BackupException("备份文件版本过高，请升级 App 后再导入")
            }

            val recordsArray = json.requireArray("records")
            val records = (0 until recordsArray.length()).map { index ->
                val item = recordsArray.optJSONObject(index)
                    ?: throw BackupException("第 ${index + 1} 条账单格式错误")
                BillBackupRecord.fromJson(item, index)
            }

            return BillBackupFile(
                version = version,
                exportedAt = json.requireLong("exportedAt", minValue = 1),
                records = records,
            )
        }
    }
}

data class BillBackupRecord(
    val id: Long,
    val type: BillType,
    val category: Int,
    val amountCents: Long,
    val note: String,
    val occurredAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
) {
    fun toJson(): JSONObject =
        JSONObject()
            .put("id", id)
            .put("type", type.name)
            .put("category", category)
            .put("amountCents", amountCents)
            .put("note", note)
            .put("occurredAt", occurredAt)
            .put("createdAt", createdAt)
            .put("updatedAt", updatedAt)

    fun toEntity(): BillRecordEntity =
        BillRecordEntity(
            id = 0,
            type = type,
            category = category,
            amountCents = amountCents,
            note = note,
            occurredAt = occurredAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    companion object {
        fun fromJson(json: JSONObject, index: Int): BillBackupRecord {
            val row = index + 1
            val type = runCatching { BillType.valueOf(json.requireString("type")) }
                .getOrElse { throw BackupException("第 $row 条账单类型无效") }
            val category = json.requireInt("category", minValue = 0)
            return BillBackupRecord(
                id = json.requireLong("id", minValue = 0),
                type = type,
                category = category,
                amountCents = json.requireLong("amountCents", minValue = 1),
                note = json.requireString("note"),
                occurredAt = json.requireLong("occurredAt", minValue = 1),
                createdAt = json.requireLong("createdAt", minValue = 1),
                updatedAt = json.requireLong("updatedAt", minValue = 1),
            )
        }
    }
}

class BackupException(message: String) : IllegalArgumentException(message)

fun BillRecordEntity.toBackupRecord(): BillBackupRecord =
    BillBackupRecord(
        id = id,
        type = type,
        category = category,
        amountCents = amountCents,
        note = note,
        occurredAt = occurredAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

private fun JSONObject.requireString(name: String): String {
    if (!has(name)) {
        throw BackupException("备份文件缺少字段：$name")
    }
    return getString(name)
}

private fun JSONObject.requireInt(name: String, minValue: Int? = null): Int {
    if (!has(name)) {
        throw BackupException("备份文件缺少字段：$name")
    }
    val value = getInt(name)
    if (minValue != null && value < minValue) {
        throw BackupException("备份文件字段无效：$name")
    }
    return value
}

private fun JSONObject.requireLong(name: String, minValue: Long? = null): Long {
    if (!has(name)) {
        throw BackupException("备份文件缺少字段：$name")
    }
    val value = getLong(name)
    if (minValue != null && value < minValue) {
        throw BackupException("备份文件字段无效：$name")
    }
    return value
}

private fun JSONObject.requireArray(name: String): JSONArray {
    if (!has(name)) {
        throw BackupException("备份文件缺少字段：$name")
    }
    return getJSONArray(name)
}
