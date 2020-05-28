package spark;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NCafeCrawler {
    public static void main(String[] args) throws IOException {
//        get("https://apis.naver.com/cafe-web/cafe2/ArticleList.json?search.clubid=19943558&search.queryType=lastArticle&search.page=1");
        String gocd = "19943558";
        String joongo = "10050146";
        String dima = "11262350";

        crawlCafeArticle(dima);
    }

    public static void crawlCafeArticle(String clubid) throws IOException {
        String url = "https://apis.naver.com/cafe-web/cafe2/ArticleList.json?search.clubid="+clubid+"&search.queryType=lastArticle&search.page=1";
        URL parsedUrl = new URL(url);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = new HashMap<String, Object>();
        json = mapper.readValue(parsedUrl, new TypeReference<Map<String, Object>>() { });

        json = (HashMap<String, Object>) json.get("message");
        json = (HashMap<String, Object>) json.get("result");

        List<HashMap<String, Object>> articleList = new ArrayList<>();
        articleList = (List<HashMap<String,Object>>) json.get("articleList");

        for(Map<String, Object> article : articleList){
            System.out.println("=====================================");
            System.out.println("menu: "+article.get("menuName"));
            System.out.println("subject: "+article.get("subject"));
            System.out.println("writer: "+article.get("writerNickname"));
            if(!article.get("commentCount").equals(0)){
                System.out.print("comments: ");
                try{
                    List<HashMap<String, Object>> commentsList = crawlCafeComments(clubid,  article.get("articleId").toString());
                    for(Map<String, Object> comments : commentsList){
                        HashMap<String, Object> writer = (HashMap<String, Object>) comments.get("writer");
                        System.out.print("   "+ writer.get("nick")+ " : ");
                        System.out.println(comments.get("content"));
                    };
                }catch(IOException e){
                    System.out.println("권한이 없습니다.");
                    continue;
                }
            };
        }
    };

    public static List<HashMap<String, Object>> crawlCafeComments(String clubid, String articleId) throws IOException {
        String url = "https://apis.naver.com/cafe-web/cafe-articleapi/cafes/"+clubid+"/articles/"+articleId+"/comments";
        URL parsedUrl = new URL(url);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = new HashMap<String, Object>();
        json = mapper.readValue(parsedUrl, new TypeReference<Map<String, Object>>() { });
        json = (HashMap<String, Object>) json.get("comments");

        List<HashMap<String, Object>> commentsList = new ArrayList<>();
        commentsList = (List<HashMap<String,Object>>) json.get("items");

        return commentsList;
    };

    public static void get(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000); //서버에 연결되는 Timeout 시간 설정
            con.setReadTimeout(5000); // InputStream 읽어 오는 Timeout 시간 설정
            con.setRequestMethod("GET");
            con.setDoOutput(false);

            StringBuilder sb = new StringBuilder();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //Stream을 처리해줘야 하는 귀찮음이 있음.
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                System.out.println("" + sb.toString());
            } else {
                System.out.println(con.getResponseMessage());
            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
