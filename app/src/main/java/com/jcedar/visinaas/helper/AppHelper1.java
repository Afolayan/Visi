package com.jcedar.visinaas.helper;

/*
import com.jcedar.visinaas.provider.DataContract;
import com.jcedar.visinaas.util.PrefUtils;
*/

import android.content.Context;

import com.jcedar.visinaas.volley.RequestHandler;

/*import com.android.volley.Response;
import com.android.volley.VolleyError;*/

public class AppHelper1  {
    static RequestHandler requestHandler = new RequestHandler();
    private static final String TAG = AppHelper1.class.getSimpleName();

    public static void pullAndSaveAllStudentData(final Context context){
          /*Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        return;
                    }
                };

                Response.Listener<String> onSuccess = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, response + " response1");
                        if(response == null){
                            return;
                        }
                        try {  ArrayList<ContentProviderOperation> operations =
                                new StudentHandler(context).parse(response);
                        if (operations.size() > 0) {
                            ContentResolver resolver = context.getContentResolver();
                            resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations);

                        }
                        }catch (IOException | OperationApplicationException | RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                };
              requestHandler.getAllStudent( onSuccess, errorListener);
*/
    }


    public static void pullAndSaveStudentChapterData(final Context context){
/*
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                return;
            }
        };

        Response.Listener<String> onSuccess = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response + " response_chapter");
                if(response == null){
                    return;
                }
                try {  ArrayList<ContentProviderOperation> operations =
                        new StudentChapterHandler(context).parse(response);
                    if (operations.size() > 0) {
                        ContentResolver resolver = context.getContentResolver();
                        resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations);

                    }
                }catch (IOException | OperationApplicationException | RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        String chapter="";
        if ( AccountUtils.getUserChapter(context) != null){
            chapter = AccountUtils.getUserChapter(context);
        }
        requestHandler.getStudentByChapter(chapter, onSuccess, errorListener);
    */

    }


}