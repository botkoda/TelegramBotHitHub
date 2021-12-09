package com.telegramBot;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.runtime.version", "11");
        GitHubJob gitHubJob=new GitHubJob();
        new TelegramBotGitHub().start();

    }
}
