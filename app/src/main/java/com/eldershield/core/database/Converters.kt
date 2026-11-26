package com.eldershield.core.database

import androidx.room.TypeConverter
import com.eldershield.domain.models.SecurityStatus
import com.eldershield.domain.models.SeverityLevel
import com.eldershield.domain.models.ThreatCategory

class Converters {
    @TypeConverter
    fun fromSecurityStatus(status: SecurityStatus?): String? = status?.name

    @TypeConverter
    fun toSecurityStatus(value: String?): SecurityStatus? =
        value?.let { enumValueOf<SecurityStatus>(it) }

    @TypeConverter
    fun fromSeverityLevel(severity: SeverityLevel?): String? = severity?.name

    @TypeConverter
    fun toSeverityLevel(value: String?): SeverityLevel? =
        value?.let { enumValueOf<SeverityLevel>(it) }

    @TypeConverter
    fun fromThreatCategory(category: ThreatCategory?): String? = category?.name

    @TypeConverter
    fun toThreatCategory(value: String?): ThreatCategory? =
        value?.let { enumValueOf<ThreatCategory>(it) }
}
