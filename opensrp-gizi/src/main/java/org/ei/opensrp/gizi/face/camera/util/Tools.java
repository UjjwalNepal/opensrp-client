package org.ei.opensrp.gizi.face.camera.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import org.apache.commons.lang3.ArrayUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.gizi.face.camera.ClientsList;
import org.ei.opensrp.gizi.face.camera.ImageConfirmation;
import org.ei.opensrp.gizi.face.camera.SmartShutterActivity;
import org.ei.opensrp.gizi.gizi.GiziDetailActivity;
import org.ei.opensrp.repository.ImageRepository;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;


/**
 * Created by wildan on 1/4/17.
 */
public class Tools {

    private static final String TAG = Tools.class.getSimpleName();
    public static final int CONFIDENCE_VALUE = 58;
    public static org.ei.opensrp.Context appContext;
    public static android.content.Context androContext;
    private static String[] splitStringArray;
    private static Bitmap dummyImage = null;
    private static byte[] headerOfVector;
    private static byte[] bodyOfVector;
    private static byte[] lastContentOfVector;
    //    private static String headerOne;
    private static byte[] albumVectors;
    private Bitmap helperImage = null;
    private Canvas canvas = null;
    SmartShutterActivity ss = new SmartShutterActivity();
    ClientsList cl = new ClientsList();
    private static HashMap<String, String> hash;
    private String albumBuffer;
    private List<ProfileImage> list;
    private static String anmId = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
    private static ProfileImage profileImage = new ProfileImage();
    private static ImageRepository imageRepo = (ImageRepository) org.ei.opensrp.Context.imageRepository();
    private FaceRepository faceRepo = (FaceRepository) new FaceRepository().faceRepository();
//    private static FaceRepository faceRepo = (FaceRepository) Context.faceRepository();

    String headerO = "76, 120, -92, -48, -4, 40, 84, -128, -84, -40, 4, 48, 92, -120, -76, -32, 12, 56, 100, -112, -68, -24, 20, 64, 108, -104, -60, -16, 28, 72, 116, -96, -52, -8, 36, 80, 124, -88, -44, 0, 44, 88, -124, -80, -36, 8, 52, 96, -116, -72, -28, 16, 60, 104, -108, -64, -20, 24, 68, 112, -100, -56, -12, 32";
    static String emptyAlbum = "[32, 0, 0, 0, 76, 65, -68, -20, 77, 116, 46, 83, 105, 110, 97, 105, 6, 0, 0, 0, -24, 3, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0]";
    String headerSingle = "76, 1, 0, 0, 76, 65, -68, -20, 77, 116, 46, 83, 105, 110, 97, 105, 6, 0, 0, 0, -24, 3, 0, 0, 10, 0, 0, 0, 1, 0, 0, 0"; // 1 user
    // idx-2 0,1,2,3,4 @228
    // 219, 437
    // 219 idx-1
//    private static String[] headerOne = emptyAlbum.substring(1, emptyAlbum.length()-1).split(", ");
    private static String headerOne = emptyAlbum;
    private byte[] allFileVector;


//    FaceRepository faceRepo = (FaceRepository) faceRepository();

    public Tools() {
        Log.e(TAG, "Tools: 1");
        imageRepo = (ImageRepository) org.ei.opensrp.Context.imageRepository();
//        hash = retrieveHash(appContext.applicationContext());
    }

    public Tools(org.ei.opensrp.Context appContext) {
        imageRepo = (ImageRepository) org.ei.opensrp.Context.imageRepository();
        Tools.appContext = appContext;
//        helperImage = BitmapFactory.decodeResource( appContext.applicationContext().getResources(), R.drawable.h8);//ok
//        hash = retrieveHash(appContext.applicationContext());
    }


    public void setAlbumBuffer(String albumBuffer) {
        this.albumBuffer = albumBuffer;
    }

    public String getAlbumBuffer() {

        return albumBuffer;
    }

    /**
     * Method to Stored Bitmap as File and Buffer
     *
     * @param bitmap     Photo bitmap
     * @param entityId   Base user id
     * @param faceVector Vector from face
     * @param updated    capture mode
     * @return Boolean
     */
//    public static boolean WritePictureToFile(android.content.Context context, Bitmap bitmap, String entityId, byte[] faceVector, boolean updated) {
    public static boolean WritePictureToFile(Bitmap bitmap, String entityId, String[] faceVector, boolean updated) {

        File pictureFile = getOutputMediaFile(0, entityId);
        File thumbs_photo = getOutputMediaFile(1, entityId);

        if (pictureFile == null || thumbs_photo == null) {
            Log.e(TAG, "Error creating media file, check storage permissions!");
            return false;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.e(TAG, "Wrote image to " + pictureFile);

//            MediaScannerConnection.scanFile(context, new String[]{
//                            pictureFile.toString()}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String path, Uri uri) {
//                            Log.i("ExternalStorage", "Scanned " + path + ":");
//                            Log.i("ExternalStorage", "-> uri=" + uri);
//                        }
//                    });
            String photoPath = pictureFile.toString();
            Log.e(TAG, "Photo Path = " + photoPath);

//            Create Thumbs
            FileOutputStream tfos = new FileOutputStream(thumbs_photo);
            final int THUMBSIZE = FaceConstants.THUMBSIZE;

            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(photoPath),
                    THUMBSIZE, THUMBSIZE);
            ThumbImage.compress(Bitmap.CompressFormat.PNG, 100, tfos);
            tfos.close();
            Log.e(TAG, "Wrote Thumbs image to " + thumbs_photo);

//           FIXME File & Database Stored
//            saveStaticImageToDisk(entityId, ThumbImage, Arrays.toString(faceVector), updated);

            saveToDb(entityId, thumbs_photo.toString(), Arrays.toString(faceVector), updated);

            return true;

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return false;
    }

    private static void saveToDb(String entityId, String absoluteFileName, String faceVector, boolean updated) {

        Log.e(TAG, "saveToDb: " + "start");
        // insert into the db local
        if (!updated) {
            profileImage.setImageid(UUID.randomUUID().toString());
            profileImage.setAnmId(anmId);
            profileImage.setEntityID(entityId);
            profileImage.setContenttype("jpeg");
            profileImage.setFilepath(absoluteFileName);
            profileImage.setFilecategory("profilepic");
            profileImage.setFilevector(faceVector);
            profileImage.setSyncStatus(ImageRepository.TYPE_Unsynced);

            imageRepo.add(profileImage, entityId);
        } else {
            imageRepo.updateByEntityId(entityId, faceVector);
        }
        Log.e(TAG, "saveToDb: " + "done");

    }

    /**
     * Method create new file
     *
     * @param mode     capture mode.
     * @param entityId Base user id.
     * @return File
     */
    @Nullable
    private static File getOutputMediaFile(Integer mode, String entityId) {
        // Mode 0 = Original
        // Mode 1 = Thumbs

        // Location use app_dir
        String imgFolder = (mode == 0) ? DrishtiApplication.getAppDir() :
                DrishtiApplication.getAppDir() + File.separator + ".thumbs";
//        String imgFolder = (mode == 0) ? "OPENSRP_SID":"OPENSRP_SID"+File.separator+".thumbs";
//        File mediaStorageDir = new File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), imgFolder);
        File mediaStorageDir = new File(imgFolder);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            Log.e(TAG, "failed to find directory " + mediaStorageDir.getAbsolutePath());
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "Created new directory " + mediaStorageDir.getAbsolutePath());
                return null;
            }
        }

        // Create a media file name
        return new File(String.format("%s%s%s.jpg", mediaStorageDir.getPath(), File.separator, entityId));
    }

    /**
     * Methof for Draw Information of existing Person
     *
     * @param rect          Rectangular
     * @param mutableBitmap Bitmap
     * @param pixelDensity  Pixel group area
     * @param personName    name
     */
    public static void drawInfo(Rect rect, Bitmap mutableBitmap, float pixelDensity, String personName) {
//        Log.e(TAG, "drawInfo: rect " + rect);
//        Log.e(TAG, "drawInfo: bitmap" + mutableBitmap);

        // Extra padding around the faceRects
        rect.set(rect.left -= 20, rect.top -= 20, rect.right += 20, rect.bottom += 20);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paintForRectFill = new Paint();

        // Draw rect fill
        paintForRectFill.setStyle(Paint.Style.FILL);
        paintForRectFill.setColor(Color.WHITE);
        paintForRectFill.setAlpha(80);

        // Draw rectangular strokes
        Paint paintForRectStroke = new Paint();
        paintForRectStroke.setStyle(Paint.Style.STROKE);
        paintForRectStroke.setColor(Color.GREEN);
        paintForRectStroke.setStrokeWidth(5);
        canvas.drawRect(rect, paintForRectFill);
        canvas.drawRect(rect, paintForRectStroke);

//        float pixelDensity = getResources().getDisplayMetrics().density;
        int textSize = (int) (rect.width() / 25 * pixelDensity);

        Paint paintForText = new Paint();
        Paint paintForTextBackground = new Paint();
        Typeface tp = Typeface.SERIF;
        Rect backgroundRect = new Rect(rect.left, rect.bottom, rect.right, (rect.bottom + textSize));

        paintForText.setColor(Color.WHITE);
        paintForText.setTextSize(textSize);
        paintForTextBackground.setStyle(Paint.Style.FILL);
        paintForTextBackground.setColor(Color.BLACK);
        paintForText.setTypeface(tp);
        paintForTextBackground.setAlpha(80);

        if (personName != null) {
            canvas.drawRect(backgroundRect, paintForTextBackground);
            canvas.drawText(personName, rect.left, rect.bottom + (textSize), paintForText);
        } else {
            canvas.drawRect(backgroundRect, paintForTextBackground);
            canvas.drawText("Not identified", rect.left, rect.bottom + (textSize), paintForText);
        }

//        confirmationView.setImageBitmap(mutableBitmap);

    }

    /**
     * Draw Area that detected as Face
     *
     * @param rect          Rectangular
     * @param mutableBitmap Modified Bitmap
     * @param pixelDensity  Pixel area density
     */
    public static void drawRectFace(Rect rect, Bitmap mutableBitmap, float pixelDensity) {

        Log.e(TAG, "drawRectFace: rect " + rect);
        Log.e(TAG, "drawRectFace: bitmap " + mutableBitmap);
        Log.e(TAG, "drawRectFace: pixelDensity " + pixelDensity);

        // Extra padding around the faceRects
        rect.set(rect.left -= 20, rect.top -= 20, rect.right += 20, rect.bottom += 20);
        Canvas canvas = new Canvas(mutableBitmap);

        // Draw rect fill
        Paint paintForRectFill = new Paint();
        paintForRectFill.setStyle(Paint.Style.FILL);
        paintForRectFill.setColor(Color.WHITE);
        paintForRectFill.setAlpha(80);

        // Draw rect strokes
        Paint paintForRectStroke = new Paint();
        paintForRectStroke.setStyle(Paint.Style.STROKE);
        paintForRectStroke.setColor(Color.GREEN);
        paintForRectStroke.setStrokeWidth(5);

        // Draw Face detected Area
        canvas.drawRect(rect, paintForRectFill);
        canvas.drawRect(rect, paintForRectStroke);

    }

    /**
     * Stored list detected Base entity ID to Shared Preference for buffered
     *
     * @param hashMap HashMap
     * @param context context
     */
    public static void saveHash(HashMap<String, String> hashMap, android.content.Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.HASH_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
//        Log.e(TAG, "Hash Save Size = " + hashMap.size());
        for (String s : hashMap.keySet()) {
//            Log.e(TAG, "saveHash: " + s);
            editor.putString(s, hashMap.get(s));
        }
        editor.apply();
    }

    /**
     * Get Existing Hash
     *
     * @param context Context
     * @return hash
     */
    public static HashMap<String, String> retrieveHash(android.content.Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.HASH_NAME, 0);
        HashMap<String, String> hash = new HashMap<>();
        hash.putAll((Map<? extends String, ? extends String>) settings.getAll());
        return hash;
    }

    /**
     * Save Vector array to xml
     */
    public static void saveAlbum(String albumBuffer, android.content.Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.ALBUM_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(FaceConstants.ALBUM_ARRAY, albumBuffer);
        editor.apply();
    }

    public static void loadAlbum(android.content.Context context) {

        SharedPreferences settings = context.getSharedPreferences(FaceConstants.ALBUM_NAME, 0);
        String arrayOfString = settings.getString(FaceConstants.ALBUM_ARRAY, null);
        byte[] albumArray;

        if (arrayOfString != null) {

            splitStringArray = arrayOfString.substring(1, arrayOfString.length() - 1).split(", ");

            albumArray = new byte[splitStringArray.length];


            for (int i = 0; i < splitStringArray.length; i++) {
                albumArray[i] = Byte.parseByte(splitStringArray[i]);
            }

            boolean result = SmartShutterActivity.faceProc.deserializeRecognitionAlbum(albumArray);

            if (result) Log.e(TAG, "loadAlbum: "+"Succes" );

        } else {
            Log.e(TAG, "loadAlbum: " + "is it your first record ? if no, there is problem happen.");
        }
    }

    public static void alertDialog(android.content.Context context, int opt) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        Tools tools = new Tools();
//        alertDialog.setMessage(message);
        String message = "";
        switch (opt) {
            case 0:
                message = "Are you sure to empty The Album?";
//                doEmpty;
                break;
            case 1:
                message = "Are you sure to delete item";
                break;
            default:
                break;
        }
        alertDialog.setMessage(message);
//        alertDialog.setButton("OK", do);
        alertDialog.setPositiveButton("ERASE", tools.doEmpty);
        alertDialog.show();
    }

    private DialogInterface.OnClickListener doEmpty = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            boolean result = SmartShutterActivity.faceProc.resetAlbum();
            if (result) {
//                HashMap<String, String> hashMap = SmartShutterActivity.retrieveHash(getApplicationContext());
//                HashMap<String, String> hashMap = retrieveHash(getApplicationContext());
//                HashMap<String, String> hashMap = retrieveHash();
//                hashMap.clear();
//                SmartShutterActivity ss = new SmartShutterActivity();
//                saveHash(hashMap, getApplicationContext());
//                saveAlbum();
//                Toast.makeText(getApplicationContext(),
//                        "Album Reset Successful.",
//                        Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(
//                        getApplicationContext(),
//                        "Internal Error. Reset album failed",
//                        Toast.LENGTH_LONG).show();
            }
        }
    };

    public void resetAlbum() {

        Log.e(TAG, "resetAlbum: " + "start");
        boolean result = SmartShutterActivity.faceProc.resetAlbum();

        if (result) {
            // Clear data
            // TODO: Null getApplication COntext
//            HashMap<String, String> hashMap = SmartShutterActivity.retrieveHash(new ClientsList().getApplicationContext());
            HashMap<String, String> hashMap = SmartShutterActivity.retrieveHash(appContext.applicationContext().getApplicationContext());
            hashMap.clear();
//            saveHash(hashMap, cl.getApplicationContext());
            saveHash(hashMap, appContext.applicationContext().getApplicationContext());
//            saveAlbum();

//            Toast.makeText(cl.getApplicationContext(), "Reset Succesfully done!", Toast.LENGTH_LONG).show();
            Toast.makeText(appContext.applicationContext().getApplicationContext(), "Reset Succesfully done!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(appContext.applicationContext().getApplicationContext(), "Reset Failed!", Toast.LENGTH_LONG).show();

        }
        Log.e(TAG, "resetAlbum: " + "finish");
    }

    /**
     * Fetch data from API (json
     */
    public static void setVectorfromAPI(final android.content.Context context) {
//        AllSharedPreferences allSharedPreferences;

        String DRISTHI_BASE_URL = appContext.configuration().dristhiBaseURL();
        String user = appContext.allSharedPreferences().fetchRegisteredANM();
        String location = appContext.allSharedPreferences().getPreference("locationId");
        String pwd = appContext.allSettings().fetchANMPassword();
        //TODO : cange to based locationId
//        String api_url = DRISTHI_BASE_URL + "/multimedia-file?anm-id=" + user;
        final String api_url = DRISTHI_BASE_URL + "/multimedia-file?locationid=" + location;

        AsyncHttpClient client = new AsyncHttpClient();

        client.setBasicAuth(user, pwd);

//        client.get(api_url, new JsonHttpResponseHandler(){
//        });

        client.get(api_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(TAG, "onSuccess: " + statusCode);
                insertOrUpdate(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "onFailure: " + api_url);
            }
        });


    }

    private static void insertOrUpdate(byte[] responseBody) {

        try {
            JSONArray response = new JSONArray(new String(responseBody));

            for (int i = 0; i < response.length(); i++) {
                JSONObject data = response.getJSONObject(i);

                String uid = data.getString("caseId");
                String anmId = data.getString("providerId");
//                        String uid = data.getString("caseId");

                // To AlbumArray
                String faceVector = data.getJSONObject("attributes").getString("faceVector");

                // Update Table ImageList on existing record based on entityId where faceVector== null
                ProfileImage profileImage = new ProfileImage();
//                profileImage.setImageid(UUID.randomUUID().toString());
                // TODO : get anmID from ?
                profileImage.setAnmId(anmId);
                profileImage.setEntityID(uid);
//                profileImage.setFilepath(null);
//                profileImage.setFilecategory("profilepic");
//                profileImage.setSyncStatus(ImageRepository.TYPE_Synced);

                // TODO : fetch vector from imagebitmap
                profileImage.setFilevector(faceVector);

                imageRepo.createOrUpdate(profileImage, uid);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to Parse String
     *
     * @param arrayOfString
     * @return
     */
    private String[] parseArray(String arrayOfString) {

        return arrayOfString.substring(1,
                arrayOfString.length() - 1).split(", ");
    }

    /**
     * Save to Buffer from Local DB
     *
     * @param context
     */

    public static void saveAndClose(android.content.Context context, String entityId, boolean updated,
                                    FacialProcessing objFace, int arrayPossition,
                                    Bitmap storedBitmap, String className) {

        byte[] faceVector;

        if (!updated) {

//            Log.e(TAG, "saveAndClose: "+ "updated : false" );
            int result = objFace.addPerson(arrayPossition);
            faceVector = objFace.serializeRecogntionAlbum();

//            Log.e(TAG, "saveAndClose: length "+ faceVector.length ); // 32
//
//            Log.e(TAG, "saveAndClose: " + result);

            hash = retrieveHash(context);

            hash.put(entityId, Integer.toString(result));
//
            // Save Hash
            saveHash(hash, context);

//        byte[] albumBuffer = SmartShutterActivity.faceProc.serializeRecogntionAlbum();

            Log.e(TAG, "saveAndClose: " + faceVector.length);

            saveAlbum(Arrays.toString(faceVector), context);

            String albumBufferArr = Arrays.toString(faceVector);

            String[] faceVectorContent = albumBufferArr.substring(1, albumBufferArr.length() - 1).split(", ");

            // Get Face Vector Contnt Only by removing Header
            faceVectorContent = Arrays.copyOfRange(faceVectorContent, faceVector.length - 300, faceVector.length);

            WritePictureToFile(storedBitmap, entityId, faceVectorContent, updated);

            // Reset Album to get Single Face Vector
//            SmartShutterActivity.faceProc.resetAlbum();

        } else {

            int update_result = objFace.updatePerson(Integer.parseInt(hash.get(entityId)), 0);

            if (update_result == 0) {

                Log.e(TAG, "saveAndClose: " + "success");

            } else {

                Log.e(TAG, "saveAndClose: " + "Maximum Reached Limit for Face");

            }

            faceVector = objFace.serializeRecogntionAlbum();

            // TODO : update only face vector
            saveAlbum(Arrays.toString(faceVector), context);
        }

        new ImageConfirmation().finish();

        Class<?> origin_class = null;

        if(className.equals(GiziDetailActivity.class.getSimpleName())){
            origin_class = GiziDetailActivity.class;
        }

        Intent resultIntent = new Intent(appContext.applicationContext(), origin_class);
//        setResult(RESULT_OK, resultIntent);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        appContext.applicationContext().startActivity(resultIntent);

        Log.e(TAG, "saveAndClose: " + "end");
    }

    public static void setVectorsBuffered() {

        List<ProfileImage> vectorList = imageRepo.getAllVectorImages();

        if (vectorList.size() != 0) {

            hash = retrieveHash(appContext.applicationContext().getApplicationContext());
//            Log.e(TAG, "setVectorsBuffered: hash size " + hash.size());

            String[] albumBuffered = new String[0];

            int i = 0;
            for (ProfileImage profileImage : vectorList) {
                String[] vectorFace = new String[]{};
                if (profileImage.getFilevector() != null) {

                    vectorFace = profileImage.getFilevector().substring(1, profileImage.getFilevector().length() - 1).split(", ");
                    vectorFace[0] = String.valueOf(i);


                    albumBuffered = ArrayUtils.addAll(albumBuffered, vectorFace);
                    hash.put(profileImage.getEntityID(), String.valueOf(i));

                } else {
                    Log.e(TAG, "setVectorsBuffered: Profile Image Null");
                }
//                Log.e(TAG, "setVectorsBuffered: "+ i +" - "+ vectorFace.length);
                i++;

            }

            albumBuffered = ArrayUtils.addAll(getHeaderBaseUserCount(vectorList.size()), albumBuffered);

//            Log.e(TAG, "setVectorsBuffered: hash size" + hash.size() + " album size "+ albumBuffered.length);
            saveAlbum(Arrays.toString(albumBuffered), appContext.applicationContext());
            saveHash(hash, appContext.applicationContext());

        } else {
            Log.e(TAG, "setVectorsBuffered: "+ "Multimedia Table Not ready" );
        }

    }

    private static String[] getHeaderBaseUserCount(int i) {
        String headerNew = imageRepo.findByUserCount(i);
        return headerNew.substring(1, headerNew.length() -1).split(", ");
    }

    /**
     * Stored from Local captured
     *
     * @param entityId      Base entity Id
     * @param image         Photo image
     * @param contentVector Face vector
     * @param updated       Boolean updated mode
     */
    public static void saveStaticImageToDisk(String entityId, Bitmap image, String contentVector, boolean updated) {
        String anmId = Context.getInstance().allSharedPreferences().fetchRegisteredANM();

        String[] res = contentVector.substring(1, contentVector.length() - 1).split(",");

        Log.e(TAG, "saveStaticImageToDisk: " + res.length);
        String[] faceVector = Arrays.copyOfRange(res, 32, 332);
        Log.e(TAG, "saveStaticImageToDisk: " + faceVector.length);

        if (image != null) {
            OutputStream os = null;
            try {

                if (entityId != null && !entityId.isEmpty()) {
                    final String absoluteFileName = DrishtiApplication.getAppDir() + File.separator + entityId + ".JPEG";

                    File outputFile = new File(absoluteFileName);
                    os = new FileOutputStream(outputFile);
                    Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                    if (compressFormat != null) {
                        image.compress(compressFormat, 100, os);
                    } else {
                        throw new IllegalArgumentException(
                                "Failed to save static image, could not retrieve image compression format from name "
                                        + absoluteFileName);
                    }

                    // insert into the db local
                    ProfileImage profileImage = new ProfileImage();

                    profileImage.setImageid(UUID.randomUUID().toString());
                    profileImage.setAnmId(anmId);
                    profileImage.setEntityID(entityId);
                    profileImage.setContenttype("jpeg");
                    profileImage.setFilepath(absoluteFileName);
                    profileImage.setFilecategory("profilepic");
                    profileImage.setFilevector(Arrays.toString(faceVector));
                    profileImage.setSyncStatus(ImageRepository.TYPE_Unsynced);

                    ImageRepository imageRepo = (ImageRepository) org.ei.opensrp.Context.imageRepository();
                    imageRepo.add(profileImage, entityId);
                }

            } catch (FileNotFoundException e) {
                Log.e(TAG, "Failed to save static image to disk");
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to close static images output stream after attempting to write image");
                    }
                }
            }
        }
    }

    public void parseSavedVector(Context context) {

        hash = retrieveHash(context.applicationContext());

//        list = imageRepo.allVectorImages();
        list = imageRepo.getAllVectorImages();
//        Helper Face
//        Bitmap helperImage = BitmapFactory.decodeResource( context.applicationContext().getResources(), R.drawable.h9);//OK

//        Log.e(TAG, "parseSavedVector: "+ Arrays.toString(list.toArray()));
//        Log.e(TAG, "parseSavedVector: "+ list.size() );

//        for (ProfileImage pi : list){
//            Log.e(TAG, "parseSavedVector: "+pi.getEntityID() );
//        }
//
        if (list.size() != 0) {
            if (SmartShutterActivity.faceProc == null) {
                SmartShutterActivity.faceProc = FacialProcessing.getInstance();
            }

            FacialProcessing objFace = SmartShutterActivity.faceProc;

            boolean resSetBitmap = objFace.setBitmap(helperImage);

            FaceData[] faceData = objFace.getFaceData();
            byte[] initContentBuffered = null;

            if (resSetBitmap) {
//                Log.e(TAG, "parseSavedVector: before"+ Arrays.toString(objFace.serializeRecogntionAlbum()));
                resetAlbum();

                for (int i = 0; i < list.size(); i++) {


                    Log.e(TAG, "loadAlbum: start" + i);
                    loadAlbum(context.applicationContext().getApplicationContext());
                    Log.e(TAG, "parseSavedVector: after " + i + " - " + Arrays.toString(objFace.serializeRecogntionAlbum()));

                    // Get vector fromm Existing buffer
                    byte[] initBuffered = objFace.serializeRecogntionAlbum();
                    Log.e(TAG, "parseSavedVector: initBuffered" + initBuffered.length);

                    // Get Init Vector Content
                    initContentBuffered = Arrays.copyOfRange(initBuffered, 32, initBuffered.length);

                    String fileVector = list.get(i).getFilevector();
                    String uid = list.get(i).getEntityID();


                    // FIXME : ADDED PERSON TO GET NEW HEADER
                    objFace.addPerson(0); // adding new vector of helper image, length and content has changed

                    byte[] newBuffered = objFace.serializeRecogntionAlbum();

                    Log.e(TAG, "parseSavedVector: len after add new " + newBuffered.length);

                    String albumBufferArr = Arrays.toString(newBuffered);

                    // Remove Last 300th byte[] array ( Helper Vector Content), copy new Array of header:
                    // NEW HEADER + CONTENT
                    byte[] tempHeaderBuffered = Arrays.copyOfRange(newBuffered, 0, newBuffered.length - 300);

                    Log.e(TAG, "parseSavedVector: len new " + newBuffered.length);
                    Log.e(TAG, "parseSavedVector: len temp " + tempHeaderBuffered.length);

                    Log.e(TAG, "parseSavedVector: len new byte " + Arrays.toString(newBuffered));
                    Log.e(TAG, "parseSavedVector: len temp byte " + Arrays.toString(tempHeaderBuffered));

                    byte[] tempAddBuffered = null;

                    if (fileVector != null) {
//                        parseArray(fileVector);

                        String[] splitStringArray = fileVector.substring(1,
                                fileVector.length() - 1).split(", ");

                        byte[] tempFileVector = new byte[splitStringArray.length];
                        Log.e(TAG, "parseSavedVector: Parsing Data from DB len" + splitStringArray.length);
                        for (int j = 0; j < splitStringArray.length; j++) {
                            tempFileVector[j] = Byte.parseByte(splitStringArray[j].trim());
                        }


                        // Vector = Recent Number of Vector Header + DB content
                        // TODO
//                        tempAddBuffered = ArrayUtils.addAll(tempBuffered, tempFileVector );
                        tempAddBuffered = ArrayUtils.addAll(initContentBuffered, tempFileVector);
//                        tempAddBuffered = ArrayUtils.addAll(ArrayUtils.addAll(), tempFileVector );
                        Log.e(TAG, "parseSavedVector: initcontent " + initContentBuffered.length);
                        Log.e(TAG, "parseSavedVector: konten semuanya " + tempAddBuffered.length);


                        // OK
                        tempFileVector[0] = Byte.parseByte(String.valueOf(i));
                        allFileVector = ArrayUtils.addAll(allFileVector, tempFileVector);
                        Log.e(TAG, "parseSavedVector: length " + allFileVector.length);

                    } else {
                        Log.e(TAG, "parseSavedVector: Filevector Null");
                    }


                    tempAddBuffered = ArrayUtils.addAll(tempHeaderBuffered, tempAddBuffered);

                    Log.e(TAG, "parseSavedVector: " + hash.size());
                    if (!hash.containsKey(uid)) {
                        hash.put(uid, String.valueOf(i));
                        saveHash(hash, appContext.applicationContext().getApplicationContext());

                        saveAlbum(Arrays.toString(tempAddBuffered), appContext.applicationContext().getApplicationContext());

//                    tempAddBuffered = ArrayUtils.addAll(tempAddBuffered, tempAddBuffered, tempAddBuffered );

                        Log.e(TAG, "parseSavedVector: hash tempBuffer len of " + uid + " - " + tempAddBuffered.length);

                    } else {
                        Log.e(TAG, "parseSavedVector: hash " + "Already Recorded");
                    }

//                    int offset = 300;
//                    byte[] tempBuffered =  Arrays.copyOfRange(newBuffered, offset, newBuffered.length);

                    String[] newBufferedVector = albumBufferArr.substring(1, albumBufferArr.length() - 1).split(", ");

                    // Get Face Vector Content Only by removing Header
                    newBufferedVector = Arrays.copyOfRange(newBufferedVector, newBuffered.length - 300, newBuffered.length);

//                    String[] albumBufferArr = ArrayUtils.addAll(oldBuffer, faceVector);


                    Log.e(TAG, "parseSavedVector: after add " + Arrays.toString(objFace.serializeRecogntionAlbum()));

                    SmartShutterActivity.faceProc.resetAlbum();
//                    Log.e(TAG, "parseSavedVector: after reset " + Arrays.toString(objFace.serializeRecogntionAlbum()));

                } // End for loop local db

            } else {
                Log.e(TAG, "parseSavedVector: " + "setBitmap helper failed!.");
            }

            // TODO : initialize start by checking HashMap

        }

    }



}
