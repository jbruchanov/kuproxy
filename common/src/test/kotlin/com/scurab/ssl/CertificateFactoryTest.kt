package com.scurab.ssl

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import sun.misc.BASE64Encoder
import sun.security.provider.X509Factory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate

internal class CertificateFactoryTest {

    @Test
    fun testCreateCA() {
        // create certificates
        val rootCert = CertificateFactory.createCACertificate("TestRootCA")

        // save it
        val keyStoreCa = KeyStore.getInstance(PKCS)
        keyStoreCa.load(null, EMPTY_PWD)
        keyStoreCa.setKeyEntry(
            CERT_ALIAS,
            rootCert.privateKey,
            EMPTY_PWD,
            arrayOf(rootCert.certificate)
        )

        val cert = ByteArrayOutputStream()
            .also { store -> keyStoreCa.store(store, EMPTY_PWD) }
            .toByteArray()

        val keyStoreLoaded = KeyStore.getInstance(PKCS)
        keyStoreLoaded.load(ByteArrayInputStream(cert), EMPTY_PWD)

        // load file and compare it
        val privateKey = keyStoreLoaded.getKey(CERT_ALIAS, EMPTY_PWD)
        val certificate = keyStoreCa.getCertificate(CERT_ALIAS)
        val loadedRootCa = CertificateFactory.Cert(privateKey as PrivateKey, certificate as X509Certificate)

        assertArrayEquals(rootCert.privateKey.encoded, loadedRootCa.privateKey.encoded)
        assertArrayEquals(rootCert.certificate.encoded, loadedRootCa.certificate.encoded)
    }

    @Test
    fun testLeafCA() {
        // create certificates
        val rootCert = CertificateFactory.createCACertificate("TestRootCA")
        val domainCert = CertificateFactory.createLeafCertificate(
            "LeafCert",
            listOf("www.test.com", "*sample.com"),
            rootCert
        )

        // save them
        val keyStoreCa = KeyStore.getInstance(PKCS)
        keyStoreCa.load(null, EMPTY_PWD)
        keyStoreCa.setKeyEntry(
            CERT_ALIAS,
            domainCert.privateKey,
            EMPTY_PWD,
            arrayOf(domainCert.certificate)
        )

        val cert = ByteArrayOutputStream()
            .also { store -> keyStoreCa.store(store, EMPTY_PWD) }
            .toByteArray()

        assertTrue(cert.isNotEmpty())
    }

    /**
     * Simple example how to create a certificate for windows/android
     * Android needs to have a single cert per file, otherwise won't be part of CAs even if it has CA cert
     */
    @Test
    fun testSimpleCer() {
        val rootCert = CertificateFactory.createCACertificate("TestRootCA")
        val cert = ByteArrayOutputStream()
            .also {
                it.write((X509Factory.BEGIN_CERT + "\n").toByteArray())
                BASE64Encoder().encodeBuffer(rootCert.certificate.encoded, it)
                it.write((X509Factory.END_CERT + "\n").toByteArray())
            }
            .toString()

        assertTrue(cert.isNotEmpty())
        println(cert)
    }

    companion object {
        private val EMPTY_PWD = CharArray(0)
        private const val CERT_ALIAS = "TestAlias"
        private const val PKCS = "PKCS12"
    }
}
