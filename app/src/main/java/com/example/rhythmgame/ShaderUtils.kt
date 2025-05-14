// 파일명: ShaderUtils.kt
package com.example.rhythmgame   // 실제 패키지 경로에 맞춰 수정하세요

import android.opengl.GLES20.*

object ShaderUtils {

    /**
     * 셰이더를 생성하고 컴파일한 뒤 핸들을 반환합니다.
     * @param type      GL_VERTEX_SHADER 또는 GL_FRAGMENT_SHADER
     * @param shaderCode 셰이더 소스 코드 문자열
     * @return 컴파일된 셰이더 핸들 (0 이하면 오류)
     * @throws RuntimeException 컴파일 에러 발생 시
     */
    fun loadShader(type: Int, shaderCode: String): Int {
        // 셰이더 객체 생성
        val shader = glCreateShader(type)
        if (shader == 0) {
            throw RuntimeException("Error creating shader of type $type")
        }

        // 소스 설정 및 컴파일
        glShaderSource(shader, shaderCode)
        glCompileShader(shader)

        // 컴파일 상태 체크
        val compileStatus = IntArray(1)
        glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            // 컴파일 실패 시 로그를 읽고 셰이더 삭제
            val errorLog = glGetShaderInfoLog(shader)
            glDeleteShader(shader)
            throw RuntimeException("Shader compile failed: $errorLog")
        }

        return shader
    }

    /**
     * 셰이더 프로그램을 생성하여 셰이더들을 링크한 뒤 프로그램 핸들을 반환합니다.
     * @param vertexShaderHandle   정점 셰이더 핸들
     * @param fragmentShaderHandle 프래그먼트 셰이더 핸들
     * @return 링크된 프로그램 핸들
     * @throws RuntimeException 링크 에러 발생 시
     */
    fun createAndLinkProgram(vertexShaderHandle: Int, fragmentShaderHandle: Int): Int {
        val program = glCreateProgram()
        if (program == 0) {
            throw RuntimeException("Error creating shader program")
        }

        glAttachShader(program, vertexShaderHandle)
        glAttachShader(program, fragmentShaderHandle)
        glLinkProgram(program)

        val linkStatus = IntArray(1)
        glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val errorLog = glGetProgramInfoLog(program)
            glDeleteProgram(program)
            throw RuntimeException("Program link failed: $errorLog")
        }

        return program
    }
}
