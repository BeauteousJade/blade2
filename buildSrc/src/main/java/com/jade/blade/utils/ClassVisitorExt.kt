package com.jade.blade.utils

import groovyjarjarasm.asm.Opcodes.ACC_FINAL
import groovyjarjarasm.asm.Opcodes.ACC_PRIVATE
import groovyjarjarasm.asm.Opcodes.RETURN
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor


/**
 * 给类添加一个方法。
 */
fun ClassVisitor.addMethod(
    name: String,
    descriptor: String = "()V",
    signature: String? = null,
    access: Int = ACC_FINAL,
    callback: MethodVisitor.() -> Unit = {
        visitCode()
        visitInsn(RETURN)
        visitMaxs(0, 1)
        visitEnd()
    }
) {
    visitMethod(access, name, descriptor, signature, null).apply {
        callback.invoke(this)
    }
}

/**
 * 给类添加一个变量。
 */
fun ClassVisitor.addField(
    name: String,
    descriptor: String,
    signature: String? = null,
    access: Int = ACC_PRIVATE or ACC_FINAL,
    value: String? = null,
) {
    visitField(access, name, descriptor, signature, value).apply {
        visitEnd()
    }
}