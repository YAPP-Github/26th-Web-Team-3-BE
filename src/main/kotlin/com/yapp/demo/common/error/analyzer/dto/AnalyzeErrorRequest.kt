package com.yapp.demo.common.error.analyzer.dto

data class AnalyzeErrorRequest(
    val path: String,
    val httpMethod: String,
    val exception: Exception,
    val userId: Long?,
    val notify: Boolean,
    val logId: String?,
)
