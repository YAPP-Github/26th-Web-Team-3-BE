package com.yapp.demo.common.error.analyzer

import com.yapp.demo.common.error.analyzer.dto.AnalyzeErrorRequest
import com.yapp.demo.common.error.analyzer.dto.AnalyzeErrorResponse
import com.yapp.demo.common.error.analyzer.dto.MethodSignatureInfo
import com.yapp.demo.common.error.analyzer.dto.ParameterInfo
import com.yapp.demo.infrastructure.llm.LlmProperties
import com.yapp.demo.infrastructure.llm.call
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LineNumberNode
import org.objectweb.asm.tree.MethodNode
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class LlmErrorAnalyzer(
    private val llmWebClient: WebClient,
    private val llmProperties: LlmProperties,
) : ErrorAnalyzer {
    override fun analyze(request: AnalyzeErrorRequest): AnalyzeErrorResponse {
        val filteredStackTrace = filterStackTrace(request.exception)

        return llmWebClient.call(
            method = HttpMethod.POST,
            uri = "/api/v1/prediction/${llmProperties.id}",
            requestBody =
            mapOf(
                "question" to
                    """
                        service: ${llmProperties.serviceName},
                        httpMethod: ${request.httpMethod.lowercase()},
                        requestUrl: ${request.path},
                        cause: ${getStackTraceAsString(request.exception.message!!, filteredStackTrace)},
                        methodSignatures: ${getMethodSignatures(filteredStackTrace)}
                        """.trimIndent(),
            ),
            headersConsumer = { it.setBearerAuth(llmProperties.key) },
        )
    }

    // 외부 라이브러리 예외는 제외
    private fun filterStackTrace(exception: Exception): List<StackTraceElement> {
        return exception.stackTrace.filter { it.className.startsWith(BASE_PACKAGE_PREFIX) }
    }

    private fun getStackTraceAsString(
        message: String,
        stackTrace: List<StackTraceElement>,
    ): String {
        return buildString {
            appendLine(message)
            stackTrace.forEach { appendLine(it) }
        }
    }

    private fun getMethodSignatures(stackTraceElements: List<StackTraceElement>): List<MethodSignatureInfo> {
        return stackTraceElements.mapNotNull { getMethodSignature(it.className, it.lineNumber) }
    }

    private fun getMethodSignature(
        className: String,
        lineToFind: Int,
    ): MethodSignatureInfo? {
        return this.javaClass.classLoader
            .getResourceAsStream(className.replace('.', '/') + ".class")
            ?.use { classInputStream ->
                val classNode = ClassNode()
                val classReader = ClassReader(classInputStream)
                classReader.accept(classNode, 0)

                for (method in classNode.methods) {
                    val lineNumbers = mutableListOf<Int>()

                    for (insn in method.instructions) {
                        if (insn is LineNumberNode) {
                            lineNumbers.add(insn.line)
                        }
                    }

                    if (lineToFind in lineNumbers) {
                        return extractMethodSignature(className, lineToFind, method)
                    }
                }

                null // use 블록의 반환값
            }
    }

    private fun extractMethodSignature(
        className: String,
        lineNumber: Int,
        method: MethodNode,
    ): MethodSignatureInfo {
        val argumentTypes = Type.getArgumentTypes(method.desc)

        val parameterNames =
            method.localVariables
                .asSequence()
                .drop(1)
                .take(argumentTypes.size)
                .map { it.name }
                .toList()

        val parameters =
            argumentTypes.mapIndexed { index, type ->
                ParameterInfo(parameterNames.getOrNull(index), type.className)
            }

        return MethodSignatureInfo(
            className = className,
            lineNumber = lineNumber,
            parameters = parameters,
            returnType = Type.getReturnType(method.desc).className,
        )
    }

    companion object {
        private const val BASE_PACKAGE_PREFIX = "com.yapp"
    }
}
