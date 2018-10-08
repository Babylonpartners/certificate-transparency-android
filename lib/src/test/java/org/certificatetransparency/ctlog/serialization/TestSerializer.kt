package org.certificatetransparency.ctlog.serialization

import com.google.common.io.Files
import com.google.protobuf.ByteString
import org.apache.commons.codec.binary.Base64
import org.certificatetransparency.ctlog.TestData
import org.certificatetransparency.ctlog.proto.Ct
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/** Test serialization.  */
@RunWith(JUnit4::class)
class TestSerializer {

    @Test
    @Throws(IOException::class)
    fun serializeSCT() {
        val builder = Ct.SignedCertificateTimestamp.newBuilder()
        builder.version = Ct.Version.V1
        builder.timestamp = 1365181456089L

        val keyIdBase64 = "3xwuwRUAlFJHqWFoMl3cXHlZ6PfG04j8AC4LvT9012Q="
        builder.id = Ct.LogID.newBuilder()
            .setKeyId(ByteString.copyFrom(Base64.decodeBase64(keyIdBase64)))
            .build()

        val signatureBase64 = "MEUCIGBuEK5cLVobCu1J3Ek39I3nGk6XhOnCCN+/6e9TbPfyAiEAvrKcctfQbWHQa9s4oGlGmqhv4S4Yu3zEVomiwBh+9aU="

        val signatureBuilder = Ct.DigitallySigned.newBuilder()
        signatureBuilder.hashAlgorithm = Ct.DigitallySigned.HashAlgorithm.SHA256
        signatureBuilder.sigAlgorithm = Ct.DigitallySigned.SignatureAlgorithm.ECDSA
        signatureBuilder.signature = ByteString.copyFrom(Base64.decodeBase64(signatureBase64))

        builder.signature = signatureBuilder.build()

        val generatedBytes = Serializer.serializeSctToBinary(builder.build())
        val readBytes = Files.toByteArray(TestData.file(TEST_CERT_SCT))
        assertArrayEquals(readBytes, generatedBytes)
    }

    companion object {
        const val TEST_CERT_SCT = "/testdata/test-cert.proof"
    }
}
