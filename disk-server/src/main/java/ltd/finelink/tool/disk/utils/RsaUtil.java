package ltd.finelink.tool.disk.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsaUtil {
    /**
     * 私钥加密
     *
     * @param data 内容
     * @param privatekey 私钥
     * @return 密文
     * @throws Exception
     */
    public byte[] encryptByPrivateKey(byte[] data, Key privatekey) throws Exception {
        Cipher cipher = Cipher.getInstance(privatekey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privatekey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     *
     * @param data 内容
     * @param publickey 公钥
     * @return 密文
     * @throws Exception
     */
    public byte[] encryptByPublicKey(byte[] data, Key publickey) throws Exception {
        //对数据解密
        Cipher cipher = Cipher.getInstance(publickey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publickey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data 密文
     * @param privatekey 私钥
     * @return 明文
     * @throws Exception
     */
    public byte[] decryptByPrivateKey(byte[] data, Key privatekey) throws Exception {
        //对数据解密
        Cipher cipher = Cipher.getInstance(privatekey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privatekey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥解密
     *
     * @param data 密文
     * @param publickey 公钥
     * @return 明文
     * @throws Exception
     */
    public byte[] decryptByPublicKey(byte[] data, Key publickey) throws Exception {
        //对数据解密
        Cipher cipher = Cipher.getInstance(publickey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publickey);
        return cipher.doFinal(data);
    }

    /**
     * 从密钥文件中读取公钥
     *
     * @param kstorefile 密钥文件
     * @param kstoretype 密钥文件类型，例如：JKS
     * @param kstorepwd 密钥文件访问密码
     * @param alias 别名
     * @return 公钥
     */
    public PublicKey getPublicKey(String kstorefile, String kstoretype, String kstorepwd, String alias) {


        try {
            KeyStore ks;
            try (FileInputStream in = new FileInputStream(kstorefile)) {
                ks = KeyStore.getInstance(kstoretype);
                ks.load(in, kstorepwd.toCharArray());
            }
            if (!ks.containsAlias(alias)) { 
                return null;
            }
            Certificate cert = ks.getCertificate(alias);
            return cert.getPublicKey();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException ex) {
            log.error("getPublicKey failure.", ex);
            return null;
        } catch (FileNotFoundException ex) {
            log.error("getPublicKey failure.", ex);
            return null;
        } catch (IOException ex) {
            log.error("getPublicKey failure.", ex);
            return null;
        }
    }


    /**
     * 从密钥文件中读取私钥
     *
     * @param kstorefile 密钥文件
     * @param kstoretype 密钥文件类型，例如：JKS
     * @param kstorepwd 密钥文件访问密码
     * @param alias 别名
     * @return 私钥
     */
    public PrivateKey getPrivateKey(String kstorefile, String kstoretype, String kstorepwd, String alias, String keypwd) {
        try {
            KeyStore ks;
            try (FileInputStream in = new FileInputStream(kstorefile)) {
                ks = KeyStore.getInstance(kstoretype);
                ks.load(in, kstorepwd.toCharArray());
            }
            if (!ks.containsAlias(alias)) {
                log.warn("No such alias in the keystore.");
                return null;
            }
            return (PrivateKey) ks.getKey(alias, keypwd.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException ex) {
            log.error("getPrivateKey failure.", ex);
            return null;
        } catch (FileNotFoundException ex) {
            log.error("getPrivateKey failure.", ex);
            return null;
        } catch (IOException ex) {
            log.error("getPrivateKey failure.", ex);
            return null;
        }
    }


    public String getCert(String kstorefile, String kstoretype, String kstorepwd, String alias) {
        try {
            KeyStore ks;
            try (FileInputStream in = new FileInputStream(kstorefile)) {
                ks = KeyStore.getInstance(kstoretype);
                ks.load(in, kstorepwd.toCharArray());
            }
            if (!ks.containsAlias(alias)) { 
                return null;
            }
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);


            return Base64.encodeBase64String(cert.getEncoded());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException ex) {
            log.error("getPublicKey failure.", ex);
            return null;
        } catch (FileNotFoundException ex) {
            log.error("getPublicKey failure.", ex);
            return null;
        } catch (IOException ex) {
            log.error("getPublicKey failure.", ex);
            return null;
        }
    }


    public PrivateKey getPrivatekey(String DERfile) {
        PrivateKey privateKey = null;
        InputStream in = null;
        try {
            
            byte[] key = new byte[2048];
            in = new FileInputStream(DERfile);
            in.read(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            log.error("私钥证书文件格式错误",ex);
        } catch (IOException ex) {
            log.error(ex.getMessage(),ex);
        }
        return privateKey;
    }


    public PublicKey getPublickey(String CRTfile) {
        try {
            CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
            FileInputStream bais = new FileInputStream(CRTfile);
            X509Certificate Cert = (X509Certificate) certificatefactory.generateCertificate(bais);
            return Cert.getPublicKey();
        } catch (CertificateException | FileNotFoundException ex) {
        }
        return null;
    }

    /**
     * 从字符串获取私钥
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public PrivateKey loadPrivateKey(String privateKeyStr) throws Exception{
        try {
            byte[] buffer=  Base64.decodeBase64(privateKeyStr);
            PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");
            PrivateKey privateKey =  keyFactory.generatePrivate(keySpec);
            return  privateKey;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法",e);
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法",e);
        }  catch (NullPointerException e) {
            throw new Exception("私钥数据为空",e);
        }
    }

    /**
     * 从字符串获取公钥
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public PublicKey loadPublicKey(String publicKeyStr) throws Exception{
        try {
            byte[] buffer= Base64.decodeBase64(publicKeyStr);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);
            PublicKey publicKey =  keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法",e);
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法",e);
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空",e);
        }
    }

}
