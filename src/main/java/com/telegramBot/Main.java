package com.telegramBot;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.runtime.version", "11");
        new TelegramBotGitHub(new GitHubJob()).start();


    }
}
