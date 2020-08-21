/*
 * Copyright 2019 Babylon Partners Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.babylon.certificatetransparency.internal.logclient.model

internal data class DigitallySigned(
    val hashAlgorithm: HashAlgorithm = HashAlgorithm.NONE,
    val signatureAlgorithm: SignatureAlgorithm = SignatureAlgorithm.ANONYMOUS,
    val signature: ByteArray
) {
    // Numbers part of specification
    @Suppress("MagicNumber", "unused")
    enum class HashAlgorithm(val number: Int) {
        NONE(0),
        MD5(1),
        SHA1(2),
        SHA224(3),
        SHA256(4),
        SHA384(5),
        SHA512(6);

        companion object {
            fun forNumber(number: Int) = values().firstOrNull { it.number == number }
        }
    }

    // Numbers part of specification
    @Suppress("MagicNumber", "unused")
    enum class SignatureAlgorithm(val number: Int) {
        ANONYMOUS(0),
        RSA(1),
        DSA(2),
        ECDSA(3);

        companion object {
            fun forNumber(number: Int) = values().firstOrNull { it.number == number }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DigitallySigned

        if (hashAlgorithm != other.hashAlgorithm) return false
        if (signatureAlgorithm != other.signatureAlgorithm) return false
        if (!signature.contentEquals(other.signature)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hashAlgorithm.hashCode()
        result = 31 * result + signatureAlgorithm.hashCode()
        result = 31 * result + signature.contentHashCode()
        return result
    }
}
