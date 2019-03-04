package com.ml.lib_tool

/**
 * 字符帮助类
 */
class CharacterUtil private constructor(){

    companion object {
        val instance   by lazy { CharacterUtil() }
    }

    /**
     * 字符转换为全角
     */
    fun toDBC(input: String): String {
        val c = input.toCharArray()
        for (i in c.indices) {
            if (c[i].toInt() == 12288) {
                c[i] = 32.toChar()
                continue
            }
            if (c[i].toInt() in 65281..65374)
                c[i] = (c[i].toInt() - 65248).toChar()
        }
        return String(c)
    }

}