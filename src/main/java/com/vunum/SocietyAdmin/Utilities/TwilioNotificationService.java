package com.vunum.SocietyAdmin.Utilities;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TwilioNotificationService {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String twilioPhoneNumber;

    @Value("${twilio.service-sid}")
    private String SERVICE_SID;

    public void sendSms(String phoneNumber, String message) {

        Twilio.init(accountSid, authToken);

        Message message1 = Message.creator(
                        new PhoneNumber(phoneNumber),
                        new PhoneNumber(twilioPhoneNumber),
                        message)
                .create();
        message1.getStatus().toString();
    }


    /**
     * Sends an OTP to the given phone number via SMS using Twilio Verify Services.
     *
     * @param phoneNumber The recipient's phone number (E.164 format, e.g., +1234567890).
     */
    public void sendSMS(String phoneNumber) {

        Twilio.init(accountSid, authToken);
        try {
            Verification verification = Verification.creator(
                    SERVICE_SID,
                    phoneNumber,
                    "sms"
            ).create();

            log.info("OTP sent successfully to {}. Status: {}", phoneNumber, verification.getStatus());
        } catch (Exception e) {
            log.error("Error sending OTP to {}: {}", phoneNumber, e.getMessage());
            throw new RuntimeException("Failed to send OTP, please try again.");
        }
    }

    /**
     * Verifies the OTP entered by the user against the code sent to their phone.
     *
     * @param phoneNumber The phone number to verify (E.164 format, e.g., +1234567890).
     * @param otpCode     The OTP entered by the user.
     * @return True if the OTP is valid; otherwise, false.
     */
    public boolean verifyOTP(String phoneNumber, String otpCode) {

        Twilio.init(accountSid, accountSid);
        try {

            VerificationCheck verificationCheck = VerificationCheck.creator(SERVICE_SID)
                    .setTo(phoneNumber)
                    .setCode(otpCode)
                    .create();

            log.info("Verification status for {}: {}", phoneNumber, verificationCheck.getStatus());

            return "approved".equalsIgnoreCase(verificationCheck.getStatus());
        } catch (Exception e) {
            log.error("Error verifying OTP for {}: {}", phoneNumber, e.getMessage());
            throw new RuntimeException("Failed to verify OTP, please try again.");
        }
    }

    public void sendOTP(String phoneNumber) {
        sendSMS(phoneNumber);
    }
}
