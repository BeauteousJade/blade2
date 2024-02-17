package com.jade.blade

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.jade.blade.fetcher.FetcherTransform
import com.jade.blade.injector.InjectorTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class BladePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.getByType(AndroidComponentsExtension::class.java).apply {
            onVariants { variant ->
                variant.instrumentation.apply {
                    transformClassesWith(
                        InjectorTransform::class.java, InstrumentationScope.PROJECT
                    ) {}
                    transformClassesWith(
                        FetcherTransform::class.java, InstrumentationScope.PROJECT
                    ) {}
                    setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
                }
            }
        }
    }
}