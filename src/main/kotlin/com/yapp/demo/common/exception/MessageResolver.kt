package com.yapp.demo.common.exception

import java.text.MessageFormat
import java.util.*

object MessageResolver {
    private const val BUNDLE_NAME = "api-error-messages"
    private val bundle: ResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME)

    fun resolve(code: String, args: Array<out Any>? = null): String? {
        return try {
            val pattern = bundle.getString(code)
            if (args.isNullOrEmpty()) pattern else MessageFormat.format(pattern, *args)
        } catch (e: MissingResourceException) {
            null
        }
    }
}
