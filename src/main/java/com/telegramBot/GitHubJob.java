package com.telegramBot;

import org.kohsuke.github.*;

import java.io.IOException;
import java.util.*;


public class GitHubJob {
    GitHub github;
    GHRepository repo;


    {
        try {
            github = new GitHubBuilder().withOAuthToken("gittoken").build();
            repo = github.getRepository("noviygorod1k/task-tracking");


//            getOpenIssues().forEach((k, v) -> System.out.println(k + " " + v));
//            getCommentsIssues(88).forEach(x -> System.out.println(x));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, String> getOpenIssues() {
        Map<Integer, String> issueMap = new HashMap<>();
        //  List<GHIssue> answer = new ArrayList<>();

        //  List<GHIssue> issues = null;
        if (repo != null) {
            try {
                List<GHIssue> issues = repo.getIssues(GHIssueState.OPEN);
                issues.stream().forEach(x -> {
                    try {
                        if (x.getAssignee() != null) {
                            if (x.getMilestone() != null) {
                                issueMap.put(x.getNumber(), x.getTitle() + "..(отв: " + x.getAssignee().getLogin() + ")" + "%%%" + x.getMilestone().getTitle());
                            } else {
                                issueMap.put(x.getNumber(), x.getTitle() + "..(отв: " + x.getAssignee().getLogin() + ")" + "%%%НЕТ");
                            }

                        } else {
                            if (x.getMilestone() != null) {
                                issueMap.put(x.getNumber(), x.getTitle() + "...(отв: НЕТ)" + "%%%" + x.getMilestone().getTitle());
                            } else {
                                issueMap.put(x.getNumber(), x.getTitle() + "...(отв: НЕТ)" + "%%%НЕТ");
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return issueMap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return issueMap;

    }

    private GHIssue findIssue(int number) {
        try {
            List<GHIssue> issues = repo.getIssues(GHIssueState.OPEN);
            if (issues != null) {
                for (GHIssue issue : issues) {
                    if (issue.getNumber() == number) {
                        return issue;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<String> getCommentsIssues(int number) {
        GHIssue issue = findIssue(number);
        List<String> listIssueWithComment = new ArrayList<>();
        if (issue != null) {
            try {
                listIssueWithComment.add("Тема: " + issue.getNumber() + "." + issue.getTitle());
                listIssueWithComment.add("Проблема: " + issue.getBody());
                for (GHIssueComment l : issue.getComments()) {
                    listIssueWithComment.add("Коммент: " + l.getBody());
                }//.stream().map(x->listIssueWithComment.add("Комментарии:" +x.getBody() ));
                return listIssueWithComment;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return listIssueWithComment;
    }


    public void createIssue(String lable) {
        try {
            GHIssueBuilder ghIssueBuilder = repo.createIssue(lable);
            ghIssueBuilder.create();
        } catch (IOException e) {
            System.out.println("Ошибка создания issue");
        }
    }

    public Set<String> getOpenIssueYear() {
        Set<String> setIssueYear = new HashSet<>();
        if (repo != null) {
            try {
                List<GHIssue> issues = repo.getIssues(GHIssueState.OPEN);
                issues.stream().forEach(x -> {
                    if (x.getMilestone() != null) {
                        setIssueYear.add(x.getMilestone().getTitle());
                    } else
                        setIssueYear.add("НЕТ");
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setIssueYear.add("ВСЕ");
        return setIssueYear;
    }

}
