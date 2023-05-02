package com.github.exadmin.mobilepass.utils;

import java.io.*;

public class CiscoUtils {
    private static final String CMD_SHORT_FILENAME = ".\\_vpncli.cmd";

    public static void runSafe(String password, String pinCodeFromMobileApp, boolean cliCallDisabled) {
        // create cmd-file to be executed
        File cmdFile = new File(CMD_SHORT_FILENAME);
        try (PrintWriter pw = new PrintWriter(cmdFile)) {
            if (Settings.isAutoStopEnabled()) {
                pw.println("taskkill /F /IM vpnagent.exe /IM vpnui.exe");
            }

            pw.println("\"" + Settings.getVpncliPath() + "\" -s < %1");
            pw.println("\"" + FileUtils.getFolderOnly(Settings.getVpncliPath()) + "\\vpnui.exe" + "\"");
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        // create parameters file to be passed as argument into executable file
        File tmpCfgFile;
        try {
            tmpCfgFile = File.createTempFile("mpass", "");
            printlnToFxConsole("Temp file created " + tmpCfgFile);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }

        try (PrintWriter pw = new PrintWriter(tmpCfgFile)) {
            pw.println("connect " + Settings.getVpnHost());
            pw.println(Settings.getNtLogin());
            char[] chars = password.toCharArray();
            for (char ch : chars) {
                pw.print(ch);
            }
            pw.println();
            pw.println(pinCodeFromMobileApp);
            pw.println("y");
            pw.println("exit");
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return;
        }

        if (cliCallDisabled) {
            printlnToFxConsole("CLI call is disabled. Stopping establishing process.");
            return;
        }

        Runtime rt = Runtime.getRuntime();
        String[] commands = {CMD_SHORT_FILENAME, tmpCfgFile.getAbsolutePath()};

        Process process;
        try {
            process = rt.exec(commands);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }

        try (InputStream stdout = process.getInputStream ();
             BufferedReader reader = new BufferedReader (new InputStreamReader(stdout))) {

            String line;
            while ((line = reader.readLine ()) != null) {
                line = line.trim();
                if (StrUtils.isStringEmpty(line, false)) continue;

                printlnToFxConsole(line);

                if (line.equals("goodbye...")) break;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        try {
            int attemptsToDeleteFile = 16;

            while (attemptsToDeleteFile > 0) {
                attemptsToDeleteFile--;

                boolean isDeleted = tmpCfgFile.delete();
                printlnToFxConsole("Temp file was " + (isDeleted ? "" : "not") + " deleted");

                if (isDeleted) {
                    printlnToFxConsole("Temp file was deleted successfully. You can close application now.");
                    break;
                }

                printlnToFxConsole("Waiting a little to repeat deletion attempt");
                ThreadUtils.sleep(attemptsToDeleteFile > 8 ? 500 : 1000); // let's wait more if first attempts are failed
            }

            if (attemptsToDeleteFile == 0) {
                printlnToFxConsole("ERROR: Can't delete temp file at "+ tmpCfgFile.getAbsolutePath() + ", please delete it manually! It contains NT Password in plain-text!!!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void printlnToFxConsole(String msg) {
        System.out.println(msg);
    }
}
