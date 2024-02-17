package com.jade.blade.injector

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

abstract class InjectorTransform : AsmClassVisitorFactory<InstrumentationParameters.None> {

    companion object {
        const val INTERFACE_NAME = "com.blade.inject.injector.InjectorProvider"
        const val SUPPORT_ANNOTATION_NAME = "com.blade.annotation.Injector"
    }

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return InjectorProviderClassVisitor(
            nextClassVisitor,
            onlyHasAnnotation(classContext.currentClassData),
            classContext.currentClassData.className
        )
    }

    private fun onlyHasAnnotation(classData: ClassData): Boolean {
        return !classData.interfaces.contains(INTERFACE_NAME)
                && classData.classAnnotations.contains(SUPPORT_ANNOTATION_NAME)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.interfaces.contains(INTERFACE_NAME)
                || classData.classAnnotations.contains(SUPPORT_ANNOTATION_NAME)
    }
}