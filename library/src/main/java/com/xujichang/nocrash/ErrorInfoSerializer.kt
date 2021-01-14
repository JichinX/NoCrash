package com.xujichang.nocrash

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

object ErrorInfoSerializer : Serializer<ErrorInfo> {
    override val defaultValue: ErrorInfo = ErrorInfo.getDefaultInstance()

    override fun readFrom(input: InputStream): ErrorInfo = ErrorInfo.parseFrom(input)

    override fun writeTo(t: ErrorInfo, output: OutputStream) = t.writeTo(output)
}