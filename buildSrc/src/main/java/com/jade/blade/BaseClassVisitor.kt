package com.jade.blade

import com.jade.blade.utils.addField
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

abstract class BaseClassVisitor(
    nextVisitor: ClassVisitor,
    protected val className: String
) : ClassVisitor(Opcodes.ASM5, nextVisitor) {

    protected val newClassName = className.replace(".", "/")

    override fun visitEnd() {
        super.visitEnd()
        val fieldInfo = getFieldInfo()
        // 添加对应的变量
        addField(fieldInfo)
        // 添加ensure的方法，用于保证变量必须初始化。
        addEnsureMethod(fieldInfo)
    }


    private fun addField(fieldInfo: Triple<String, String, String?>) {
        addField(
            fieldInfo.first, fieldInfo.second, fieldInfo.third,
            groovyjarjarasm.asm.Opcodes.ACC_PRIVATE
        )
    }

    protected abstract fun getFieldInfo(): Triple<String, String, String?>

    protected abstract fun getEnsureMethodInfo(): Triple<String, String, String?>

    protected abstract fun addEnsureMethod(fieldInfo: Triple<String, String, String?>)

    protected abstract fun getSuffix(): String
}