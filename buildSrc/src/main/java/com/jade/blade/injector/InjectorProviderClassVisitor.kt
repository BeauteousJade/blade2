package com.jade.blade.injector

import com.jade.blade.BaseClassVisitor
import com.jade.blade.utils.addMethod
import groovyjarjarasm.asm.Opcodes.*
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label

class InjectorProviderClassVisitor(
    nextVisitor: ClassVisitor,
    private val onlyHasAnnotation: Boolean,
    className: String
) : BaseClassVisitor(nextVisitor, className) {


    companion object {

        private val doInjectMethodInfo = Triple(
            "doInject", // name
            "([Ljava/lang/Object;)V", // descriptor
            null // signature
        )

        private val ensureInjectorMethodInfo = Triple(
            "ensureInjector", // name
            "()V", // descriptor
            null // 返回值的signature
        )

        // 静态方法
        private val injectMethodInfo = Triple(
            "com/blade/inject/injector/Injector", // owner
            "inject", // method name
            "(Ljava/lang/Object;[Ljava/lang/Object;)V", // descriptor
        )
    }

    private var mIsRoot: Boolean = false
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
        val interfaceName = InjectorTransform.INTERFACE_NAME.replace(".", "/")
        if (onlyHasAnnotation) {
            mIsRoot = true
            val newInterfaces = ArrayList<String>().apply {
                add(interfaceName)
                interfaces?.let {
                    addAll(it)
                }
            }.toArray(arrayOf<String>())
            super.visit(version, access, name, signature, superName, newInterfaces)
        } else {
            mIsRoot = interfaces?.contains(interfaceName) ?: false
            super.visit(version, access, name, signature, superName, interfaces)
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        addDoInjectMethod()
    }

    private fun addDoInjectMethod() {
        addMethod(
            doInjectMethodInfo.first,
            doInjectMethodInfo.second,
            doInjectMethodInfo.third,
            ACC_PUBLIC or ACC_VARARGS
        ) {

            visitCode()
            visitVarInsn(ALOAD, 0)
            // 调用super的doInject方法。
            if (!mIsRoot) {
                visitVarInsn(ALOAD, 1)
                visitMethodInsn(
                    INVOKESPECIAL,
                    mSuperName,
                    doInjectMethodInfo.first,
                    doInjectMethodInfo.second,
                    false
                )
                visitVarInsn(ALOAD, 0)
            }
            val ensureMethodInfo = getEnsureMethodInfo()
            visitMethodInsn(
                INVOKESPECIAL,
                newClassName,
                ensureMethodInfo.first,
                ensureMethodInfo.second,
                false
            )
            visitVarInsn(ALOAD, 0)
            val fieldInfo = getFieldInfo()
            visitFieldInsn(GETFIELD, newClassName, fieldInfo.first, fieldInfo.second)
            visitVarInsn(ALOAD, 0)
            visitVarInsn(ALOAD, 1)
            visitMethodInsn(
                INVOKEINTERFACE,
                injectMethodInfo.first,
                injectMethodInfo.second,
                injectMethodInfo.third,
                true
            )
            visitInsn(RETURN)
            visitMaxs(3, 2)
            visitEnd()
        }
    }

    override fun getFieldInfo(): Triple<String, String, String?> {
        return Triple(
            "injector",
            "L${newClassName}Injector;",
            "()Lcom/blade/inject/injector/Injector<L${newClassName};>;"
        )
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
            val label = Label()
            visitJumpInsn(IFNONNULL, label)
            visitVarInsn(ALOAD, 0)
            val suffix = getSuffix()
            visitTypeInsn(NEW, "${newClassName}${suffix}")
            visitInsn(DUP)
            visitMethodInsn(
                INVOKESPECIAL,
                "${newClassName}${suffix}",
                "<init>",
                "()V",
                false
            )
            visitFieldInsn(
                PUTFIELD,
                newClassName,
                fieldInfo.first,
                fieldInfo.second
            )
            visitLabel(label)
            visitInsn(RETURN)
            visitMaxs(3, 1)
            visitEnd()
        }
    }

    override fun getEnsureMethodInfo() = ensureInjectorMethodInfo

    override fun getSuffix() = "Injector"


}