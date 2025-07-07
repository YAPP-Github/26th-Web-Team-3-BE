package com.yapp.lettie.common.error.analyzer

import com.yapp.lettie.common.error.analyzer.dto.AnalyzeErrorRequest
import com.yapp.lettie.common.error.analyzer.dto.AnalyzeErrorResponse

interface ErrorAnalyzer {
    fun analyze(request: AnalyzeErrorRequest): AnalyzeErrorResponse
}
