package com.example.rhythmgame.Component

import android.opengl.GLES20
import android.util.Log

class Comp_Shader(vs: String, fs: String) : Component() {
    private val VtxShaderStr = vs
    private val FrgShaderStr = fs
    private var VtxShader : Int
    private var FrgShader : Int
    var programHandle = 0

    init {
        VtxShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(VtxShader, vs)
        GLES20.glCompileShader(VtxShader)
        Check_CompiledShader(VtxShader)

        FrgShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(FrgShader, fs)
        GLES20.glCompileShader(FrgShader)
        Check_CompiledShader(FrgShader)

        programHandle = GLES20.glCreateProgram()
        GLES20.glAttachShader(programHandle, VtxShader)
        GLES20.glAttachShader(programHandle, FrgShader)
        GLES20.glLinkProgram(programHandle)
        Check_LinkedProgram()
    }

    private fun Check_CompiledShader(shader: Int) {
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e("Shader", "Could not compile shader :")
            Log.e("Shader", GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Shader compilation failed")
        }
    }

    private fun Check_LinkedProgram() {
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e("ShaderUtils", "Could not link program:")
            Log.e("ShaderUtils", GLES20.glGetProgramInfoLog(programHandle))
            GLES20.glDeleteProgram(programHandle)
            throw RuntimeException("Program linking failed")
        }
    }

    public fun Use_Program() {
        GLES20.glUseProgram(programHandle)
    }
    public fun Get_Attribute(name: String) : Int {
        return GLES20.glGetAttribLocation(programHandle, name)
    }
    public fun Get_UniformAttribute(name: String) : Int {
        return GLES20.glGetUniformLocation(programHandle, name)
    }

    override fun Clone(): Comp_Shader {
        return Comp_Shader(VtxShaderStr, FrgShaderStr)
    }
}