package com.qrsimulator.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Random;

@RestController
public class QrSimulatorController {

    
//PAYDIBS SIMULATOR - 4 APIS
    
    @Value("${paydibs.threshold.amount}")
    private float thresholdAmount;

    @Value("${paydibs.decline.amount}")
    private float declineAmount;
    
    @Value("${paydibs.delay.amount}")
    private float delayAmount;
    
    @Value("${paydibs.timeout.amount}")
    private float timeoutAmount;
    
    
    
    @PostMapping("/token/api-token")
    @ResponseBody
    public Map<String, Object> generateAuthToken(@RequestBody final String request) {
        final Map<String, Object> requestData = new HashMap<>();
        final String[] split = request.split("&");
        for (final String param : split) {
            final String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                requestData.put(keyValue[0], keyValue[1]);
            }
        }
        
        final String login_id = (String) requestData.get("login_id");
        final String login_password = (String) requestData.get("login_password");
        final long time = Long.parseLong(requestData.get("time").toString());
        final String sign = (String) requestData.get("sign");
        
        if (login_id == null || login_password == null || sign == null ||
                login_id.isEmpty() || login_password.isEmpty() || sign.isEmpty()) {
            final Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "400");
            errorResponse.put("msg", "Bad Request: Required fields are missing or empty.");
            return errorResponse;
        }
        final Map<String, Object> data = new HashMap<>();
        data.put("status", "1");
        data.put("Auth_Token", UUID.randomUUID().toString());
        final Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("msg", "Success");
        response.put("data", data);
        return response;
    }

    
   
    

        @PostMapping("/payment/create-trade")
        @ResponseBody
        public Map<String, Object> createTradeApi(@RequestBody final String request) {
            final Map<String, Object> requestData = new HashMap<>();
            final String[] split = request.split("&");
            for (final String param : split) {
                final String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    final String key = keyValue[0];
                    final String value = keyValue[1].trim(); // Trim the value to remove whitespace
                    requestData.put(key, value);
                }
            }
            
            
            final long time = Long.parseLong(requestData.get("time").toString());
            final String sign = (String) requestData.get("sign");
            final String integrator_id = (String) requestData.get("integrator_id");
            final String amount = (String) requestData.get("amount");
            final String trx_id = (String) requestData.get("trx_id");
            final String pay_type = (String) requestData.get("pay_type");
            
            if (integrator_id == null || trx_id == null || sign == null || pay_type == null ||
                    amount == null || integrator_id.isEmpty() || trx_id.isEmpty() || sign.isEmpty() ||
                    pay_type.isEmpty() || amount.isEmpty()) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "400");
                errorResponse.put("msg", "Bad Request: Required fields are missing or empty.");
                return errorResponse;
            }
            
            float parsedAmount = Float.parseFloat(amount);
           
            if (parsedAmount == declineAmount) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "204");
                errorResponse.put("msg", "Failed: Decline amount received.");
                return errorResponse;
            }
            
            if (parsedAmount >= thresholdAmount) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "204");
                errorResponse.put("msg", "Failed: Amount exceeds threshold.");
                return errorResponse;
            }
            
            
            
            if (parsedAmount == timeoutAmount) {
                final Map<String, Object> errorresponse = new HashMap<>();
                errorresponse.put("code", "402");
                errorresponse.put("msg", "Timeout : Server timeout.");
                return errorresponse;
            }
            
            
            if (parsedAmount == delayAmount) {
            	
            	try {
                    Thread.sleep(15000); // Delay for 15 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            	
            	final Map<String, Object> data = new HashMap<>();
                data.put("trans_id", generateRandomNumber(24));
                data.put("pay_type", pay_type);
                data.put("amount", amount);
                data.put("integrator_id", integrator_id);
                final Map<String, Object> response = new HashMap<>();
                response.put("code", "200");
                response.put("msg", "Success : Delayed response");
                response.put("data", data);
                return response;
            }
            
            final Map<String, Object> data = new HashMap<>();
            data.put("trans_id", generateRandomNumber(24));
            data.put("pay_type", pay_type);
            data.put("amount", amount);
            data.put("integrator_id", integrator_id);
            final Map<String, Object> response = new HashMap<>();
            response.put("code", "200");
            response.put("msg", "Success");
            response.put("data", data);
            return response;
        }

        private String generateRandomNumber(final int digits) {
            final StringBuilder sb = new StringBuilder();
            final Random random = new Random();
            for (int i = 0; i < digits; ++i) {
                final int digit = random.nextInt(10);
                sb.append(digit);
            }
            return sb.toString();
        }
        
        
        
        
        
        @PostMapping("/payment/generate")
        @ResponseBody
        public Map<String, Object> qrCodeRequestApi(@RequestBody final String request) {
            final Map<String, Object> requestData = new HashMap<>();
            final String[] split = request.split("&");
            for (final String param : split) {
                final String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    requestData.put(keyValue[0], keyValue[1]);
                }
            }
            
            final long time = Long.parseLong(requestData.get("time").toString());
            final String sign = (String) requestData.get("sign");
            final String integrator_id = (String) requestData.get("integrator_id");
            final String amount = (String) requestData.get("amount");
            final String trans_id = (String) requestData.get("trans_id");
            
            if (integrator_id == null || trans_id == null || sign == null || amount == null ||
                    integrator_id.isEmpty() || trans_id.isEmpty() || sign.isEmpty() || amount.isEmpty()) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "400");
                errorResponse.put("msg", "Bad Request: Required fields are missing or empty.");
                return errorResponse;
            }
            
            float parsedAmount = Float.parseFloat(amount);
            
            if (parsedAmount == declineAmount) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "204");
                errorResponse.put("msg", "Failed: Decline amount received.");
                return errorResponse;
            }
            
            if (parsedAmount >= thresholdAmount) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "204");
                errorResponse.put("msg", "Failed: Amount exceeds threshold.");
                return errorResponse;
            }
            
            if (parsedAmount == timeoutAmount) {
                final Map<String, Object> errorresponse = new HashMap<>();
                errorresponse.put("code", "402");
                errorresponse.put("msg", "Timeout : Server timeout.");
                return errorresponse;
            }
            
            if (parsedAmount == delayAmount) {
                try {
                    Thread.sleep(15000); // Delay for 15 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                final Map<String, Object> data = new HashMap<>();
                data.put("qrcode", UUID.randomUUID().toString());
                data.put("trans_id", trans_id.trim()); // Remove leading/trailing whitespace
                data.put("qrcode_format", "dynamic");
                data.put("wait_time", "150");
                final Map<String, Object> response = new HashMap<>();
                response.put("code", "200");
                response.put("msg", "Success : Delayed response");
                response.put("data", data);
                return response;
            }
            
            final Map<String, Object> data = new HashMap<>();
            data.put("qrcode", UUID.randomUUID().toString());
            data.put("trans_id", trans_id.trim()); // Remove leading/trailing whitespace
            data.put("qrcode_format", "dynamic");
            data.put("wait_time", "150");
            final Map<String, Object> response = new HashMap<>();
            response.put("code", "200");
            response.put("msg", "Success");
            response.put("data", data);
            return response;
        }


        
        
                
        @PostMapping("/payment/check-status")
        @ResponseBody
        public Map<String, Object> CheckTransactionStatusApi(@RequestBody String request) {
            Map<String, Object> requestData = new HashMap<>();
            String[] params = request.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    final String key = keyValue[0];
                    final String value = keyValue[1].trim(); // Trim the value to remove whitespace
                    requestData.put(key, value);
                }
            }

            long time = Long.parseLong(requestData.get("time").toString());
            String sign = (String) requestData.get("sign");
            String integrator_id = (String) requestData.get("integrator_id");
            String trans_id = (String) requestData.get("trans_id");

            if (integrator_id == null || trans_id == null || sign == null
                    || integrator_id.isEmpty() || trans_id.isEmpty() || sign.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "400");
                errorResponse.put("msg", "Bad Request: Required fields are missing or empty.");
                return errorResponse;
            }
            
            

            int count = getCountForTransaction(trans_id);
            if (count == 2) {
                Map<String, Object> data = new HashMap<>();
                data.put("terminal_id", "");
                data.put("trans_id", trans_id);
                data.put("status", "200");
                data.put("msg", "Success Payment");
                data.put("paystatus", "D");

                Map<String, Object> response = new HashMap<>();
                response.put("code", "200");
                response.put("msg", "Success");
                response.put("data", data);
                
                // Reset transactionCountMap after processing
                //resetTransactionCountMap();
                
                return response;
                
            } else {
                incrementCountForTransaction(trans_id);

                Map<String, Object> data = new HashMap<>();
                data.put("terminal_id", "");
                data.put("trans_id", trans_id);
                data.put("status", "202");
                data.put("msg", "Waiting Payment");
                data.put("paystatus", "W");

                Map<String, Object> response = new HashMap<>();
                response.put("code", "200");
                response.put("msg", "Success");
                response.put("data", data);
               
                return response;
            } 
        }

        private static Map<String, Integer> transactionCountMap = new HashMap<>();

        private int getCountForTransaction(String trans_id) {
            Integer count = transactionCountMap.get(trans_id);
            return count == null ? 0 : count;
        }

        private void incrementCountForTransaction(String trans_id) {
            Integer count = transactionCountMap.get(trans_id);
            if (count == null) {
                transactionCountMap.put(trans_id, 1);
            } else {
                transactionCountMap.put(trans_id, count + 1);
            }
        }

       
        
        
        
        
//IOUPAY SIMULATOR - 4 APIS       
        
        @Value("${iou.threshold.x_amount}")
        private float thresholdx_Amount;

        @Value("${iou.decline.x_amount}")
        private float declinex_Amount;
        
        @Value("${iou.delay.x_amount}")
        private float delayx_Amount;
        
        @Value("${iou.timeout.x_amount}")
        private float timeoutx_Amount;   
        
        
        
        @GetMapping("/v1/merchant_packages")
        @ResponseBody
        public Map<String, Object> getMerchantPackagesAPI(@RequestParam("x_account_id") final String x_account_id,
                @RequestParam("x_token") final String x_token) {
            if (x_account_id == null || x_token == null || x_account_id.isEmpty() || x_token.isEmpty()) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "400");
                errorResponse.put("msg", "Bad Request: Required fields are missing or empty.");
                return errorResponse;
            }

            final Map<String, Object> packageDetail = new HashMap<>();
            packageDetail.put("tenure", 2);

            final Map<String, Object> packageInfo = new HashMap<>();
            packageInfo.put("package_code", "NORMAL");
            packageInfo.put("first_payment_percent", 30);
            packageInfo.put("deposit_min_limit", 0);
            packageInfo.put("payment_min_limit", 50);
            packageInfo.put("rental_min_limit", 0);
            packageInfo.put("booking_min_limit", 0);
            packageInfo.put("deposit_max_limit", 0);
            packageInfo.put("payment_max_limit", 10000);
            packageInfo.put("rental_max_limit", 0);
            packageInfo.put("booking_max_limit", 0);
            packageInfo.put("deposit_pre_approve_limit", 0);
            packageInfo.put("payment_pre_approve_limit", 0);
            packageInfo.put("rental_pre_approve_limit", 0);
            packageInfo.put("booking_pre_approve_limit", 0);

            final List<Map<String, Object>> packageDetailList = new ArrayList<>();
            packageDetailList.add(packageDetail);

            final List<Map<String, Object>> packageList = new ArrayList<>();
            packageList.add(packageInfo);
            packageInfo.put("package_detail_list", packageDetailList);

            final List<Map<String, Object>> merchantPaymentTypeList = new ArrayList<>();
            final Map<String, Object> packageItem = new HashMap<>();
            packageItem.put("payment_type", "PAYMENT");
            packageItem.put("payment_description", null);
            packageItem.put("order_num", null);
            packageItem.put("package_list", packageList);
            merchantPaymentTypeList.add(packageItem);

            final Map<String, Object> details = new HashMap<>();
            details.put("merchant_id", 0);
            details.put("merchant_code", null);
            details.put("merchant_name", null);
            details.put("merchant_payment_type_list", merchantPaymentTypeList);

            final Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("message", "Success");
            response.put("version", "v1");
            response.put("details", details);

            return response;
        }

        
        
              
        @GetMapping("/v1/customer_information")
        @ResponseBody
        public Map<String, Object> getCustomerInformationAPI(@RequestParam("x_account_id") final String x_account_id,
                @RequestParam("x_token") final String x_token, @RequestParam("x_qr_content") final String x_qr_content) {
            if (x_account_id == null || x_token == null || x_qr_content == null ||
                    x_account_id.isEmpty() || x_token.isEmpty() || x_qr_content.isEmpty()) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "400");
                errorResponse.put("msg", "Bad Request: Required fields are missing or empty.");
                return errorResponse;
            }

            final Map<String, Object> successResponse = new HashMap<>();
            final Map<String, Object> details = new HashMap<>();
            details.put("cust_code", "60123456789");
            details.put("cust_name", "John Smith");
            details.put("email", "john.smith@gmail.com");
            successResponse.put("code", 0);
            successResponse.put("message", "Success");
            successResponse.put("version", "v1");
            successResponse.put("details", details);

            return successResponse;
        }

        
               
        
        @PostMapping("/v1/pos/payment" )
        @ResponseBody
        public Map<String, Object> proceedPaymentAPI(@RequestParam("x_account_id") String x_account_id,
                @RequestParam("x_token") String x_token, @RequestParam("x_qr_content") String x_qr_content,
                @RequestParam("x_amount") double x_amount, @RequestParam("x_payment_type") String x_payment_type,
                @RequestParam("x_package_code") String x_package_code, @RequestParam("x_tenure") int x_tenure,
                @RequestParam("x_reference") String x_reference, @RequestParam("x_callback_url") String x_callback_url) {

            if (x_account_id == null || x_token == null || x_qr_content == null || x_amount <= 0.0 ||
                    x_payment_type == null || x_package_code == null || x_tenure <= 0 ||
                    x_reference == null || x_callback_url == null || x_account_id.isEmpty() ||
                    x_qr_content.isEmpty() || x_token.isEmpty() || x_payment_type.isEmpty() ||
                    x_package_code.isEmpty() || x_reference.isEmpty() || x_callback_url.isEmpty()) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "400");
                errorResponse.put("msg", "Bad Request: Required fields are missing or empty.");
                return errorResponse;
            }

            float parsedAmount = (float) x_amount;
        
            if (parsedAmount == declinex_Amount) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "204");
                errorResponse.put("msg", "Failed: Decline amount received.");
                return errorResponse;
            }
            
            if (parsedAmount >= thresholdx_Amount) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "204");
                errorResponse.put("msg", "Failed: Amount exceeds threshold.");
                return errorResponse;
            }
            
            
            
            if (parsedAmount == timeoutx_Amount) {
                final Map<String, Object> errorresponse = new HashMap<>();
                errorresponse.put("code", "402");
                errorresponse.put("msg", "Timeout : Server timeout.");
                return errorresponse;
            }
            
            
            if (parsedAmount == delayx_Amount) {
            	
            	try {
                    Thread.sleep(15000); // Delay for 15 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            	
                Map<String, Object> response = new HashMap<>();
                response.put("code", 0);
                response.put("message", "Success : Delayed response");
                response.put("version", "v1");

                Map<String, Object> details = new HashMap<>();
                String transactionNo = generateRandomTransactionNo();
                details.put("transaction_no", transactionNo);
                details.put("total_amount", 120);
                details.put("transaction_date", "2021-08-11T03:45:41.7595261Z");
                details.put("payment_type", "PAYMENT");
                details.put("payment_description", "Payment No: " + transactionNo + ", Merchant Code: T00001, Package: NORMAL, Description: Payment No: " + transactionNo + ", Merchant Code:T00001, Package: NORMAL");

                List<Map<String, Object>> detail_transaction_schedule = new ArrayList<>();

                Map<String, Object> payment1 = new HashMap<>();
                payment1.put("total_amount", 36);
                payment1.put("next_payment_date", null);
                payment1.put("payment_percent", 30);
                payment1.put("order_number", 1);
                payment1.put("is_first_payment", true);
                detail_transaction_schedule.add(payment1);

                Map<String, Object> payment2 = new HashMap<>();
                payment2.put("total_amount", 42);
                payment2.put("next_payment_date", "2021-09-10T00:00:00Z");
                payment2.put("payment_percent", 50);
                payment2.put("order_number", 2);
                payment2.put("is_first_payment", false);
                detail_transaction_schedule.add(payment2);

                Map<String, Object> payment3 = new HashMap<>();
                payment3.put("total_amount", 42);
                payment3.put("next_payment_date", "2021-10-10T00:00:00Z");
                payment3.put("payment_percent", 50);
                payment3.put("order_number", 3);
                payment3.put("is_first_payment", false);
                detail_transaction_schedule.add(payment3);

                details.put("detail_transaction_schedule", detail_transaction_schedule);
                details.put("discount_amount", 0);
                details.put("use_referral_available_credit", null);
                details.put("total_discount_from_referral_available_credit", null);
                details.put("total_discount_amount", 0);
                details.put("transaction_id", 6731);

                response.put("details", details);
                return response;
            }
            
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("message", "Success");
            response.put("version", "v1");

            Map<String, Object> details = new HashMap<>();
            String transactionNo = generateRandomTransactionNo();
            details.put("transaction_no", transactionNo);
            details.put("total_amount", 120);
            details.put("transaction_date", "2021-08-11T03:45:41.7595261Z");
            details.put("payment_type", "PAYMENT");
            details.put("payment_description", "Payment No: " + transactionNo + ", Merchant Code: T00001, Package: NORMAL, Description: Payment No: " + transactionNo + ", Merchant Code:T00001, Package: NORMAL");

            List<Map<String, Object>> detail_transaction_schedule = new ArrayList<>();

            Map<String, Object> payment1 = new HashMap<>();
            payment1.put("total_amount", 36);
            payment1.put("next_payment_date", null);
            payment1.put("payment_percent", 30);
            payment1.put("order_number", 1);
            payment1.put("is_first_payment", true);
            detail_transaction_schedule.add(payment1);

            Map<String, Object> payment2 = new HashMap<>();
            payment2.put("total_amount", 42);
            payment2.put("next_payment_date", "2021-09-10T00:00:00Z");
            payment2.put("payment_percent", 50);
            payment2.put("order_number", 2);
            payment2.put("is_first_payment", false);
            detail_transaction_schedule.add(payment2);

            Map<String, Object> payment3 = new HashMap<>();
            payment3.put("total_amount", 42);
            payment3.put("next_payment_date", "2021-10-10T00:00:00Z");
            payment3.put("payment_percent", 50);
            payment3.put("order_number", 3);
            payment3.put("is_first_payment", false);
            detail_transaction_schedule.add(payment3);

            details.put("detail_transaction_schedule", detail_transaction_schedule);
            details.put("discount_amount", 0);
            details.put("use_referral_available_credit", null);
            details.put("total_discount_from_referral_available_credit", null);
            details.put("total_discount_amount", 0);
            details.put("transaction_id", 6731);

            response.put("details", details);
            return response;
        }

        private static String generateRandomTransactionNo() {
            Random random = new Random();
            String prefix = "S";
            long number = random.nextLong() % 9000000000000L + 1000000000000L;
            return prefix + Math.abs(number);
        }
        
        
        
        
        @GetMapping("/v1/payment/transaction_status")
        @ResponseBody
        public Map<String, Object> paymentEnquiry(@RequestParam final String x_account_id,
                @RequestParam final double x_amount, @RequestParam final String x_reference,
                @RequestParam final String token, @RequestParam final String x_transaction_type) {
            
            if (x_account_id == null || token == null || x_reference == null || x_transaction_type == null
                    || x_amount <= 0.0 || x_account_id.isEmpty() || token.isEmpty() || x_reference.isEmpty()
                    || x_transaction_type.isEmpty()) {
                
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "400");
                errorResponse.put("msg", "Bad Request: Required fields are missing or empty.");
                return errorResponse;
            }
            
            float parsedAmount = (float) x_amount;
            if (parsedAmount == declinex_Amount) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "204");
                errorResponse.put("msg", "Failed: Decline amount received.");
                return errorResponse;
            }
            
            if (parsedAmount >= thresholdx_Amount) {
                final Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "204");
                errorResponse.put("msg", "Failed: Amount exceeds threshold.");
                return errorResponse;
            }
            
            
            
            if (parsedAmount == timeoutx_Amount) {
                final Map<String, Object> errorresponse = new HashMap<>();
                errorresponse.put("code", "402");
                errorresponse.put("msg", "Timeout : Server timeout.");
                return errorresponse;
            }
            
            
            if (parsedAmount == delayx_Amount) {
            	
            	try {
                    Thread.sleep(15000); // Delay for 15 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            	
            	 final Map<String, Object> response = new HashMap<>();
                 response.put("code", -8046);
                 response.put("message",
                         "Delayed response : Payment Transaction is approved. Please contact IOU Pay support at support@ioupay.com or call +603-77335500 for assistance.");
                 response.put("version", "v1");
                 final Map<String, Object> details = new HashMap<>();
                 details.put("transaction_date", "2023-04-26 16:02:58");
                 details.put("transaction_no", generateRandomTransactionNo());
                 details.put("transaction_amount", x_amount);
                 response.put("details", details);
                 return response;
            }

            final Map<String, Object> response = new HashMap<>();
            response.put("code", -8046);
            response.put("message",
                    "Payment Transaction is approved. Please contact IOU Pay support at support@ioupay.com or call +603-77335500 for assistance.");
            response.put("version", "v1");
            final Map<String, Object> details = new HashMap<>();
            details.put("transaction_date", "2023-04-26 16:02:58");
            details.put("transaction_no", generateRandomTransactionNo());
            details.put("transaction_amount", x_amount);
            response.put("details", details);
            return response;
        }
    }

    
