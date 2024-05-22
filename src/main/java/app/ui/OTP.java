package app.ui;

import java.security.SecureRandom;

class OTP {

    private static final int OTP_LENGTH = 6;
    private String generatedOTP;
    private String title;

    OTP(String title) {
        this.title = title;
    }
    
    public boolean verifyOTP(String otp) {
        return (otp.equals(generatedOTP)) ? true : false;
    }

    public String generateOTP() {

        StringBuilder otp = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < OTP_LENGTH; i++) { 
            otp.append(random.nextInt(10));  
        }

        if (!SendNotification.sendOTP(otp.toString(), title))
            return null; // Failed to Send OTP      

        generatedOTP = otp.toString();
        return generatedOTP;
    }

    public static void main(String[] args) {
        OTP otp = new OTP("SBI");
        otp.generateOTP();
        otp.verifyOTP("12234");
    }
}

