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
            github = new GitHubBuilder().withOAuthToken("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").build();
            repo = github.getRepository("noviygorod1k/task-tracking");
            List<GHIssue> issuesList = getOpenIssues(repo);
            //все открытые таски
            issuesList.stream().forEach(x -> {
                issueMap.put(x.getNumber(), x.getTitle());
            });
            //коменты конкретного таска;
            List<GHIssueComment> issuesCommentList= getCommentsIssues(findIssue(88,issuesList)); 
            //вывод в консоль    
            issueMap.forEach((k,v)-> System.out.println(k+" "+v));
            issuesCommentList.stream().forEach(x-> System.out.println(issuesCommentList.indexOf(x)+"-->"+x.getBody()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<GHIssue> getOpenIssues(GHRepository ghRepository) throws IOException {
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
    
    public GHIssue findIssue(int number, List<GHIssue> issues) {
        //GHIssue issue=issues.Stream().map(issue->issue.getNumber()).filter(x->x==number).findAny().get()
        
    if (issues != null) {
        for (GHIssue issue : issues) {
            if (issue.getNumber() == number) {
                return issue;
            }
        }
    }
    return null;
}
 

    public List<GHIssueComment> getCommentsIssues(GHIssue issue) {
//         GHIssue issue = new GHIssue();
//         List<GHIssueComment> comments = null;
//         for (GHIssue i : issuesList) {
//             if (i.getNumber() == number) {
//                 issue = i;
//             }
//         }
        if (issue != null) {
        try {
           List<GHIssueComment> comments = issue.getComments();
           return comments;
        } catch (IOException e) {
            e.printStackTrace();
        }   
        }
        return null;
    }

    public void createIssue(GHRepository ghRepository,String lable){
        ghRepository.createIssue(lable);

    }
}
