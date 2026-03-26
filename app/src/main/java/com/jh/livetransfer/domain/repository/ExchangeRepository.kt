package com.jh.livetransfer.domain.repository

import com.jh.livetransfer.data.model.ExchangeResponse

interface ExchangeRepository {
    suspend fun getExchangeRates(base: String = "USD"): Result<ExchangeResponse>
}