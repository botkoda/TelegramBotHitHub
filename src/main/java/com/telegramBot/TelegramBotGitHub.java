package com.telegramBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TelegramBotGitHub {
    String[] userNames = {"Dmshagaliev", "MarinaVlaso", "oleg_lisitsyn", "tmspich", "anton_ng_izh", "StanislavShirobokov", "IrinaShagalieva"};
    String[] chatNames = {"testbot", "ВЛКСМ 2: Совет"};
    GitHubJob gitHubJob;
    private final TelegramBot telegramBot = new TelegramBot("");

    public TelegramBotGitHub(GitHubJob gitHubJob) {
        this.gitHubJob = gitHubJob;
    }

    public void start() {
        telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {
        BaseRequest request = null;
        int numberIssue = 0;
        //получение
        Message message = update.message();
        try {
            if (message != null && (Arrays.asList(userNames).contains(message.chat().username()) ||
                    Arrays.asList(chatNames).contains(message.chat().title()))
            ) {
                if (message.text() != null) {
                    String[] spMessage = message.text().split("_");

                    if (spMessage.length == 2) {
                        try {
                            numberIssue = Integer.parseInt(spMessage[1]);
                        } catch (NumberFormatException e) {
                            numberIssue = 0;
                        }
                    }
                    //обработка
                    //получить все открытые issue
                    if (message.text().equals("gitissue") || message.text().equals("Gitissue")) {
                        StringBuffer sb = new StringBuffer();
                        Map<Integer, String> issueMap = gitHubJob.getOpenIssues();
                        long chatId = message.chat().id();
                        issueMap.forEach((k, v) -> {
                            sb.append(k + ".." + v + "\n");
                        });

                        request = new SendMessage(chatId, sb.toString());
                    }

                    //получить коммент
                    if ((spMessage[0].equals("gitissue") || spMessage[0].equals("Gitissue")) && numberIssue > 0) {
                        StringBuffer sb = new StringBuffer();
                        List<String> issueWithComment = gitHubJob.getCommentsIssues(numberIssue);
                        long chatId = message.chat().id();
                        if (!issueWithComment.isEmpty() && issueWithComment != null) {
                            issueWithComment.forEach(x ->
                                    sb.append(x + "\n")
                            );
                            request = new SendMessage(chatId, sb.toString());
                        } else {
                            request = new SendMessage(chatId, "ошибка, проверьте номер");
                        }
                    }

                    //добавить issue
                    if (spMessage[0].equals("addissue") || spMessage[0].equals("Addissue")) {
                        String text=message.text().replace(spMessage[0]+"_","");
                        gitHubJob.createIssue(text);
                        long chatId = message.chat().id();
                        request = new SendMessage(chatId, "На GitHub добавлена проблема: " + text);

                    }

                    if (message.text().equals("git") || message.text().equals("Git")) {
//                        String text = "***gitissue***-посмотреть все открытые проблемы на github\n" +
//                                "***gitissue_номер*** -команда выводит инф-ю по проблеме `номер`:\n" +
//                                "`gitissue_1` или `gitissue_12` или `gitissue_121`  и т.п.\n" +
//                                "***addissue_текст*** -команда добавляет проблему `текст` на GitHub :\n" +
//                                "`addissue_не работает доводчик на калитке у 6й секции`";

                        String text2 = "<strong>gitissue</strong> --команда \n" +
                                "<i>выводит все открытые проблемы на github</i> \n\n" +
                                "<strong>gitissue_номер</strong> --команда \n" +
                                "выводит инф-ю по проблеме. пример: <i>gitissue_1</i> \n\n" +
                                "<strong>addissue_текст</strong> --команда\n" +
                                "добавляет проблему на GitHub. пример :\n" +
                                "<i>addissue_не работает доводчик на калитке у 6й секции</i>";
                        long chatId = message.chat().id();
                        request = new SendMessage(chatId, text2).parseMode(ParseMode.HTML);

                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка в боте--" + e);
        }


        //действия бота
        if (request != null) {
            telegramBot.execute(request);
        }
    }
}
