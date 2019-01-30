package com.edu.flinnt.util;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Opens and display media on screen
 */
public class MediaHelper {

    /**
     * Opens and display image
     *
     * @param filename image name
     * @param context  activity context
     */
    public static void showImage(String filename, Context context) {
        try {
            String url = filename;
            String FileName = url.substring(url.lastIndexOf("/") + 1);
            String FolderName = url.replace(FileName, "");
            File file = new File(FolderName, FileName);
            if (file.exists()) {
                //String extension = MimeTypeMap.getFileExtensionFromUrl(OutPutFile);
                //String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

			/*
             * Uri uri = Uri.parse("/mnt/sdcard/" + con.getString(
			 * R.string.iconncetfolder) + "/" + TypeFolder + "/" + FileName ) ;
			 * Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			 * intent.setDataAndType(uri, "image/*");
			 */

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                // File file = new File("/sdcard/test.mp4");
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                // startActivity(intent);

                context.startActivity(intent);
            } else {
                // File not found
            }
        }
        catch (Exception e) {

        }
    }

    /**
     * Opens and play video
     *
     * @param filename video name
     * @param context  activity context
     */
    public static void showVideo(String filename, Context context, Common common) {
        try {
            String url = filename;

            //comm.LOG1(4, MyClassName + "Flinnt Play audio", url);
            String a[];
            a = url.split("/");

            String FileName = a[a.length - 1];
            String FolderName = url.replace(FileName, "");
            //comm.LOG1(4, MyClassName + "Flinnt Play audio 1", "File found "                + UploadFileName);
            File file = new File(FolderName, FileName);
            if (file.exists()) {

			/*
			 * Uri uri = Uri.parse(MyAllVal[9] + con.getString(
			 * R.string.iconncetfolder) + "/audio/" + FileName) ; Intent intent
			 * = new Intent(Intent.ACTION_VIEW, uri); intent.setDataAndType(uri,
			 * "video/mp3"); con.startActivity(intent);
			 */
                if (filename.matches("") == false) {
                    Intent intent = new Intent();
                    intent = common.PlayAudio(filename, context);

                    context.startActivity(intent);

                } else {
                    // comm.ShowMessage(activity, "file not found "
                    // +UploadFileName);
                }
			/*
			 * Intent intent = new Intent();
			 *
			 * intent =comm.PlayAudio("/mnt/sdcard/" + con.getString(
			 * R.string.iconncetfolder) + "/audio/" + FileName,con);
			 *
			 * con.startActivity(intent);
			 */
            } else {

            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No App available to show this file", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Opens and play audio
     *
     * @param filename audio file name
     * @param context  activity context
     */
    public static void showAudio(String filename, Context context, Common common) {
        try {

            // SHOW LOCAL PATH audio
            // PlayVideo(v.getTag().toString());
            String url = filename;
            //comm.LOG1(4, MyClassName + "Flinnt Play Video", url);
            int ContentType = 0;
            if (ContentType == 1) // you tube
            {
                //comm.LOG1(4, MyClassName + "play youtube video", url);
                // comm.PlayYoutube(url, con);
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } else {

                //comm.LOG1(4, MyClassName + "Start downloading  mp4 video", url);

                String a[];
                a = url.split("/");

                String FileName = a[a.length - 1];
                String FolderName = url.replace("/" + FileName, "");
                File file = new File(FolderName + "", FileName);
                if (file.exists()) {
                    //comm.LOG1(4, MyClassName + "Flinnt Play video 1",                            "File found " + FileName);

					/*
					 * Uri uri = Uri.parse("/mnt/sdcard/" + con.getString(
					 * R.string.iconncetfolder) + "/" + MyConfig.VIDEO + "/" +
					 * FileName) ; Intent intent = new
					 * Intent(Intent.ACTION_VIEW, uri);
					 * intent.setDataAndType(uri, "video/mp3");
					 * con.startActivity(intent);
					 */
                    Intent intent = new Intent();
                    filename = filename.trim();
                    intent = common.PlayAudio(filename, context);

                    context.startActivity(intent);

                } else {

                }

            }

        } catch (Exception e) {

        }
    }

    /**
     * Opens and display document
     *
     * @param filename document file name
     * @param context  activity context
     */
    public static void showDocument(String filename, Context context) {
        // comm.ShowMessage(details.this, FileName);
        String extension = MimeTypeMap.getFileExtensionFromUrl(filename);
        extension = extension.toLowerCase();

        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("showDocument :: extension : " + extension + " , mimeType : " + mimeType);

        if (!TextUtils.isEmpty(mimeType)) {
            File file = new File(filename);
        /*PackageManager packageManager = context.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");*/
            // List list = packageManager.queryIntentActivities(testIntent,
            // PackageManager.MATCH_DEFAULT_ONLY);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, mimeType);
            //intent.setDataAndType(uri, "application/msword");
            // Verify that the intent will resolve to an activity
            //context.startActivity(intent);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Helper.showToast("No App available to show this file", Toast.LENGTH_LONG);
            }

        } else {
            Helper.showToast("No App available to show this file", Toast.LENGTH_LONG);
        }

    }


    public static void ShowLink(String link, Common common) {
        common.OpenLink(link);

    }


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = (Build.VERSION.SDK_INT >= 19); //Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && android.provider.DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = android.provider.DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = android.provider.DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = android.provider.DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }

    }
}
