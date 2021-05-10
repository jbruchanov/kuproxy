package test

import com.scurab.ssl.CertificateFactory
import java.io.File
import java.security.KeyStore
import java.security.cert.Certificate
import java.util.Base64

object SslHelper {
    const val KeystoreType = CertificateFactory.PKCS
    const val RootCN = "_KuProxyCA"
    const val ServerCN = "KuProxyServer"
    const val CAAlias = "KuProxyCA"
    const val ServerAlias = ServerCN
    val Password = CertificateFactory.EMPTY_PWD
    const val BEGIN_CERT = "-----BEGIN CERTIFICATE-----"
    const val END_CERT = "-----END CERTIFICATE-----"

    fun createServerCertSignedByCA(
        rootCert: CertificateFactory.Cert? = null,
        domains: List<String> = emptyList()
    ): KeyStore {
        val certCA = rootCert ?: CertificateFactory.createCACertificate(RootCN)
        val domainCert = CertificateFactory.createLeafCertificate(ServerCN, domains, certCA)

        val keyStoreCa = KeyStore.getInstance(KeystoreType)
        keyStoreCa.load(null, Password)
        keyStoreCa.setKeyEntry(
            ServerAlias,
            domainCert.privateKey,
            Password,
            arrayOf(domainCert.certificate, certCA.certificate)
        )
        return keyStoreCa
    }

    fun saveCer(file: File, cert: Certificate) {
        file.outputStream().use {
            it.write((BEGIN_CERT + "\n").toByteArray())
            it.write(Base64.getEncoder().encode(cert.encoded))
            it.write((END_CERT + "\n").toByteArray())
        }
    }
}
