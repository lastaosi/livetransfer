package com.jh.livetransfer.ui.screen.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jh.livetransfer.data.model.WeatherResponse
import com.jh.livetransfer.data.repository.WeatherRepository
import com.jh.livetransfer.util.L
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 날씨 화면 ViewModel.
 *
 * 상태:
 * - [currentWeather]: 현재 위치 날씨 (GPS 기반, null이면 미조회)
 * - [cityWeatherList]: 사용자가 추가한 도시 목록 (중복 체크 포함)
 * - [isLoading]: 현재 위치 날씨 조회 중 로딩 상태
 * - [errorMessage]: 에러 발생 시 Toast 메시지용. clearError()로 초기화.
 *
 * 도시 목록은 메모리에만 보관 (앱 재시작 시 초기화됨).
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    // 현재 위치 날씨
    private val _currentWeather = MutableStateFlow<WeatherResponse?>(null)
    val currentWeather : StateFlow<WeatherResponse?> =_currentWeather

    // 추가한 도시 날씨 리스트
    private val _cityWeatherList = MutableStateFlow<List<WeatherResponse>>(emptyList())
    val cityWeatherList : StateFlow<List<WeatherResponse>> = _cityWeatherList

    // 로딩상태
    private val _isLoading = MutableStateFlow(false)
     val isLoading : StateFlow<Boolean> = _isLoading

    // 에러상태
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage : StateFlow<String?> = _errorMessage

    //현재위치 날씨 조회
    fun fetchCurrentLocationWeather(lat:Double,lon:Double){
        viewModelScope.launch{
            _isLoading.value = true
            weatherRepository.getWeatherByLocation(lat,lon)
                .onSuccess { _currentWeather.value = it }
                .onFailure {
                    L.d("날씨 실패 : ${it.message}")
                    _errorMessage.value = "현재 위치 날씨를 가져올수 없어요" }
            _isLoading.value = false
        }
    }

    // 도시 날씨 추가
    fun addCity(city:String){
        viewModelScope.launch {
            weatherRepository.getWeatherByCity(city)
                .onSuccess { response ->
                    // 중복 도시 체크
                    val isDuplicate = _cityWeatherList.value.any { it.name == response.name }
                    if(!isDuplicate){
                        _cityWeatherList.value = _cityWeatherList.value + response
                    }else{
                        _errorMessage.value = "이미 추가된 도시입니다."
                    }
                }
                .onFailure { _errorMessage.value = "도시를 찾을 수 없어요" }
        }
    }

    // 도시 삭제
    fun removeCity(cityName : String){
        _cityWeatherList.value = _cityWeatherList.value.filter{ it.name != cityName}
    }

    // 에러 메시지 초기화
    fun clearError(){
        _errorMessage.value = null
    }
}