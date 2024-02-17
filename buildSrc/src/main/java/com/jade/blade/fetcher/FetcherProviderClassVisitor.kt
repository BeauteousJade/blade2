package com.jade.blade.fetcher

import com.jade.blade.BaseClassVisitor
import com.jade.blade.utils.addMethod
import groovyjarjarasm.asm.Opcodes.*
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label


class FetcherProviderClassVisitor(
    nextVisitor: ClassVisitor,
    className: String
) : BaseClassVisitor(nextVisitor, className) {


    companion object {
        private val getFetcherMethodInfo = Triple(
            "getFetcher", // name
            "()Lcom/blade/inject/fetcher/Fetcher;", // descriptor
            "()Lcom/blade/inject/fetcher/Fetcher<*>;" // 返回值的signature
        )

        private val ensureFetcherMethodInfo = Triple(
            "ensureFetcher", // name
            "()V", // descriptor
            null // 返回值的signature
        )

        private const val IS_ROOT_NAME = "isRoot"
    }

    private val interfaceName = FetcherTransform.INTERFACE_NAME.replace(".", "/")
    private var mIsRoot: Boolean = true
    private var mSuperName: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        mSuperName = superName
        if (interfaces == null || !interfaces.contains(interfaceName)) {
            val newInterfaces = ArrayList<String>().apply {
                add(interfaceName)
                interfaces?.let {
                    addAll(it)
                }
            }.toArray(arrayOf<String>())
            super.visit(version, access, name, signature, superName, newInterfaces)
        } else {
            super.visit(version, access, name, signature, superName, interfaces)
        }
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return object : AnnotationVisitor(ASM5, super.visitAnnotation(descriptor, visible)) {

            override fun visit(name: String?, value: Any?) {
                if (IS_ROOT_NAME == name) {
                    mIsRoot = value as Boolean
                }
            }
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        addProvideMethodInfoMethod(getFieldInfo())
    }

    /**
     * 添加provider方法，外部可以通过此方法获取Fetcher或者Injector.
     */
    private fun addProvideMethodInfoMethod(fieldInfo: Triple<String, String, String?>) {
        val provideMethodInfo = getProvideMethodInfo()
        val ensureMethodInfo = getEnsureMethodInfo()
        addMethod(
            provideMethodInfo.first,
            provideMethodInfo.second,
            provideMethodInfo.third,
            ACC_PUBLIC or ACC_VARARGS
        ) {
            // 添加Nullable注解
            visitAnnotation("Landroidx/annotation/Nullable;", false).apply {
                visitEnd()
            }
            visitCode()
            visitVarInsn(ALOAD, 0)
            visitMethodInsn(
                INVOKESPECIAL,
                newClassName,
                ensureMethodInfo.first,
                ensureMethodInfo.second,
                false
            )
            visitVarInsn(ALOAD, 0)
            visitFieldInsn(
                GETFIELD,
                newClassName,
                fieldInfo.first,
                fieldInfo.second
            )
            visitInsn(ARETURN)
            visitMaxs(1, 1)
            visitEnd()
        }
    }

    override fun addEnsureMethod(fieldInfo: Triple<String, String, String?>) {
        val ensureMethodInfo = getEnsureMethodInfo()
        addMethod(
            ensureMethodInfo.first,
            ensureMethodInfo.second,
            ensureMethodInfo.third,
            ACC_PRIVATE
        ) {
            visitCode()
            visitVarInsn(ALOAD, 0)
            visitFieldInsn(
                GETFIELD,
                newClassName,
                fieldInfo.first,
                fieldInfo.second
            )
            val label0 = Label()
            visitJumpInsn(IFNONNULL, label0)
            visitVarInsn(ALOAD, 0)
            val className = if (mIsRoot) {
                interfaceName
            } else {
                mSuperName!!
            }
            visitMethodInsn(
                INVOKESPECIAL,
                className,
                getFetcherMethodInfo.first,
                getFetcherMethodInfo.second,
                mIsRoot
            )
            visitVarInsn(ASTORE, 1)
            visitVarInsn(ALOAD, 0)
            visitTypeInsn(NEW, "${newClassName}${getSuffix()}")
            visitInsn(DUP)
            visitVarInsn(ALOAD, 1)
            visitMethodInsn(
                INVOKESPECIAL,
                "${newClassName}${getSuffix()}",
                "<init>",
                "(Lcom/blade/inject/fetcher/Fetcher;)V",
                false
            )
            visitFieldInsn(
                PUTFIELD,
                newClassName,
                fieldInfo.first,
                fieldInfo.second
            )
            visitLabel(label0)
            visitInsn(RETURN)
            visitMaxs(4, 2)
            visitEnd()
        }
    }

    private fun getProvideMethodInfo(): Triple<String, String, String?> {
        return getFetcherMethodInfo
    }

    override fun getFieldInfo(): Triple<String, String, String?> {
        return Triple(
            "fetcher", // name
            "L${newClassName}Fetcher;", // descriptor
            null // signature
        )
    }

    override fun getEnsureMethodInfo(): Triple<String, String, String?> {
        return ensureFetcherMethodInfo
    }


    override fun getSuffix() = "Fetcher"

}