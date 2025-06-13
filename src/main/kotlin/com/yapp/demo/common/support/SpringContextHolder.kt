package com.yapp.demo.common.support

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
object SpringContextHolder : ApplicationContextAware {
    private lateinit var context: ApplicationContext

    @EventListener
    fun onContextClosed(@Suppress("UNUSED_PARAMETER") event: ContextClosedEvent) {
        // 메모리 누수 방지
        if (::context.isInitialized) {
            (context as? ConfigurableApplicationContext)?.close()
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    fun <T> getBean(clazz: Class<T>): T = context.getBean(clazz)
}
