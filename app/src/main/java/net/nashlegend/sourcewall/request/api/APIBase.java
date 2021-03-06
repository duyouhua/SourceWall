package net.nashlegend.sourcewall.request.api;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import net.nashlegend.sourcewall.model.AceModel;
import net.nashlegend.sourcewall.model.Article;
import net.nashlegend.sourcewall.model.Post;
import net.nashlegend.sourcewall.model.Question;
import net.nashlegend.sourcewall.request.HttpUtil;
import net.nashlegend.sourcewall.request.JsonHandler;
import net.nashlegend.sourcewall.request.NetworkTask;
import net.nashlegend.sourcewall.request.RequestBuilder;
import net.nashlegend.sourcewall.request.RequestObject.RequestCallBack;
import net.nashlegend.sourcewall.request.ResponseObject;
import net.nashlegend.sourcewall.request.parsers.ImageUploadParser;
import net.nashlegend.sourcewall.util.Config;
import net.nashlegend.sourcewall.util.ImageUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class APIBase {

    /**
     * 统一回复，回复主题站、帖子、问题
     *
     * @return ResponseObject
     */
    @Nullable
    public static NetworkTask<String> reply(AceModel data, String content, RequestCallBack<String> callBack) {
        if (data instanceof Article) {
            return ArticleAPI.replyArticle(((Article) data).getId(), content + Config.getSimpleReplyTail(), callBack);
        } else if (data instanceof Post) {
            return PostAPI.replyPost(((Post) data).getId(), content + Config.getSimpleReplyTail(), callBack);
        } else if (data instanceof Question) {
            return QuestionAPI.answerQuestion(((Question) data).getId(), content + Config.getSimpleReplyTail(), callBack);
        } else {
            return null;
        }
    }

    /**
     * 上传图片
     *
     * @param path 要上传图片的路径
     * @return 返回ResponseObject，resultObject.result是上传后的图片地址，果壳并不会对图片进行压缩
     */
    public static Subscription uploadImage(final String path, final RequestCallBack<String> callBack) {
        return Observable.just(path)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String path) {
                        return ImageUtils.compressImage(path);
                    }
                })
                .map(new Func1<String, ResponseObject<String>>() {
                    @Override
                    public ResponseObject<String> call(String path) {
                        return new RequestBuilder<String>()
                                .post()
                                .url("http://www.guokr.com/apis/image.json?enable_watermark=true")
                                .upload(path)
                                .uploadFileKey("upload_file")
                                .mediaType("image/*")
                                .callback(callBack)
                                .parser(new ImageUploadParser())
                                .requestSync();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseObject<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(e, new ResponseObject<String>());
                    }

                    @Override
                    public void onNext(ResponseObject<String> responseObject) {
                        if (callBack != null) {
                            if (responseObject.ok) {
                                callBack.onSuccess(responseObject.result, responseObject);
                            } else {
                                callBack.onFailure(responseObject.throwable, responseObject);
                            }
                        }
                    }
                });
    }

    /**
     * 使用github的接口转换markdown为html.
     * 通过github接口转换markdown，一小时只能60次
     *
     * @param text 要转换的文本内容
     * @return ResponseObject
     */
    @Deprecated
    public static ResponseObject<String> parseMarkdownByGitHub(String text) {
        ResponseObject<String> resultObject = new ResponseObject<>();
        if (TextUtils.isEmpty(text)) {
            resultObject.ok = true;
            resultObject.result = "";
        } else {
            String url = "https://api.github.com/markdown";
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("text", text);
                jsonObject.put("mode", "gfm");
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder().post(body).url(url).build();
                Response response = HttpUtil.getDefaultHttpClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    resultObject.ok = true;
                    resultObject.result = result;
                }
            } catch (Exception e) {
                JsonHandler.handleRequestException(e, resultObject);
            }
        }
        return resultObject;
    }

    /**
     * 将时间转换成可见的。话说果壳返回的时间格式是什么标准
     *
     * @param dateString 传入的时间字符串
     * @return 解析后的时间 yyyy-mm-dd hh:mm:ss
     */
    public static String parseDate(String dateString) {
        return dateString.replace("T", " ").replaceAll("[\\+\\.]\\S+$", "");
    }
}
