package com.example.keepaccount.data

import android.content.Context
import com.example.keepaccount.AppLocaleManager
import com.example.keepaccount.R
import java.io.File
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BillBackupManager(
    context: Context,
) {
    private val appContext = context.applicationContext
    init {
        BackupStrings.bind(appContext)
    }

    val backupDirectory: File
        get() = File(externalFilesRoot(), BACKUP_DIRECTORY_NAME)

    val backupFile: File
        get() = File(backupDirectory, BACKUP_FILE_NAME)

    val backupDirectoryLabel: String
        get() = "Android/data/${appContext.packageName}/files/$BACKUP_DIRECTORY_NAME"

    val backupFileLabel: String
        get() = "$backupDirectoryLabel/$BACKUP_FILE_NAME"

    fun ensureBackupDirectoryExists(): Boolean =
        runCatching {
            createBackupDirectoryIfNeeded()
            true
        }.getOrDefault(false)

    fun export(records: List<BillRecordEntity>): BackupExportResult {
        val backup = BillBackupFile(
            version = CURRENT_BACKUP_VERSION,
            exportedAt = System.currentTimeMillis(),
            records = records.map { it.toBackupRecord() },
        )
        val content = backup.toJson().toString(JSON_INDENT_SPACES)

        writeToAppSpecificExternalStorage(content)

        return BackupExportResult(
            filePath = backupFileLabel,
            directoryPath = backupDirectoryLabel,
            recordCount = records.size,
        )
    }

    fun readRecords(): List<BillRecordEntity> {
        val content = readFromAppSpecificExternalStorage()
        return parseRecords(content)
    }

    private fun parseRecords(content: String): List<BillRecordEntity> {
        val backup = runCatching {
            BillBackupFile.fromJson(JSONObject(content))
        }.getOrElse {
            val message = when (it) {
                is BackupException -> it.message ?: localizedString(R.string.backup_file_invalid)
                is JSONException -> localizedString(R.string.backup_file_not_json)
                else -> it.localizedMessage ?: localizedString(R.string.backup_file_invalid)
            }
            throw BackupException(message)
        }

        return backup.records.map { it.toEntity() }
    }

    private fun writeToAppSpecificExternalStorage(content: String) {
        createBackupDirectoryIfNeeded()

        runCatching {
            backupFile.writeText(content, Charsets.UTF_8)
        }.getOrElse {
            throw BackupException(localizedString(R.string.backup_file_write_failed, it.localizedMessage ?: localizedString(R.string.common_unknown_error)))
        }
    }

    private fun createBackupDirectoryIfNeeded() {
        val directory = backupDirectory
        if (!directory.exists() && !directory.mkdirs()) {
            throw BackupException(localizedString(R.string.backup_directory_create_failed, directory.absolutePath))
        }
    }

    private fun readFromAppSpecificExternalStorage(): String {
        val file = backupFile
        if (!file.exists()) {
            throw BackupException(localizedString(R.string.backup_file_missing_app_specific, backupFileLabel))
        }

        return runCatching {
            file.readText(Charsets.UTF_8)
        }.getOrElse {
            throw BackupException(localizedString(R.string.backup_file_read_failed, it.localizedMessage ?: localizedString(R.string.common_unknown_error)))
        }
    }

    private fun externalFilesRoot(): File =
        appContext.getExternalFilesDir(null)
            ?: throw BackupException(localizedString(R.string.backup_external_storage_unavailable))

    private fun localizedString(resId: Int, vararg args: Any): String =
        AppLocaleManager.wrap(appContext).getString(resId, *args)

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
                throw BackupException(BackupStrings.fileVersionInvalid)
            }
            if (version > BillBackupManager.CURRENT_BACKUP_VERSION) {
                throw BackupException(BackupStrings.fileVersionTooHigh)
            }

            val recordsArray = json.requireArray("records")
            val records = (0 until recordsArray.length()).map { index ->
                val item = recordsArray.optJSONObject(index)
                    ?: throw BackupException(BackupStrings.recordFormatInvalid(index + 1))
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
                .getOrElse { throw BackupException(BackupStrings.recordTypeInvalid(row)) }
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

private object BackupStrings {
    private var context: Context? = null

    val fileVersionInvalid: String
        get() = string(R.string.backup_file_version_invalid, "Invalid backup file version")

    val fileVersionTooHigh: String
        get() = string(R.string.backup_file_version_too_high, "Backup file version is too high. Update the app and import again.")

    fun bind(context: Context) {
        this.context = context.applicationContext
    }

    fun recordFormatInvalid(row: Int): String =
        string(R.string.backup_record_format_invalid, "Backup record $row format is invalid", row)

    fun recordTypeInvalid(row: Int): String =
        string(R.string.backup_record_type_invalid, "Backup record $row type is invalid", row)

    fun missingField(name: String): String =
        string(R.string.backup_file_missing_field, "Backup file is missing field: $name", name)

    fun invalidField(name: String): String =
        string(R.string.backup_file_field_invalid, "Backup file field is invalid: $name", name)

    private fun string(resId: Int, fallback: String, vararg args: Any): String {
        val currentContext = context ?: return fallback
        return AppLocaleManager.wrap(currentContext).getString(resId, *args)
    }
}

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
        throw BackupException(BackupStrings.missingField(name))
    }
    return getString(name)
}

private fun JSONObject.requireInt(name: String, minValue: Int? = null): Int {
    if (!has(name)) {
        throw BackupException(BackupStrings.missingField(name))
    }
    val value = getInt(name)
    if (minValue != null && value < minValue) {
        throw BackupException(BackupStrings.invalidField(name))
    }
    return value
}

private fun JSONObject.requireLong(name: String, minValue: Long? = null): Long {
    if (!has(name)) {
        throw BackupException(BackupStrings.missingField(name))
    }
    val value = getLong(name)
    if (minValue != null && value < minValue) {
        throw BackupException(BackupStrings.invalidField(name))
    }
    return value
}

private fun JSONObject.requireArray(name: String): JSONArray {
    if (!has(name)) {
        throw BackupException(BackupStrings.missingField(name))
    }
    return getJSONArray(name)
}
