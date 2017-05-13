package com.example.jeka.exampledrawerbar.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import com.example.jeka.exampledrawerbar.model.OpenWeatherFetch;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IconDownloader<T> extends HandlerThread{
    private static final String TAG = "IconDownloader";
    private static final String ICON_URL = "http://openweathermap.org/img/w/";
    private static final String IMAGE_FORMAT = ".png";
    private static final int MESSAGE_DOWNLOAD = 0;

    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private IconDownloadListener<T> mIconDownloadListener;
    private LruCache mLruCache;

    public interface IconDownloadListener<T>{
        void onIconDownloaded(T target, Bitmap icon);
    }

    public void setIconDownloadListener(IconDownloadListener<T> listener){
        mIconDownloadListener = listener;
    }

    public IconDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mLruCache = new LruCache(cacheSize);
    }

    @Override
    protected void onLooperPrepared(){
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message message){
                if (message.what == MESSAGE_DOWNLOAD){
                    T target = (T) message.obj;
                    Log.i(TAG, "Got a request for URL: " +
                                                mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    public void queueIcon(T target, String url){
        Log.i(TAG, "Got a URL: " + url);

        if (url == null){
            mRequestMap.remove(target);
        }else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    public void clearQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    public void handleRequest(final T target){
        try {
            final String url = mRequestMap.get(target);
            if (url == null){
                return;
            }


            byte[] bitmapBytes;
            final Bitmap bitmap;
            if (mLruCache.get(url) != null){
                bitmap = (Bitmap) mLruCache.get(url);
                Log.i(TAG, "LRUCache load image");
            }else {
                bitmapBytes = new OpenWeatherFetch().getUrlBytes(new StringBuffer()
                        .append(ICON_URL)
                        .append(url)
                        .append(IMAGE_FORMAT).toString());
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                mLruCache.put(url, bitmap);
                Log.i(TAG, "Image downloaded and put in cache");
            }

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mRequestMap.get(target) != url){
                        return;
                    }
                    mRequestMap.remove(target);
                    mIconDownloadListener.onIconDownloaded(target, bitmap);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error downloading image", e);
        }
    }
}
