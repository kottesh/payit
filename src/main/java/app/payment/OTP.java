package app;

import java.util.Scanner;
import java.security.SecureRandom;

class OTP {

    private static final int OTP_LENGTH = 6;
    // private static final long OTP_DURATION = 30000l;

    public static boolean verifyOTP(String phoneNumber) {
        String otp = new String();
        otp = generateOTP();

        System.out.println(otp);
        // Sending otp to the given number
        if (!sendSMS.sendOTP(otp.toString(), phoneNumber))
            return false; // Failed to Send OTP

        String enteredOTP = getOTP();

        return (otp.equals(enteredOTP)) ? true : false;
    }

    private static String getOTP() {
        System.out.print("Enter the OTP: ");
        Scanner scan = new Scanner(System.in);

        long otp = scan.nextLong();
        scan.close();
        return otp + "";
    }

    private static String generateOTP() {

        StringBuilder otp = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < OTP_LENGTH; i++) { 
            otp.append(random.nextInt(10));  
        }

        return otp.toString();
    }

    public static void main(String[] args) {
        System.out.println("Successfull");
    }
}

