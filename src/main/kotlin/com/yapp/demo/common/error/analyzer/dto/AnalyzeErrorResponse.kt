package com.yapp.demo.common.error.analyzer.dto

data class AnalyzeErrorResponse(
    val json: AnalyzeErrorResult,
    val success: Boolean,
    val question: String,
    val chatId: String,
    val chatMessageId: String,
    val isStreamValid: Boolean,
    val sessionId: String,
)
