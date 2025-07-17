package com.group1.project.swp_project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group1.project.swp_project.utils.VnPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnpayService {

    @Value("${vnpay.url}")
    private String vnp_PayUrl;
    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;
    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;
    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;
    @Value("${vnpay.version}")
    private String vnp_Version;

    public String createPaymentUrl(HttpServletRequest request, long amount, String orderInfo) {
        String vnp_TxnRef = VnPayUtils.getRandomNumber(8);
        String vnp_IpAddr = VnPayUtils.getIpAddress(request);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayUtils.hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnp_PayUrl + "?" + queryUrl;
    }
    public String refundPayment(String vnp_TxnRef, long amount, String userEmail) {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("vnp_RequestId", VnPayUtils.getRandomNumber(8));
            body.put("vnp_Version", vnp_Version);
            body.put("vnp_Command", "refund");
            body.put("vnp_TmnCode", vnp_TmnCode);
            body.put("vnp_TransactionType", "02");
            body.put("vnp_TxnRef", vnp_TxnRef);
            body.put("vnp_Amount", String.valueOf(amount * 100));
            body.put("vnp_OrderInfo", "Hoàn tiền cho đơn #" + vnp_TxnRef);
            body.put("vnp_TransactionNo", "");
            body.put("vnp_CreateBy", userEmail);
            body.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

            List<String> sortedKeys = new ArrayList<>(body.keySet());
            Collections.sort(sortedKeys);
            StringBuilder dataToHash = new StringBuilder();
            for (int i = 0; i < sortedKeys.size(); i++) {
                String k = sortedKeys.get(i);
                String v = body.get(k);
                dataToHash.append(k).append("=").append(URLEncoder.encode(v, StandardCharsets.UTF_8));
                if (i < sortedKeys.size() - 1) dataToHash.append("&");
            }
            String secureHash = VnPayUtils.hmacSHA512(vnp_HashSecret, dataToHash.toString());
            body.put("vnp_SecureHash", secureHash);


            String apiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction"; // hoặc production URL
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Gửi yêu cầu hoàn tiền thất bại", e);
        }
    }
}