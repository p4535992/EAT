package p4535992.util.json;
import org.apache.http.client.HttpClient;
/**
 * Created by 4535992 on 28/04/2015.
 */
public class GsonKit {

   /* public static JsonObject getData(String url)
    {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try
        {
            HttpResponse response = httpClient.execute(httpPost);
            String content = EntityUtils.toString(response.getEntity());
            Gson gson = new Gson();
            JsonObject jobj = new Gson().fromJson(content, JsonObject.class);
            return jobj;
        }
        catch (Exception ex)
        {
            return null;
        }
    }*/
}
