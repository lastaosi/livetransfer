package com.jh.livetransfer.data.repository

import com.jh.livetransfer.data.model.ExchangeResponse
import com.jh.livetransfer.data.remote.KTorClient
import com.jh.livetransfer.util.L
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject

/**
 * Frankfurter API를 통해 환율 정보를 조회하는 Repository.
 *
 * Ktor HttpClient로 REST 호출. 내부에서 try-catch로 감싸 [Result]로 반환하므로
 * 호출부에서 예외 처리 없이 [Result.onSuccess] / [Result.onFailure]로 분기 가능.
 */
class ExchangeRepository @Inject constructor() {
    private val client = KTorClient.client
    private val baseUrl = "https://api.frankfurter.app"

    /**
     * 기준 통화 대비 최신 환율 목록을 조회한다.
     *
     * @param base 기준 통화 코드 (기본값: "USD")
     * @return 성공 시 [ExchangeResponse], 실패 시 예외를 담은 [Result]
     */
    suspend fun getExchangeRates(base: String = "USD"): Result<ExchangeResponse> {
        return try {
            val response = client.get("$baseUrl/latest"){
                parameter("from",base)
            }
            L.d("환율 응답 : ${response.bodyAsText()}")
            Result.success(response.body<ExchangeResponse>())
        }catch (e: Exception){
            L.d("환율 실패 : ${e.message}")
            Result.failure(e)
        }
    }
}