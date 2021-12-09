import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GitHubJob {
    GitHub github;
    GHRepository repo;


    {
        try {
            github = new GitHubBuilder().withOAuthToken("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").build();
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
                issues.stream().forEach(x -> issueMap.put(x.getNumber(), x.getTitle()));
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
                listIssueWithComment.add("Тема: " +issue.getNumber()+"."+issue.getTitle());
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

    public GHIssueBuilder createIssue(GHRepository ghRepository, String lable) {
        GHIssueBuilder ghIssueBuilder = ghRepository.createIssue(lable);
        return ghIssueBuilder;
    }
}
