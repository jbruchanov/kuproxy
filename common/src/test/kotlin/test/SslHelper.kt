package test

import com.scurab.ssl.CertificateFactory
import sun.misc.BASE64Encoder
import sun.security.provider.X509Factory
import java.io.File
import java.security.KeyStore
import java.security.cert.Certificate

object SslHelper {
    const val KeystoreType = CertificateFactory.PKCS
    const val RootCN = "_KuProxyCA"
    const val ServerCN = "KuProxyServer"
    const val CAAlias = "KuProxyCA"
    const val ServerAlias = ServerCN
    val Password = CertificateFactory.EMPTY_PWD

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
            it.write((X509Factory.BEGIN_CERT + "\n").toByteArray())
            BASE64Encoder().encodeBuffer(cert.encoded, it)
            it.write((X509Factory.END_CERT + "\n").toByteArray())
        }
    }
}
