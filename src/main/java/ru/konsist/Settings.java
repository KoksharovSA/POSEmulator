package ru.konsist;

import java.nio.file.Paths;

public class Settings {
    private static Settings settingsApp;
    String ip = "127.0.0.1";
    int port = 5101;
    int socketTimeOut = 30;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    public void setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    public static Settings getInstance(){
        if (settingsApp == null){
            settingsApp = new Settings();
        }
        return settingsApp;
    }
    private Settings() {

        for (String item: Supports.readFile(Paths.get(".").toAbsolutePath().normalize().toString() + "/Requests.txt")) {
            if (item.contains("#ip")){
                this.ip = item.split(":")[1];
            }
            if (item.contains("#port")){
                this.port = Integer.parseInt(item.split(":")[1]);
            }
            if (item.contains("#socketTimeOut")){
                this.socketTimeOut = Integer.parseInt(item.split(":")[1]);
            }
        }
    }
}
