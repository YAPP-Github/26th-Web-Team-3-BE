package com.yapp.demo.common.error.analyzer.dto

data class MethodSignatureInfo(
    val className: String,
    val lineNumber: Int,
    val parameters: List<ParameterInfo>,
    val returnType: String,
)
