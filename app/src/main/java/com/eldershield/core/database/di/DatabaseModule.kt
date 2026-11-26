package com.eldershield.core.database.di

import android.content.Context
import androidx.room.Room
import com.eldershield.core.database.ElderShieldDatabase
import com.eldershield.data.dao.AppSecurityDao
import com.eldershield.data.dao.LearningDao
import com.eldershield.data.dao.ScanDao
import com.eldershield.data.dao.ThreatDao
import com.eldershield.data.dao.UserContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ElderShieldDatabase {
        return Room.databaseBuilder(
            context,
            ElderShieldDatabase::class.java,
            ElderShieldDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL(
                        "INSERT OR IGNORE INTO user_profiles (id, displayName, preferredLanguage, isLargeTextEnabled, isVoiceGuidanceEnabled, isFamilyAssistanceEnabled, createdAt) VALUES (1, 'Elder User', 'en', 0, 1, 0, ${System.currentTimeMillis()})"
                    )
                }
            })
            .build()
    }

    @Provides
    fun provideThreatDao(db: ElderShieldDatabase): ThreatDao = db.threatDao()

    @Provides
    fun provideScanDao(db: ElderShieldDatabase): ScanDao = db.scanDao()

    @Provides
    fun provideAppSecurityDao(db: ElderShieldDatabase): AppSecurityDao = db.appSecurityDao()

    @Provides
    fun provideUserContactDao(db: ElderShieldDatabase): UserContactDao = db.userContactDao()

    @Provides
    fun provideLearningDao(db: ElderShieldDatabase): LearningDao = db.learningDao()
}
