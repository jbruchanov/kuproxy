package com.scurab.ssl

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import sun.misc.BASE64Encoder
import sun.security.provider.X509Factory
import test.SslHelper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate

internal class CertificateFactoryTest {

    @Test
    fun testCreateCA() {
        // create certificates
        val rootCert = CertificateFactory.createCACertificate("TestRootCA")

        // save it
        val keyStoreCa = KeyStore.getInstance(SslHelper.KeystoreType)
        keyStoreCa.load(null, SslHelper.Password)
        keyStoreCa.setKeyEntry(
            SslHelper.CAAlias,
            rootCert.privateKey,
            SslHelper.Password,
            arrayOf(rootCert.certificate)
        )

        val cert = ByteArrayOutputStream()
            .also { store -> keyStoreCa.store(store, SslHelper.Password) }
            .toByteArray()

        val keyStoreLoaded = KeyStore.getInstance(SslHelper.KeystoreType)
        keyStoreLoaded.load(ByteArrayInputStream(cert), SslHelper.Password)

        // load file and compare it
        val privateKey = keyStoreLoaded.getKey(SslHelper.CAAlias, SslHelper.Password)
        val certificate = keyStoreCa.getCertificate(SslHelper.CAAlias)
        val loadedRootCa = CertificateFactory.Cert(privateKey as PrivateKey, certificate as X509Certificate)

        assertArrayEquals(rootCert.privateKey.encoded, loadedRootCa.privateKey.encoded)
        assertArrayEquals(rootCert.certificate.encoded, loadedRootCa.certificate.encoded)
    }

    @Test
    fun testLeafCA() {
        // create certificates
        val rootCert = CertificateFactory.createCACertificate("_KuProxyCA")
        val domainCert: CertificateFactory.Cert = CertificateFactory.createLeafCertificate("zunpa.cz", listOf("zunpa.cz", "*.zunpa.cz", "*.xvideos.com", "cdr.cz"), rootCert)

        // save them
        val keyStoreCa = KeyStore.getInstance(SslHelper.KeystoreType)
        keyStoreCa.load(null, SslHelper.Password)
        keyStoreCa.setKeyEntry(
            SslHelper.ServerAlias,
            domainCert.privateKey,
            SslHelper.Password,
            arrayOf(domainCert.certificate, rootCert.certificate)
        )

        val cert = ByteArrayOutputStream()
            .also { store -> keyStoreCa.store(store, SslHelper.Password) }
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
    }

    /**
     * Create new CA cert for kuproxy
     */
    @Test
    @Disabled
    fun createNewCACertificate() {
        val rootCert = CertificateFactory.createCACertificate("_KuProxyCA")

        // save it
        val keyStoreCa = KeyStore.getInstance(SslHelper.KeystoreType)
        keyStoreCa.load(null, SslHelper.Password)
        keyStoreCa.setKeyEntry(
            SslHelper.CAAlias,
            rootCert.privateKey,
            SslHelper.Password,
            arrayOf(rootCert.certificate)
        )

        val file = File("KuProxyCA.p12")
        println(file.absolutePath)
        file.outputStream().use {
            keyStoreCa.store(it, SslHelper.Password)
        }

        SslHelper.saveCer(File("KuProxyCA.cer"), rootCert.certificate)
    }

    /**
     * Create and export server certificate signed by embedded CA
     */
    @Test
    @Disabled
    fun createServerCert() {
        val domains = listOf("localhost", "zunpa.cz", "scurab.com", "*.scurab.com", "cdr.cz", "*.cdr.cz")
        val serverCert =
            SslHelper.createServerCertSignedByCA(CertificateFactory.embeddedCACertificate, domains)

        SslHelper.saveCer(File("server.cer"), serverCert.getCertificate(SslHelper.ServerAlias))
    }

    /**
     * Just export embedded CA certificate into cer file for easy import into OS, android APP
     */
    @Test
    @Disabled
    fun exportCACert() {
        SslHelper.saveCer(File("KuProxyCA.cer"), CertificateFactory.embeddedCACertificate.certificate)
    }
}
