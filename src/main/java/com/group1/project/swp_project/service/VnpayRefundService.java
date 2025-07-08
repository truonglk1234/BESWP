package com.group1.project.swp_project.service;

import com.group1.project.swp_project.config.VnPayConfig;
import com.group1.project.swp_project.entity.ExaminationPayment;
import com.group1.project.swp_project.utils.VnPayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VnpayRefundService {

    private final VnPayConfig vnPayConfig;

    public boolean refund(ExaminationPayment payment) {
        try {
            // Tạo dữ liệu gửi đến VNPAY
            Map<String, String> params = new LinkedHashMap<>();
            params.put("vnp_Version", "2.1.0");
            params.put("vnp_Command", "refund");
            params.put("vnp_TmnCode", vnPayConfig.vnp_TmnCode);
            params.put("vnp_TxnRef", payment.getTxnRef());
            params.put("vnp_Amount", String.valueOf(payment.getAmount() * 100)); // nhân 100 theo yêu cầu của VNPAY
            params.put("vnp_OrderInfo", "Hoàn tiền giao dịch " + payment.getTxnRef());
            params.put("vnp_TransDate", payment.getExaminationBooking().getAppointmentDate()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            params.put("vnp_CreateBy", "admin");
            params.put("vnp_RequestId", String.valueOf(System.currentTimeMillis()));
            params.put("vnp_IpAddr", "127.0.0.1");

            String hash = VnPayUtils.hashAllFields(params, vnPayConfig.vnp_HashSecret);
            params.put("vnp_SecureHash", hash);

            String url = UriComponentsBuilder.fromHttpUrl(vnPayConfig.vnp_Url)
                    .queryParams(VnPayUtils.toMultiValueMap(params)).toUriString();

            // Gửi request đến VNPAY
            String response = VnPayUtils.sendHttpGet(url);
            log.info("Refund response: {}", response);

            return response.contains("\"vnp_ResponseCode\":\"00\"");
        } catch (Exception e) {
            log.error("Lỗi hoàn tiền VNPAY", e);
            return false;
        }
    }
}
