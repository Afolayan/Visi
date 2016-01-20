package com.jcedar.visinaas.volley;

/*
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;*/

/**
 * Created by Afolayan on 13/10/2015.
 */
public class RequestHandler {

    private static final String TAG = RequestHandler.class.getSimpleName();
    static String response = null;
    private static RequestHandler instance;

   /* public static ServiceHandler getInstance() {
            if(instance == null){
                instance = new ServiceHandler();
            }
        return instance;
    }*/

    public RequestHandler() {
    }

    /*public void checkUserEmail(String emailExtra, Response.Listener<String> success,
                               Response.ErrorListener error){
        Uri.Builder builder = Uri.parse(AppSettings.SERVER_URL).buildUpon()
                                .appendQueryParameter("email", emailExtra);
        String url = builder.build().toString();

        StringRequest request = new StringRequest(Request.Method.GET, url, success, error);

        RequestManager.getRequestQueue().add(request);

    }

    public void getAllStudent(Response.Listener<String> success, Response.ErrorListener error){
        Uri.Builder builder = Uri.parse(AppSettings.SERVER_URL).buildUpon()
                            .appendPath("get_all_students.php");
        String url = builder.build().toString();

        StringRequest request = new StringRequest(Request.Method.GET, url, success, error);

        RequestManager.getRequestQueue().add(request);
    }

    public void getStudentByChapter(String school, Response.Listener<String> success, Response.ErrorListener error){
        Uri.Builder builder = Uri.parse(AppSettings.SERVER_URL).buildUpon()
                .appendPath("getUsersChapter.php")
                .appendQueryParameter("chapter", school);
        String url = builder.build().toString();

        StringRequest request = new StringRequest(Request.Method.GET, url, success, error);

        RequestManager.getRequestQueue().add(request);
    }*/
}
