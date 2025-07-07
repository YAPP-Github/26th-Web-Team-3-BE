package com.yapp.lettie.common.error.analyzer.dto

data class AnalyzeErrorResponse(
    val success: Boolean,
    val json: Json,
) {
    data class Json(
        val action: String,
        val reason: String,
        val guide: String,
        val inference: String,
        val apiSummary: String,
    )
}
