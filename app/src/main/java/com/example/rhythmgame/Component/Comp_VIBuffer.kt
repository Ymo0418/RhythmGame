package com.example.rhythmgame.Component

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class Comp_VIBuffer : Component() {
    protected val vertexData = floatArrayOf(
        -1f, 1f, 0f,
        -1f, -1f, 0f,
        1f, 1f, 0f,
        1f, -1f, 0f
    )
    protected val texCoordData = floatArrayOf(
        0f, 0f,
        0f, 0.9999f,
        1f, 0f,
        1f, 0.9999f
    )
    public lateinit var vertexBuffer: FloatBuffer
    public lateinit var texCoordBuffer: FloatBuffer

    init {
        //ByteBuffer.allocateDirect()   - JavaVM을 거치지 않고 직접 메모리에 접근가능한 메모리영역에 할당
        //                              - 그래픽스API에서 데이터 사용시 성능향상을 기대할 수 있음
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4) //float크기 4개만큼 할당
            .order(ByteOrder.nativeOrder()) //메모리 저장방식, 현재 CPU가 쓰는 순서로
            .asFloatBuffer()                //Float타입 단위로 데이터 읽/쓰기
            .apply {
                put(vertexData)             //Data의 0인덱스부터 복사해서 값 채움
                position(0)
            }
        texCoordBuffer = ByteBuffer.allocateDirect(texCoordData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(texCoordData)
                position(0)
            }
    }

    override fun Clone(): Comp_VIBuffer {
        return Comp_VIBuffer()
    }
}