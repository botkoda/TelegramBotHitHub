package com.telegramBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.List;
import java.util.Map;

public class TelegramBotGitHub {
    GitHubJob gitHubJob;
    private final TelegramBot telegramBot = new TelegramBot("5018667969:AAEtthCDW2RDgY7c0j1ANV43HTNMd-k79D4");

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
        if (message != null) {
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
                if (spMessage[0].equals("addissue") || spMessage[0].equals("Addissue") ) {
                    StringBuffer sb = new StringBuffer();
                    gitHubJob.createIssue(spMessage[1]);
                    long chatId = message.chat().id();
                    request = new SendMessage(chatId, "На GitHub добавлена проблема: " +spMessage[1]);

                }

                if (message.text().equals("git") || message.text().equals("Git")) {
                    String text="***gitissue***-посмотреть все открытые проблемы на github" +
                            "***gitissue_номер*** -команда выводит инф-ю по проблеме `номер`:" +
                            "`gitissue_1` или `gitissue_12` или `gitissue_121`  и т.п.\n"+
                            "***addissue_текст*** -команда добавляет проблему `текст` на GitHub :" +
                            "`addissue_не работает доводчик на калитке у 6й секции`";
                    long chatId = message.chat().id();
                    request = new SendMessage(chatId, text).parseMode(ParseMode.Markdown);

                }
            }
        }


        //действия бота
        if (request != null) {
            telegramBot.execute(request);
        }
    }
}
