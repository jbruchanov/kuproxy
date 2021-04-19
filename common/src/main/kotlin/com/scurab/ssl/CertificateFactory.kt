package com.scurab.ssl

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.GeneralName
import org.bouncycastle.asn1.x509.GeneralNames
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

/**
 * Certificate factory to create CA or any leaf certificate signed by this CA.
 */
object CertificateFactory {

    class Cert(val privateKey: PrivateKey, val certificate: X509Certificate)

    /**
     * Create CA Certificate
     */
    fun createCACertificate(commonName: String) = createCertificate(commonName, null, null, true)

    /**
     * Create Leaf certificate
     *
     * @param commonName
     * @param domains - list of domains to be the certificate for (www.test.com or *.test.com)
     * @param caCert - CA certficate
     */
    fun createLeafCertificate(commonName: String, domains: List<String>, caCert: Cert) =
        createCertificate(commonName, domains, caCert, false)

    private fun createCertificate(
        commonName: String,
        domains: List<String>?,
        issuer: Cert?,
        isCA: Boolean
    ): Cert {
        // Generate the key-pair with the official Java API's
        val keyGen = KeyPairGenerator.getInstance("RSA")
        val certKeyPair = keyGen.generateKeyPair()
        val name = X500Name("CN=$commonName")

        val serialNumber = BigInteger.valueOf(System.currentTimeMillis())
        val validFrom = Instant.now()
        val validUntil = validFrom.plus(50L * 365L, ChronoUnit.DAYS)

        // If there is no issuer, we self-sign our certificate.
        val issuerName = X500Name(issuer?.certificate?.subjectDN?.name ?: "CN=$commonName")
        val issuerKey: PrivateKey = issuer?.privateKey ?: certKeyPair.private

        // The cert builder to build up our certificate information
        val builder = JcaX509v3CertificateBuilder(
            issuerName,
            serialNumber,
            Date.from(validFrom),
            Date.from(validUntil),
            name,
            certKeyPair.public
        )

        // Make the cert to a Cert Authority to sign more certs when needed
        if (isCA) {
            builder.addExtension(Extension.basicConstraints, true, BasicConstraints(isCA))
        }

        // Modern browsers demand the DNS name entry
        domains?.let { domain ->
            builder.addExtension(
                Extension.subjectAlternativeName, false,
                GeneralNames(domain.map { GeneralName(GeneralName.dNSName, it) }.toTypedArray())
            )
        }

        // Finally, sign the certificate:
        val signer = JcaContentSignerBuilder("SHA256WithRSA").build(issuerKey)
        val certHolder = builder.build(signer)
        val cert = JcaX509CertificateConverter().getCertificate(certHolder)
        return Cert(certKeyPair.private, cert)
    }
}
