package com.hackathon.project.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class StartHackathon
{

    public static void main(String[] args)
    {
        try {
            
            URL url = new URL("http://dev-intershop2-101.flatns.net:8080/job/build_rc_trout/49/api/xml?wrapper=changes&xpath=//changeSet//comment");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String buildNumber="123";
            String line = null;
            String componentName = null;
            Set <String> jiraList= new TreeSet<>();
            while ((line = reader.readLine()) != null)
            {
                Pattern pattern = Pattern.compile("[a-zA-Z]*-.[0-9]*[{0-9}+$]\\b");
                Matcher matcher = pattern.matcher(line);
                int matchlength = matcher.regionEnd();
                int matchindex = 0;
                String match = null;
                while (matchindex < matchlength)
                {
                    if (matcher.find(matchindex)) {
                        matchindex = matcher.end();
                        match = matcher.group(0);
                        if (jiraList.contains(match)){  
                        }
                        else {
                        jiraList.add(match);
                        }
                    }
                    else {
                        matchindex=matchlength+1;
                    }
                }
            }
            
            URI jiraServerUri = null;
            try
            {
                jiraServerUri = new URI("https://jira.tomtomgroup.com");
            }
            catch(URISyntaxException e)
            {
                e.printStackTrace();
            }
            
            try{
                PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
                
            final JiraRestClient restClient =  new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(jiraServerUri, "shettyso", "TomTom@456");

            for (String i : jiraList) {
                Promise<Issue> issue = restClient.getIssueClient().getIssue(i);
                String issueKey =  issue.claim().getKey();
                String issueSummary = issue.claim().getSummary();
                Iterator<Version> issuefixVersion = issue.claim().getFixVersions().iterator();
                String issueStatus = issue.claim().getStatus().getName();
                Iterator<BasicComponent> issueComponent = issue.claim().getComponents().iterator();
                writer.println("Issue Number : "+issueKey);
                writer.println("Summary : "+issueSummary);
                writer.println("Status : "+issueStatus);
                writer.print("Fix Version : ");
                while(issuefixVersion.hasNext()){
                    Version version = issuefixVersion.next();
                    writer.print(version.getName());
                }
                writer.println();
                while(issueComponent.hasNext()){
                    BasicComponent component = issueComponent.next();
                    writer.print(component.getName());
                    componentName=component.getName();
                }
                writer.println();
            }
            
            restClient.close();
            writer.close();
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            File oldName = new File("the-file-name.txt");
            File newName = new File(componentName+"_"+buildNumber+"_"+timeStamp+".txt");
            
            if(oldName.renameTo(newName)) {
               System.out.println("renamed");
            } else {
               System.out.println("Error");
            }
            } catch (IOException e) {
               // do something
            }
            
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }    
}
