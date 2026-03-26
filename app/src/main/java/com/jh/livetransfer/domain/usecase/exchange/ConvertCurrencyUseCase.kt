package com.jh.livetransfer.domain.usecase.exchange

import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor() {
    operator fun invoke(
        rates: Map<String, Double>,
        target: String,
        amount : String
    ) : Double? {
        val rate = rates[target] ?: return null
        val amountDouble = amount.toDoubleOrNull() ?: 1.0
        return rate * amountDouble
    }
}