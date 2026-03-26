package com.jh.livetransfer.domain.usecase.exchange

import com.jh.livetransfer.data.model.ExchangeResponse
import com.jh.livetransfer.domain.repository.ExchangeRepository
import javax.inject.Inject

class GetExchangeRatesUseCase @Inject constructor(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(base:String="USD"): Result<ExchangeResponse>{
        return repository.getExchangeRates(base)
    }
}