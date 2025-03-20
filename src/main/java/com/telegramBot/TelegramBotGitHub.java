package com.telegramBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.File;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class TelegramBotGitHub {
    String token = "7571835204:AAHEXZzB3phlK0LK4VNfoJYg5N-opt6Vil0";
    String[] userNames = {"Dmshagaliev", "for_insta_post_bot"};
    String[] chatNames = {"ForIstaPostBot"};
    GitHubJob gitHubJob;
    private final TelegramBot telegramBot = new TelegramBot(token);
    private final GigaChatJava gigaChatJava = new GigaChatJava();

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
        CallbackQuery callbackQuery = update.callbackQuery();
        InlineQuery inlineQuery = update.inlineQuery();
        try {

            String token = gigaChatJava.gigaConnect();
//            String textMessage = gigaChatJava.getTextMessage(token, isNull(message.caption())? message.text() : message.caption());
            String textMessage = gigaChatJava.getTextMessage(token, isNull(update.channelPost()) ? message.text() : update.channelPost().text());
            String imageMessage = gigaChatJava.getGenerateImage(token, textMessage);
            String saveUrlPath = gigaChatJava.saveImage(token, imageMessage);
            // Отправляем сообщение
//            request= new SendMessage(message.chat().id(), textMessage);

            // Отправляем картинку
            if (nonNull(saveUrlPath)) {
//                long chatId = update.message().chat().id();
                SendResponse execute = telegramBot.execute(
                        new SendPhoto(update.channelPost().chat().id(), new File(saveUrlPath))
                                .caption(textMessage)
                );
                // Проверяем результат
                if (execute.isOk()) {
                    System.out.println("Фото успешно отправлено!");
                } else {
                    System.out.println("Ошибка при отправке фото: " + execute.description());
                }

            }

            //Получение фото
//            if (nonNull(message.photo())) {
//
//                GetFile fileId = new GetFile(Arrays.stream(message.photo()).filter(x -> x.width() > 320).findFirst().get().fileId());
//                File file = telegramBot.execute(fileId).file();
//                String fullFilePath = telegramBot.getFullFilePath(file);
//
//                try (InputStream in = new URL(fullFilePath).openStream()) {
//                    Files.copy(in, Paths.get("C:/soft/" + fileId.getFileName() + ".jpg"), REPLACE_EXISTING);
//                }
//            }
//            MessageEntity messageEntity = Arrays.stream(message.entities()).findFirst().get();
//            String url = messageEntity.url();
//            try (InputStream in = new URL(url).openStream()) {
//                Files.copy(in, Paths.get("C:/soft/image.jpg"));
//            }

//            if (message != null && (Arrays.asList(userNames).contains(message.chat().username()) ||
//                    Arrays.asList(chatNames).contains(message.chat().title()))
//            ) {
//                if (message.text() != null) {
//                    String[] spMessage = message.text().split("_");
//
//                    if (spMessage.length == 2) {
//                        try {
//                            numberIssue = Integer.parseInt(spMessage[1]);
//                        } catch (NumberFormatException e) {
//                            numberIssue = 0;
//                        }
//                    }
//                    //обработка
//                    if (message.text().equals("gitissue") || message.text().equals("Gitissue")) {
//                        long chatId = message.chat().id();
//                        Set<String> setIssueYear = gitHubJob.getOpenIssueYear();
//                        String[] arrayIssueYear = setIssueYear.stream().toArray(String[]::new);
//                        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(arrayIssueYear);
//                        replyKeyboardMarkup.resizeKeyboard(true);
//                        request = new SendMessage(chatId, "Выберите период:").replyMarkup(replyKeyboardMarkup);
//
//                    }
//
//                    //получить все открытые issue
//                    if (spMessage[0].equals("ВСЕ")) {
//                        long chatId = message.chat().id();
//                        StringBuffer sb = new StringBuffer();
//                        Map<Integer, String> issueMap = gitHubJob.getOpenIssues();
//                        issueMap.forEach((k, v) -> {
//                            sb.append(k + ".." + v.split("%%%")[0] + "\n");
//                        });
//
//                        request = new SendMessage(chatId, sb.toString());
//                    }
//                    //получить  issue за период
//                    if (gitHubJob.getOpenIssueYear().contains(spMessage[0]) && !spMessage[0].equals("ВСЕ")) {
//                        long chatId = message.chat().id();
//                        StringBuffer sb = new StringBuffer();
//                        Map<Integer, String> issueMap = gitHubJob.getOpenIssues();
//                        issueMap.forEach((k, v) -> {
//                            if (v.contains("%%%" + spMessage[0])) {
//                                sb.append(k + ".." + v.split("%%%")[0] + "\n");
//                            }
//                        });
//
//                        request = new SendMessage(chatId, sb.toString());
//                    }
//
//                    //получить коммент
//                    if ((spMessage[0].equals("gitissue") || spMessage[0].equals("Gitissue")) && numberIssue > 0) {
//                        StringBuffer sb = new StringBuffer();
//                        List<String> issueWithComment = gitHubJob.getCommentsIssues(numberIssue);
//                        long chatId = message.chat().id();
//                        if (!issueWithComment.isEmpty() && issueWithComment != null) {
//                            issueWithComment.forEach(x ->
//                                    sb.append(x + "\n")
//                            );
//                            request = new SendMessage(chatId, sb.toString());
//                        } else {
//                            request = new SendMessage(chatId, "ошибка, проверьте номер");
//                        }
//                    }
//
//                    //добавить issue
//                    if (spMessage[0].equals("addissue") || spMessage[0].equals("Addissue")) {
//                        String text = message.text().replace(spMessage[0] + "_", "");
//                        gitHubJob.createIssue(text);
//                        long chatId = message.chat().id();
//                        request = new SendMessage(chatId, "На GitHub добавлена проблема: " + text);
//
//                    }
//
//                    if (message.text().equals("git") || message.text().equals("Git")) {
////                        String text = "***gitissue***-посмотреть все открытые проблемы на github\n" +
////                                "***gitissue_номер*** -команда выводит инф-ю по проблеме `номер`:\n" +
////                                "`gitissue_1` или `gitissue_12` или `gitissue_121`  и т.п.\n" +
////                                "***addissue_текст*** -команда добавляет проблему `текст` на GitHub :\n" +
////                                "`addissue_не работает доводчик на калитке у 6й секции`";
//
//                        String text2 = "<strong>gitissue</strong> --команда \n" +
//                                "<i>выводит все открытые проблемы на github</i> \n\n" +
//                                "<strong>gitissue_номер</strong> --команда \n" +
//                                "выводит инф-ю по проблеме. пример: <i>gitissue_1</i> \n\n" +
//                                "<strong>addissue_текст</strong> --команда\n" +
//                                "добавляет проблему на GitHub. пример :\n" +
//                                "<i>addissue_не работает доводчик на калитке у 6й секции</i>";
//                        long chatId = message.chat().id();
//                        request = new SendMessage(chatId, text2).parseMode(ParseMode.HTML);
//
//                    }
//                }
//            }
        } catch (Exception e) {
            System.out.println("Ошибка в боте--" + e);
        }

        //действия бота
        if (request != null) {
            telegramBot.execute(request);

        }
    }
}
