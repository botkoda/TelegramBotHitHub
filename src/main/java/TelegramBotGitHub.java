import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.Map;

public class TelegramBotGitHub {
    private final TelegramBot telegramBot = new TelegramBot("5018667969:AAEtthCDW2RDgY7c0j1ANV43HTNMd-k79D4");

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
        //  CallbackQuery callbackQuery = update.callbackQuery();
        String[] spMessage = message.text().split("_");
        if (spMessage.length == 2) {
            try {
                numberIssue = Integer.parseInt(spMessage[1]);
            } catch (NumberFormatException e) {
                numberIssue = 0;
            }
        }
        //обработка
        if (message.text().equals("gitissue") || message.text().equals("Gitissue")) {
            StringBuffer sb = new StringBuffer();
            Map<Integer, String> issueMap = new GitHubJob().getOpenIssues();
            long chatId = message.chat().id();
            issueMap.forEach((k, v) -> {
                sb.append(k + ".." + v + "\n");
            });

            request = new SendMessage(chatId, sb.toString());
        }

        if ((spMessage[0].equals("gitissue") || spMessage[0].equals("Gitissue")) && numberIssue > 0) {
            StringBuffer sb = new StringBuffer();
            var issueWithComment = new GitHubJob().getCommentsIssues(numberIssue);
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
        if (message.text().equals("git") || message.text().equals("Git")) {
            long chatId = message.chat().id();
            request = new SendMessage(chatId, "gitissue-посмотреть все открытые проблемы на github;\n" +
                    "gitissue_xx - посмотреть комменты по проблеме, где \"xx\" - номер проблемы. Например: gitissue_88 ");
        }

        //действия бота
        if (request != null) {
            telegramBot.execute(request);
        }
    }
}
