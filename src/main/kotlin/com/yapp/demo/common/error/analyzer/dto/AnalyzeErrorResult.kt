package com.yapp.demo.common.error.analyzer.dto

data class AnalyzeErrorResult(
    val action: String,
    val reason: String,
    val guide: String,
    val inference: String,
)
