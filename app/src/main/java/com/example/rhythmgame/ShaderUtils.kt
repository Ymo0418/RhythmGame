// 파일명: ShaderUtils.kt
package com.example.rhythmgame   // 실제 패키지 경로에 맞춰 수정하세요

import android.opengl.GLES20
import android.util.Log

object ShaderUtils {
    fun loadShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)

        // 컴파일 상태 체크
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e("ShaderUtils", "Could not compile shader $type:")
            Log.e("ShaderUtils", GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Shader compilation failed")
        }
        return shader
    }

    fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        // 링크 상태 체크
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e("ShaderUtils", "Could not link program:")
            Log.e("ShaderUtils", GLES20.glGetProgramInfoLog(program))
            GLES20.glDeleteProgram(program)
            throw RuntimeException("Program linking failed")
        }
        return program
    }
}
