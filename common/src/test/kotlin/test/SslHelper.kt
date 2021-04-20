package test

import com.scurab.ssl.CertificateFactory
import java.security.KeyStore

object SslHelper {
    const val KeystoreType = "PKCS12"
    const val CAAlias = "TestCAAlias"
    const val ServerAlias = "TestServerAlias"
    val Password = CharArray(0)

    fun createServerCertSignedByCA(): KeyStore {
        val rootCert = CertificateFactory.createCACertificate("TestRootCA")
        val domainCert = CertificateFactory.createLeafCertificate("LeafCert", emptyList(), rootCert)

        val keyStoreCa = KeyStore.getInstance(KeystoreType)
        keyStoreCa.load(null, Password)
        keyStoreCa.setKeyEntry(
            ServerAlias,
            rootCert.privateKey,
            Password,
            arrayOf(domainCert.certificate)
        )
        return keyStoreCa
    }
}
