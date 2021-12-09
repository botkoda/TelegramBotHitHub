import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GitHubJob {
    GitHub github;
    GHRepository repo;
    Map<Integer, String> issueMap = new HashMap<>();

    {
        try {
            //github = new GitHubBuilder().withPassword("botkoda", "Simoron2016").build();
            github = new GitHubBuilder().withOAuthToken("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").build();
            repo = github.getRepository("noviygorod1k/task-tracking");
            List<GHIssue> issuesList = getOpenIssues(repo);

            issuesList.stream().forEach(x -> {
                issueMap.put(x.getNumber(), x.getTitle());
            });

            List<GHIssueComment> issuesCommentList= getCommentsIssues(issuesList,88); //коменты конкретного таска;

            //  List<String> issuesTitle=issuesList.stream().map(x->x.getTitle()).collect(Collectors.toList());
            issueMap.forEach((k,v)-> System.out.println(k+" "+v));
            issuesCommentList.stream().forEach(x-> System.out.println(issuesCommentList.indexOf(x)+"-->"+x.getBody()));
           // System.out.println(issueMap);
            createIssue(repo,"test_lable");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<GHIssue> getOpenIssues(GHRepository ghRepository) throws IOException {
//          List<GHIssue> issues = ghRepository.listIssues(GHIssueState.ALL)
//       .withPageSize(1000)
//       .asList();
        List<GHIssue> issues = ghRepository.getIssues(GHIssueState.OPEN);
        List<GHIssue> answer = new ArrayList<>();
        for (GHIssue issue : issues) {
            answer.add(issue);
        }
        return answer;
    }

    private List<GHIssueComment> getCommentsIssues(List<GHIssue> issuesList, int number) {
        GHIssue issue = new GHIssue();
        List<GHIssueComment> comments = null;
        for (GHIssue i : issuesList) {
            if (i.getNumber() == number) {
                issue = i;
            }
        }
        try {
            comments = issue.getComments();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return comments;
    }

    private void createIssue(GHRepository ghRepository,String lable){
        ghRepository.createIssue(lable);

    }
}
