package com.jh.livetransfer.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CityDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object{
        private val CITY_LIST_KEY = stringPreferencesKey("city_list")
        private const val SEPARATOR = ","
    }

    val cityName: Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[CITY_LIST_KEY]
            ?.split(SEPARATOR)
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }

    suspend fun saveCityNames(cities: List<String>){
        dataStore.edit { preferences ->
            preferences[CITY_LIST_KEY] = cities.joinToString(SEPARATOR)
        }
    }
}