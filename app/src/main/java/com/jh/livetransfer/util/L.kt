package com.jh.livetransfer.util

import com.google.gson.Gson
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

object L {

    // 릴리즈 빌드 시 로그 자동 차단
    private val PRINT_LOG = true
    const val LOG_PREFIX = "AI_BIZ"
    private const val LOG_TAG = "${LOG_PREFIX}_LOG"
    private const val NULL = "null"

    init {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .tag(LOG_TAG)
            .methodOffset(1) // L 객체를 거치므로 offset 1 유지 (클릭 시 실제 호출부로 이동)
            .methodCount(3)
            .build()

        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?) = PRINT_LOG
        })
    }

    fun v(log: String?) = Logger.v(log ?: NULL)
    fun i(log: String?) = Logger.i(log ?: NULL)

    // 객체가 들어오면 알아서 Json으로 예쁘게 파싱
    fun d(log: Any?) = log?.let { Logger.json(Gson().toJson(it)) } ?: d(NULL)
    fun d(log: String?) = Logger.d(log ?: NULL)

    // Kotlin 'zip' 활용으로 우아하게 리팩토링 (IndexOutOfBoundsException 완벽 방지)
    fun d(titles: List<String>, contents: List<Any?>) {
        val logs = titles.zip(contents) { title, content ->
            "$title : $content"
        }.joinToString("\n")
        Logger.d(logs)
    }

    fun d(vararg items: Pair<String, Any?>) {
        Logger.d(items.joinToString("\n") { "${it.first} : ${it.second}" })
    }

    fun d(vararg items: String) {
        Logger.d(items.joinToString("\n"))
    }


    fun d(items: List<String?>) {
        Logger.d(items.joinToString("\n"))
    }

    fun w(log: String?) = Logger.w(log ?: NULL)
    fun e(log: String?) = Logger.e(log ?: NULL)
    fun e(e: Throwable) = Logger.e(e, e.toString())
    fun wtf(log: String?) = Logger.wtf(log ?: NULL)
    fun json(json: String?) = Logger.json(json ?: NULL)
    fun xml(xml: String?) = Logger.xml(xml ?: NULL)
}