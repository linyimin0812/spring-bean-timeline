package io.github.linyimin.startup;

import java.io.File;

/**
 * @author yiminlin
 * @date 2023/05/10 00:50
 * @description TODO:
 **/
public class AppNameUtil {

    private static final String JAR_SUFFIX_LOWER = ".jar";
    private static final String JAR_SUFFIX_UPPER = ".JAR";

    private static String appName;

    public static String getAppName() {
        if (appName == null) {
            resolveAppName();
        }

        return appName;
    }

    // 来自sentinel的实现
    private static void resolveAppName() {
        String app = System.getProperty("project.name");
        // use -Dproject.name first
        if (app != null && app.length() > 0) {
            appName = app;
            return;
        }

        // use -Dspring.application.name
        app = System.getProperty("spring.application.name");
        if (app != null && app.length() > 0) {
            appName = app;
            return;
        }

        // parse sun.java.command property
        String command = System.getProperty("sun.java.command");
        if (command == null || command.length() == 0) {
            return;
        }
        command = command.split("\\s")[0];
        String separator = File.separator;
        if (command.contains(separator)) {
            String[] strs;
            if ("\\".equals(separator)) {
                strs = command.split("\\\\");
            } else {
                strs = command.split(separator);
            }
            command = strs[strs.length - 1];
        }
        if (command.endsWith(JAR_SUFFIX_LOWER) || command.endsWith(JAR_SUFFIX_UPPER)) {
            command = command.substring(0, command.length() - 4);
        }
        appName = command;
    }

}

