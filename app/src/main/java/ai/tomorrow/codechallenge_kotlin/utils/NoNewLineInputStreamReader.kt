package ai.tomorrow.codechallenge_kotlin.utils

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder

class NoNewLineInputStreamReader : InputStreamReader {


    constructor(`in`: InputStream) : super(`in`) {}

    @Throws(UnsupportedEncodingException::class)
    constructor(`in`: InputStream, charsetName: String) : super(`in`, charsetName) {
    }

    constructor(`in`: InputStream, cs: Charset) : super(`in`, cs) {}

    constructor(`in`: InputStream, dec: CharsetDecoder) : super(`in`, dec) {}

    @Throws(IOException::class)
    override fun read(cbuf: CharArray, offset: Int, length: Int): Int {
        var length = length
        var n = 0
        var c: Int
        do {
            c = this.read()
            if (c != -1) {
                cbuf[offset + n] = c.toChar()
                n++
                length--
            } else {
                return c
            }
        } while (c != -1 && length > 0)
        return n
    }


    @Throws(IOException::class)
    override fun read(): Int {
        var c: Int
        do {
            c = super.read()
        } while (c != -1 && (c == '\n'.toInt() || c == '\r'.toInt()))
        return c
    }
}