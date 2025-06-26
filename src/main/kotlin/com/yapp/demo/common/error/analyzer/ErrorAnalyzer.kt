package com.yapp.demo.common.error.analyzer

import com.yapp.demo.common.error.analyzer.dto.AnalyzeErrorRequest
import com.yapp.demo.common.error.analyzer.dto.AnalyzeErrorResponse

interface ErrorAnalyzer {
    fun analyze(request: AnalyzeErrorRequest): AnalyzeErrorResponse
}
