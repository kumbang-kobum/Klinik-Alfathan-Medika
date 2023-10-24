package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.HeadlessException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class BridgingWA {
    private static final Properties prop = new Properties();
    private sekuel Sequel=new sekuel();
    private String Key,pass , url , token ,requestJson, urlApi = "" , sender = "" , number ="" , message = "" , reurn = "" , device_id = "" , group_pendaftaran = "" , group_keuangan= "";
    private HttpHeaders headers ;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode nameNode;
    private JsonNode response;


    public String getHmac() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(pass.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            Key=sb.toString();
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
        }
	return Key;
    }


    public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        javax.net.ssl.TrustManager[] trustManagers= {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {return null;}
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
            }
        };
        sslContext.init(null,trustManagers , new SecureRandom());
        SSLSocketFactory sslFactory=new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme scheme=new Scheme("https",443,sslFactory);
        HttpComponentsClientHttpRequestFactory factory=new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        return new RestTemplate(factory);
    }

//    public String sendWaBatal(String nm_dokter, String no_rkm_medis, String nm_pasien){
//        try {
//            message = "Telah dilakukan pembatalan berobat \na/n *_"+nm_pasien+"_* dengan No.RM "+no_rkm_medis+", "
//                    + "yang berobat ke "+nm_dokter+"  \n \n Petugas yang membatalkan: "+akses.getkode();
//            number = "62895355284030";
//            token = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'token'");
//            urlApi = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'server'");
//            sender = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'phonenumber'");
//            url = urlApi+"/send-message";
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
//            map.add("type", "text");
//            map.add("sender", sender);
//            map.add("number", number);
//            map.add("api_key", token);
//            map.add("message", message);
//
//            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
//
//            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
//            root = mapper.readTree(response.getBody());
//            System.out.println(root);
//            if(root.path("status").asText().equals("true")){
//                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
//                reurn = root.path("msg").asText();
//            }else {
//                JOptionPane.showMessageDialog(null,root.path("msg").asText());
//            }
//        } catch (Exception ex) {
//            System.out.println("Notifikasi : "+ex);
//            System.out.println(url);
//            if(ex.toString().contains("UnknownHostException")){
//                JOptionPane.showMessageDialog(null,"Koneksi ke server mLITE.id terputus...!");
//            }
//        }
//        return token;
//    }

    public String sendWaHapusPengeluaran(String tgl, String kategori, String petugas, String pengeluaran, String keterangan){
        try {
            message = "*WARNING #hapuspengeluaran*,\n\n*telah dilakukan penghapusan data yang di entry*\nTgl: *_"+tgl+"_*\nKategori: "+kategori+"\n"
                    + "Petugas entry: "+petugas+"\n"
                    + "Pemasukan: Rp "+pengeluaran+"\n"
                    + "Keterangan: "+keterangan+"\n\n"
                    + "Petugas yang menghapus: "+akses.getkode();
//            group = "Recepcionist part II";
            group_keuangan = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'group_keuangan'");
            device_id = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'device_id_keuangan'");
            urlApi = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'urlapi'");
            url = urlApi+"/api/sendGroup";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("type", "text");
            map.add("group", group_keuangan);
            map.add("device_id", device_id);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server mLITE.id terputus...!");
            }
        }
        return token;
    }

    public String sendWaHapusPemasukan(String tgl, String kategori, String petugas, String pemasukan, String dari, String keperluan){
        try {
            message = "*WARNING #hapuspemasukan*,\n\n*telah dilakukan penghapusan data yang di entry*\nTgl: *_"+tgl+"_*\nKategori: "+kategori+"\n"
                    + "Petugas entry: "+petugas+"\n"
                    + "Pemasukan: Rp "+pemasukan+"\n"
                    + "Terima dari: "+dari+"\n"
                    + "Keperluan: "+keperluan+"\n\n"
                    + "Petugas yang menghapus: "+akses.getkode();
//            group = "Recepcionist part II";
            group_keuangan = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'group_keuangan'");
            device_id = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'device_id_keuangan'");
            urlApi = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'urlapi'");
            url = urlApi+"/api/sendGroup";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("type", "text");
            map.add("group", group_keuangan);
            map.add("device_id", device_id);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server mLITE.id terputus...!");
            }
        }
        return token;
    }

    public String sendWaBatal(String nm_dokter, String no_rkm_medis, String nm_pasien, String poliklinik, String no_rawat, String tgl_berobat){
        try {
            message = "*_Gunakan dengan bijak menu pembatalan pasien,_*\n*_karena dapat menyebabkan biaya registrasi menjadi Rp.0_*\n \nTelah dilakukan pembatalan berobat \n"
                    + "no.Rawat: *_"+no_rawat+"_*\n"
                    + "a/n: *_"+nm_pasien+"_*\n"
                    + "No.RM: *_"+no_rkm_medis+"_*\n"
                    + "tgl. brobat: *_"+tgl_berobat+"_*\n"
                    + "Ke: *_"+poliklinik+"_*\n"
                    + "dokter: "+nm_dokter+"  \n \n Petugas yang membatalkan: "+akses.getkode();
//            group = "Recepcionist part II";
            group_pendaftaran = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'group'");
            device_id = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'device_id'");
            urlApi = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'urlapi'");
            url = urlApi+"/api/sendGroup";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("type", "text");
            map.add("group", group_pendaftaran);
            map.add("device_id", device_id);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server mLITE.id terputus...!");
            }
        }
        return token;
    }

    public String sendWa(String no_rkm_medis, String nama, String tanggal, String poli, String tanggal_booking, String jam_booking, String no_reg){
        try {
            message = "Mengingatkan kembali, kepada saudara/i 📢\n👔 Nama: *_"+nama+"_*\n🗃️ No.RM: "+no_rkm_medis+"\n"
                    + "📆 Tgl booking:"+tanggal_booking+" "+jam_booking+"\n🚪 poliklinik: *"+poli+"*\n📆 Tgl periksa: "+tanggal+"\n🎯 No antrian: *"+no_reg+"*.\n\n"
                    + "Untuk konfirmasi kehadiran, cukup membalas pesan ini dengan iya/tidak.\n\n\nCustomer Service "+akses.getnamars();
            number = Sequel.cariIsi("SELECT no_tlp FROM pasien WHERE no_rkm_medis = "+no_rkm_medis);
            device_id = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'device_id'");
            urlApi = Sequel.cariIsi("SELECT value FROM mlite_settings WHERE module='wagateway' AND field = 'urlapi'");
            url = urlApi+"/api/send";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("type", "text");
            map.add("number", number);
            map.add("device_id", device_id);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server mLITE.id terputus...!");
            }
        }
        return token;
    }

    public String sendWaUTD(String no_rkm_medis, String nama, String tanggal){
        try {
            message = "Mengingatkan kepada saudara "+nama+", untuk jadwal donor darah pada tanggal "+tanggal+". Terima kasih. Customer Service "+akses.getnamars();
            number = Sequel.cariIsi("SELECT no_tlp FROM pasien WHERE no_rkm_medis = "+no_rkm_medis);
            token = "";
            urlApi = "http://192.168.0.2:10000";
            sender = "628115167345";
            url = urlApi+"/wagateway/kirimpesan";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("type", "text");
            map.add("sender", sender);
            map.add("number", number);
            map.add("api_key", token);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server mLITE.id terputus...!");
            }
        }
        return token;
    }

}
