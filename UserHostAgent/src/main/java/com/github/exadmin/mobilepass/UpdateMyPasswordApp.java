package com.github.exadmin.mobilepass;

import com.github.exadmin.mobilepass.utils.Settings;

public class UpdateMyPasswordApp {
    public static void main(String[] args) {
        System.out.println("Usage: UpdateMyPasswordApp login password keystore-pass-phrase");

        if (args.length == 3) {
            System.out.println("All parameters are provided. Updating properties file");
            String login = args[0];
            String passw = args[1];
            String kspas = args[2];

            Settings.loadFromFile();
            Settings.setNTLogin(login);
            Settings.setNtPassword(passw, kspas);
            Exception ex = Settings.saveSettingsToFile();

            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println("Done.");
            }
        }
    }
}
