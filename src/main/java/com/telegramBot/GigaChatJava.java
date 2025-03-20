package com.telegramBot;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class GigaChatJava {
    private static final String API_URL = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth";
    //    private static final String CLIENT_ID = "ba3fea07-a926-4a3d-8d41-364c987b8c36";
//    private static final String CLIENT_SECRET = "ffc68a33-048c-4ad4-9b69-fcd0d146a874";
    private static final String AUTHORITY_KEY = "YmEzZmVhMDctYTkyNi00YTNkLThkNDEtMzY0Yzk4N2I4YzM2OjVhZDA1ZDNiLWI4MWQtNDJjYS04YzAwLTcwZjc5N2JhMjYxMg==";
    private static final String REQUEST_BODY_SCOPE = "scope=GIGACHAT_API_PERS";
    private static final String PROMPT = "\"ты профессиональный ассистент для написания актуальных новостей, сделай краткую выжимку не более 200 символов из новости которую пришлет пользователь. Результат всегда выдавай на русском языке\"";

    public OkHttpClient disableSslCert() {
        // Создаем TrustManager, который доверяет всем сертификатам
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };

        // Создаем SSLContext с TrustManager
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Создаем клиент OkHttp с отключенной проверкой SSL
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    public String gigaConnect() {
        String token = "";
        try {
            OkHttpClient client = disableSslCert();
            // Тело запроса
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, REQUEST_BODY_SCOPE);

            // Формируем POST-запрос к API GigaChat
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + AUTHORITY_KEY)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("RqUID", UUID.randomUUID().toString())
                    .post(body)
                    .build();

            // Выполняем запрос получаем Token
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String[] responseBody = response.body().string().split(":");
                    String[] split = responseBody[1].split(",");
                    return split[0].replace("\"", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    public String getTextMessage(String token, String text) {
        String userText = "\"" + text + "\"";
        OkHttpClient client = disableSslCert();
        MediaType mediaType = MediaType.parse("application/json");

        String content = "{\n  \"model\": \"GigaChat\",\n  \"messages\": [\n    {\n      \"role\": \"system\",\n   " +
                "   \"content\": " + PROMPT + "\n    },\n    {\n      \"role\": \"user\",\n    " +
                "  \"content\": " + userText + "\n    }\n  ],\n  \"stream\": false,\n  \"update_interval\": 0\n}";

        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url("https://gigachat.devices.sberbank.ru/api/v1/chat/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
//            System.out.printf(response.body().string());
            return parseJson(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getGenerateImage(String token, String text) {
        String regex = "^(.{0,36}).*$";
//        String textParse = parseJson(text);
        String userText = "\"Нарисуй " + text.replace("\"", "'") + "\"";
        OkHttpClient client = disableSslCert();
        MediaType mediaType = MediaType.parse("application/json");

        String content = "{\n  \"model\": \"GigaChat\",\n  \"messages\": [\n    {\n      \"role\": \"system\",\n   " +
                "   \"content\": \"Ты — Василий Кандинский\"\n    },\n    {\n      \"role\": \"user\",\n    " +
                "  \"content\": " + userText + "\n    }\n  ],\n  \"stream\": false,\n  \"update_interval\": 0,\n \"function_call\": \"auto\"\n}";

        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url("https://gigachat.devices.sberbank.ru/api/v1/chat/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String imgPath = parseJson(response.body().string()).replace("<img src=\"", "");
            String result = imgPath.replaceAll(regex, "$1");
            return result;
//            System.out.printf(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String saveImage(String token, String src) {

        OkHttpClient client = disableSslCert();

        Request request = new Request.Builder()
                .url("https://gigachat.devices.sberbank.ru/api/v1/files/" + src + "/content")
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {

                // Получаем путь к директории проекта
                String projectDir = System.getProperty("user.dir");
                String outputPath = projectDir + File.separator + "one.jpeg";

                // Сохраняем изображение
                saveImageWithOverwrite(response.body().byteStream(), outputPath);
                System.out.println("Изображение успешно скачано и сохранено в " + outputPath);
                return outputPath;
            } else {
                System.out.println("Ошибка: " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String parseJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode choices = jsonNode.get("choices");
            TextNode jsonText = (TextNode) choices.get(0).get("message").get("content");
            return jsonText.textValue();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Метод для сохранения изображения
    // Метод для сохранения изображения с перезаписью
    private static void saveImageWithOverwrite(InputStream inputStream, String outputPath) {
        File outputFile = new File(outputPath);

        // Удаляем файл, если он уже существует
        if (outputFile.exists()) {
            if (outputFile.delete()) {
                System.out.println("Старый файл удален: " + outputPath);
            } else {
                System.out.println("Не удалось удалить старый файл: " + outputPath);
                return;
            }
        }

        // Сохраняем новое изображение
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Читаем данные из InputStream и записываем в файл
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
