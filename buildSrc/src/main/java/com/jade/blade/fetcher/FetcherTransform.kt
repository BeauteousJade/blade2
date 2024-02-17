package com.jade.blade.fetcher

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

abstract class FetcherTransform : AsmClassVisitorFactory<InstrumentationParameters.None> {

    companion object {
        const val INTERFACE_NAME = "com.blade.inject.fetcher.FetcherProvider"
        const val SUPPORT_ANNOTATION_NAME = "com.blade.annotation.Module"
    }

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return FetcherProviderClassVisitor(
            nextClassVisitor,
            classContext.currentClassData.className
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
//        return false
        return classData.classAnnotations.contains(SUPPORT_ANNOTATION_NAME)
    }
}