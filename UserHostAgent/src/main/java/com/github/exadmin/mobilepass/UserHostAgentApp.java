package com.github.exadmin.mobilepass;

import com.github.exadmin.mobilepass.utils.CiscoUtils;
import com.github.exadmin.mobilepass.utils.DateUtils;
import com.github.exadmin.mobilepass.utils.Settings;
import com.github.exadmin.mobilepass.utils.StrUtils;
import io.javalin.Javalin;

import java.util.Set;

public class UserHostAgentApp {
    public static void main(String[] args) {
        Settings.loadFromFile();
        String errorMsg = Settings.checkSettingsOrReturnErrorDescription();
        if (errorMsg != null) {
            System.out.println("Error while settings verification. Message = " + errorMsg);
            return;
        }

        // continue application running
        Javalin app = Javalin.create()
                .port(Settings.getHttpPort())
                .start();

        app.get("/verify", ctx -> ctx.html("I am trusted service you like"));
        app.get("/connect", ctx -> {
            String keyStorePass = ctx.queryParam("kspass");
            String pinCode      = ctx.queryParam("pin");

            warn("/connect is called, kspass = " + keyStorePass + ", pin = " + pinCode);
            if (StrUtils.isStringNonEmpty(keyStorePass, false)) {
                String ntPass = Settings.getNtPassword(keyStorePass);
                if (ntPass == null) {
                    warn("Unable to get NT password using ks-password = " + keyStorePass);
                    return;
                }


                if (pinCode == null || pinCode.length() != 6) {
                    warn("Pin code is not 6 digits, pin = " + pinCode);
                    return;
                }

                ctx.html("Connecting");

                CiscoUtils.runSafe(ntPass, pinCode, false);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private static void warn(String msg) {
        System.out.println("WARN: " + msg);
    }





}
