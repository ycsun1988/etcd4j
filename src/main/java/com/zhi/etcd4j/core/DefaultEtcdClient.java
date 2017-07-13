package com.zhi.etcd4j.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhi.etcd4j.EtcdClient;
import com.zhi.etcd4j.exception.ConnectException;
import com.zhi.etcd4j.exception.EtcdException;
import com.zhi.etcd4j.exception.KeyNotFoundException;
import com.zhi.etcd4j.util.OkHttpUtil;
import com.zhi.etcd4j.util.StringUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/5.
 *         TODO add Atomically Creating In-Order Keys API
 *         TODO add Statistic API
 */
public class DefaultEtcdClient implements EtcdClient {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultEtcdClient.class);
    private static final OkHttpClient HTTP_CLIENT = OkHttpUtil.buildHttpClient();
    private static final Gson GSON = new GsonBuilder().create();
    private static final MessageParser MESSAGE_PARSER = new MessageParser(GSON);
    private static final String URI_SUFFIX = "/v2/keys";
    private static final int DEFAULT_PORT = 3379;
    private static final String HTTP_SCHEME = "http://";
    private static final String COLON_STR = ":";
    private static final String KEY_OF_VALUE = "value";
    private static final String KEY_OF_TTL = "ttl";
    public static final String KEY_OF_PREVEXIST = "prevExist";
    public static final String KEY_OF_PREVINDEX = "prevIndex";
    public static final String KEY_OF_PREVVALUE = "prevValue";
    private static final String KEY_OF_DIR = "dir";
    private static final String KEY_OF_RECURSIVE = "recursive";
    private static final String KEY_OF_WAIT = "wait";
    private static final String KEY_OF_WAITINDEX = "waitIndex";
    private String baseUri;

    /**
     * Instantiate DefaultEtcdClient
     *
     * @param uri Etcd connection uri, the follow uri format are available.
     *            1. http://hostname:port or http://hostname which will use default port.
     *            2. http://ip:port or http://ip which will use default port.
     *            3. ip:port or ip which will use default port.
     *            4. hostname:port or hostname which will use default port.
     */
    public DefaultEtcdClient(String uri) {
        this(uri, DEFAULT_PORT);
    }

    /**
     * Instantiate DefaultEtcdClient
     *
     * @param uri  Etcd connection uri, the follow uri format are available.
     *             1. http://hostname:port or http://hostname which will use default port.
     *             2. http://ip:port or http://ip which will use default port.
     *             3. ip:port or ip which will use default port.
     *             4. hostname:port or hostname which will use default port.
     * @param port Etcd's listening client port
     *             note: if uri contains the port then this argument will be ignored.
     */
    public DefaultEtcdClient(String uri, int port) {
        StringUtil.assertEmpty(uri, "Instantiate DefaultEtcdClient error, uri [" + uri + "]");
        //add http scheme if necessary.
        if (!uri.startsWith(HTTP_SCHEME)) {
            uri = HTTP_SCHEME + uri;
        }
        //if uri not contains port then port will be used.
        if (!uri.matches(".*:\\d{4,5}$")) {
            if (port < 1025 || port > 49151) {
                port = DEFAULT_PORT;
            }
            uri = uri + COLON_STR + port;
        }
        //add uri suffix to uri.
        baseUri = uri + URI_SUFFIX;
        LOG.debug("BaseUri is [{}]", baseUri);
    }

    /**
     * Get the value of a key
     * note: if the key is dir, null value will returned.
     *
     * @param key the key
     * @return operation result
     * @throws EtcdException exception during get the value of a key
     */
    public EtcdResult get(String key) throws EtcdException {
        StringUtil.assertEmpty(key, "Illegal key: [{" + key + "}]");
        String requestUrl = OkHttpUtil.buildRequestUrl(baseUri, key, null);
        Request getReq = new Request.Builder().url(requestUrl).build();
        return syncExecute(getReq);
    }

    /**
     * Delete a key
     *
     * @param key the key to be deleted.
     * @return operation result
     * @throws EtcdException exception during delete the key
     */
    public EtcdResult delete(String key) throws EtcdException {
        StringUtil.assertEmpty(key, "Illegal key: [" + key + "]");
        String requestUrl = OkHttpUtil.buildRequestUrl(baseUri, key, null);
        Request delReq = new Request.Builder()
                .url(requestUrl)
                .delete()
                .build();
        return syncExecute(delReq);
    }

    /**
     * Set the value of a key
     *
     * @param key   the key
     * @param value the key's value
     * @return operation result
     * @throws EtcdException exception during set the value of a key
     */
    public EtcdResult set(String key, String value) throws EtcdException {
        StringUtil.assertEmpty(key, "Illegal key: [" + key + "]");
        StringUtil.assertEmpty(value, "Illegal value: [" + value + "]");
        return set(key, value, -1, false);
    }

    /**
     * Set the value of a key
     * note: if key's ttl less than 0, the ttl parameter will be ignored. that's to say the key will never expired.
     *
     * @param key   the key
     * @param value the key's value
     * @param ttl   time to live of the key in seconds.
     * @return operation result
     * @throws EtcdException exception during set the value of a key
     */
    public EtcdResult set(String key, String value, int ttl) throws EtcdException {
        return set(key, value, ttl, false);
    }

    /**
     * Set the value of a key
     * note: if preExist is true, than the key will overwrite the pre key.
     *
     * @param key       the key
     * @param value     the key's value
     * @param ttl       time to live of the key
     * @param prevExist exists before
     * @return operation result
     * @throws EtcdException exception during set the value of a key
     */
    public EtcdResult set(String key, String value, int ttl, boolean prevExist) throws EtcdException {
        StringUtil.assertEmpty(key, "Illegal key: [" + key + "]");
        StringUtil.assertEmpty(value, "Illegal key: [" + value + "]");
        Map<String, String> data = new HashMap<String, String>(3);
        data.put(KEY_OF_VALUE, value);
        if (ttl > 0) {
            data.put(KEY_OF_TTL, String.valueOf(ttl));
        }
        if (prevExist) {
            data.put(KEY_OF_PREVEXIST, String.valueOf(true));
        }
        return put(key, data, null);
    }

    /**
     * TTL a exist key
     * note: the key can also be a dir key.
     *
     * @param key the key which can also be a directory key.
     * @param ttl time to live of the key in seconds. -1 represents unset the ttl
     * @return operation result
     * @throws EtcdException exception during ttl the key
     */
    public EtcdResult ttl(String key, int ttl) throws EtcdException {
        StringUtil.assertEmpty(key, "Illegal key: [" + key + "]");
        Map<String, String> data = new HashMap<String, String>(3);
        data.put(KEY_OF_PREVEXIST, String.valueOf(true));
        if (ttl < 0) {
            data.put(KEY_OF_TTL, "");
        } else {
            data.put(KEY_OF_TTL, String.valueOf(ttl));
        }
        return put(key, data, null);
    }

    /**
     * Create a dir
     *
     * @param key the dir to be created.
     * @return operation result
     * @throws EtcdException exception during create the dir
     */
    public EtcdResult createDir(String key) throws EtcdException {
        return createDir(key, -1);
    }

    /**
     * Create a dir with optional ttl
     * note: if the ttl less than 0, than
     *
     * @param key the dir to be created.
     * @param ttl time to live of the dir in seconds
     * @return operation result
     * @throws EtcdException exception during create the dir
     */
    public EtcdResult createDir(String key, int ttl) throws EtcdException {
        return createDir(key, ttl, false);
    }

    /**
     * Create dir
     *
     * @param key       the dir key
     * @param ttl       time to live of the key in seconds.
     * @param prevExist exists before
     * @return operation result
     * @throws EtcdException exception during create dir.
     */
    public EtcdResult createDir(String key, int ttl, boolean prevExist) throws EtcdException {
        StringUtil.assertEmpty(key, "Illegal key: [" + key + "]");
        Map<String, String> data = new HashMap<String, String>(3);
        data.put(KEY_OF_DIR, String.valueOf(true));
        if (ttl > 0) {
            data.put(KEY_OF_TTL, String.valueOf(ttl));
        }
        if (prevExist) {
            data.put(KEY_OF_PREVEXIST, String.valueOf(prevExist));
        }
        return put(key, data, null);
    }

    /**
     * List dir
     *
     * @param key the dir key
     * @return operation result
     * @throws EtcdException exception during list the dir.
     */
    public EtcdResult listDir(String key) throws EtcdException {
        return listDir(key, false);
    }

    /**
     * List dir
     *
     * @param key       the dir key
     * @param recursive list dir recursive.
     * @return operation result
     * @throws EtcdException exception during list the dir.
     */
    public EtcdResult listDir(String key, boolean recursive) throws EtcdException {
        StringUtil.assertEmpty(key, "Illegal key: [" + key + "]");
        Map<String, String> params = new HashMap<String, String>(1);
        if (recursive) {
            params.put(KEY_OF_RECURSIVE, String.valueOf(true));
        }
        String requestUrl = OkHttpUtil.buildRequestUrl(baseUri, key, params);
        Request getReq = new Request.Builder()
                .url(requestUrl)
                .build();
        return syncExecute(getReq);
    }

    /**
     * Delete a directory
     *
     * @param key       the dir key
     * @param recursive set recursive=true if the directory holds keys
     * @return operation result
     * @throws EtcdException
     */
    public EtcdResult deleteDir(String key, boolean recursive) throws EtcdException {
        StringUtil.assertEmpty(key, "Illegal key: [" + key + "]");
        Map<String, String> params = new HashMap<String, String>(1);
        if (recursive) {
            params.put(KEY_OF_RECURSIVE, String.valueOf(true));
        } else {
            params.put(KEY_OF_DIR, String.valueOf(true));
        }
        String requestUrl = OkHttpUtil.buildRequestUrl(baseUri, key, params);
        Request delReq = new Request.Builder()
                .url(requestUrl)
                .delete()
                .build();
        return syncExecute(delReq);
    }

    /**
     * Atomic Compare-and-Swap
     * note: Note that CompareAndSwap does not work with directories.
     *
     * @param key        the key
     * @param value      the new value
     * @param conditions comparable conditions
     * @return operation result
     * @throws EtcdException
     */
    public EtcdResult cas(String key, String value, Map<String, String> conditions) throws EtcdException {
        Map<String, String> data = new HashMap<String, String>(1);
        data.put(KEY_OF_VALUE, value);
        return put(key, data, conditions);
    }

    /**
     * Atomic Compare-and-Delete
     * note: Note that CompareAndDelete does not work with directories.
     * Etcd's Atomic Compare-and-Delete API not support pervExist condition
     *
     * @param key        the key to be deleted.
     * @param conditions comparable conditions
     * @return operation result
     * @throws EtcdException
     */
    public EtcdResult cad(String key, Map<String, String> conditions) throws EtcdException {
        //cad not support pervExist condition, so the cad method will give developer a warn.
        if (conditions != null && (conditions.remove(KEY_OF_PREVEXIST) != null)) {
            LOG.warn("Etcd's Atomic Compare-and-Delete API not support pervExist condition!");
        }
        String requestUrl = OkHttpUtil.buildRequestUrl(baseUri, key, conditions);
        Request delReq = new Request.Builder().url(requestUrl)
                .delete()
                .build();
        return syncExecute(delReq);
    }

    /**
     * Watch for a change on a key
     *
     * @param key the key
     */
    public void watch(String key, EtcdCallback etcdCallback) {
        watch(key, -1L, false, etcdCallback);
    }

    /**
     * Watch for a change on a key.
     *
     * @param key          the key
     * @param recursive    set recursive true if you want to watch for child keys
     * @param etcdCallback callback.
     */
    public void watch(String key, boolean recursive, EtcdCallback etcdCallback) {
        watch(key, -1L, recursive, etcdCallback);
    }

    /**
     * Watch for a change on a key.
     *
     * @param key          the key
     * @param index        watch the event from index to current index.
     * @param etcdCallback callback
     */
    public void watch(String key, long index, EtcdCallback etcdCallback) {
        watch(key, index, false, etcdCallback);
    }

    /**
     * Watch for a change on a key
     *
     * @param key          the key
     * @param index        the wait index
     * @param recursive    set recursive true if you want to watch for child keys
     * @param etcdCallback callback
     */
    public void watch(String key, long index, boolean recursive, EtcdCallback etcdCallback) {
        Map<String, String> params = new HashMap<String, String>(3);
        params.put(KEY_OF_WAIT, String.valueOf(true));
        if (index > 0) {
            params.put(KEY_OF_WAITINDEX, String.valueOf(index));
        }
        if (recursive) {
            params.put(KEY_OF_RECURSIVE, String.valueOf(true));
        }
        String requestUrl = OkHttpUtil.buildRequestUrl(baseUri, key, params);
        asyncExecute(new Request.Builder().url(requestUrl).build(), etcdCallback);
    }

    /**
     * Async execute Etcd Operation.
     *
     * @param request      http request
     * @param etcdCallback EtcdCallback
     */
    private void asyncExecute(Request request, EtcdCallback etcdCallback) {
        HTTP_CLIENT.newCall(request).enqueue(new OkHttp3CallbackAdapter(etcdCallback, MESSAGE_PARSER));
    }

    /**
     * The basic put operation.
     *
     * @param key    the key
     * @param data   the form body of request
     * @param params the url parameters of request
     * @return response of request
     * @throws EtcdException exception during get the value of a key
     */
    private EtcdResult put(String key, Map<String, String> data, Map<String, String> params) throws EtcdException {
        String requestUrl = OkHttpUtil.buildRequestUrl(baseUri, key, params);
        FormBody formBody = OkHttpUtil.buildFormBody(data);
        Request putReq = new Request.Builder()
                .url(requestUrl)
                .put(formBody)
                .build();
        return syncExecute(putReq);
    }

    /**
     * Check DefaultEtcdClient's available, that's to say make sure DefaultEtcdClient can access the given uri.
     */
    @Deprecated
    private void checkAvailable() {
        String notFoundKey = UUID.randomUUID().toString();
        try {
            get("/" + notFoundKey);
        } catch (KeyNotFoundException e) {
            //DefaultEtcdClient is available.
            return;
        } catch (EtcdException e) {
            //DefaultEtcdClient is not available.
            throw e;
        }
    }

    /**
     * @param request
     * @return
     * @throws EtcdException exception during get the value of a key
     */
    private EtcdResult syncExecute(Request request) throws EtcdException {
        Response response;
        try {
            response = HTTP_CLIENT.newCall(request).execute();
        } catch (IOException e) {
            LOG.error("Execute http request error, please check network connection.", e);
            throw new ConnectException("Execute http request error, please check network connection.", e);
        }
        String bodyStr;
        try {
            bodyStr = response.body().string();
        } catch (IOException e) {
            LOG.error("Read http response body error.", e);
            throw new EtcdException("Read http response body error.", e);
        }
        int statusCode = response.code();
        //remove the "\n" in the end of bodyStr. just
        if (StringUtil.isNotEmpty(bodyStr)) {
            bodyStr = bodyStr.substring(0, bodyStr.length() - 1);
        }
        LOG.debug("Http response code: {}, body: {}", statusCode, bodyStr);
        return MESSAGE_PARSER.parseResponse(statusCode, bodyStr);
    }

}
