package com.light.body;

import android.graphics.Bitmap;
import android.net.Uri;

import com.light.core.Utils.http.HttpDownLoader;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.functions.Function;

/**
 * Created by xiaoqi on 2018/1/26
 */

public class RxLight {

	public static FlowableTransformer<Uri, Bitmap> compressForUriHttp(){
		return compressForUriHttp((CompressArgs)null);
	}

	public static FlowableTransformer<Uri, Bitmap> compressForUriHttp(final CompressArgs compressArgs){
		return new FlowableTransformer<Uri, Bitmap>() {
			@Override
			public Publisher<Bitmap> apply(Flowable<Uri> upstream) {
				return upstream.flatMap(new Function<Uri, Publisher<Bitmap>>() {
					@Override
					public Publisher<Bitmap> apply(Uri uri) throws Exception {
						final byte[] bytes = HttpDownLoader.downloadImage(uri);
						return new Publisher<Bitmap>() {
							@Override
							public void subscribe(Subscriber<? super Bitmap> subscriber) {
								subscriber.onNext(Light.getInstance().compress(bytes, compressArgs));
								subscriber.onComplete();
							}

						};
					}
				});
			}
		};
	}

	public static FlowableTransformer<String, Bitmap> compressForStringHttp(){
		return compressForStringHttp(null);
	}

	public static FlowableTransformer<String, Bitmap> compressForStringHttp(final CompressArgs compressArgs){
		return new FlowableTransformer<String, Bitmap>() {
			@Override
			public Publisher<Bitmap> apply(Flowable<String> upstream) {
				return upstream.flatMap(new Function<String, Publisher<Bitmap>>() {
					@Override
					public Publisher<Bitmap> apply(String url) throws Exception {
						final byte[] bytes = HttpDownLoader.downloadImage(url);
						return new Publisher<Bitmap>() {
							@Override
							public void subscribe(Subscriber<? super Bitmap> subscriber) {
								subscriber.onNext(Light.getInstance().compress(bytes, compressArgs));
								subscriber.onComplete();
							}
						};
					}
				});
			}
		};
	}

	public static FlowableTransformer<Uri, Boolean> compressForUriHttp(String outPath){
		return compressForUriHttp(outPath, null);
	}

	public static FlowableTransformer<Uri, Boolean> compressForUriHttp(final String outPath, final CompressArgs compressArgs){
		return new FlowableTransformer<Uri, Boolean>() {
			@Override
			public Publisher<Boolean> apply(Flowable<Uri> upstream) {
				return upstream.flatMap(new Function<Uri, Publisher<Boolean>>() {
					@Override
					public Publisher<Boolean> apply(Uri uri) throws Exception {
						final byte[] bytes = HttpDownLoader.downloadImage(uri);
						return new Publisher<Boolean>() {
							@Override
							public void subscribe(Subscriber<? super Boolean> subscriber) {
								subscriber.onNext(Light.getInstance().compress(bytes, compressArgs, outPath));
								subscriber.onComplete();
							}
						};
					}
				});
			}
		};
	}

	public static FlowableTransformer<String, Boolean> compressFoStringHttp(final String outPath){
		return compressFoStringHttp(outPath, null);
	}

	public static FlowableTransformer<String, Boolean> compressFoStringHttp(final String outPath, final CompressArgs compressArgs){
		return new FlowableTransformer<String, Boolean>() {
			@Override
			public Publisher<Boolean> apply(Flowable<String> upstream) {
				return upstream.flatMap(new Function<String, Publisher<Boolean>>() {
					@Override
					public Publisher<Boolean> apply(String uri) throws Exception {
						final byte[] bytes = HttpDownLoader.downloadImage(uri);
						return new Publisher<Boolean>() {
							@Override
							public void subscribe(Subscriber<? super Boolean> subscriber) {
								subscriber.onNext(Light.getInstance().compress(bytes, compressArgs, outPath));
								subscriber.onComplete();
							}
						};
					}
				});
			}
		};
	}

	public static <T> FlowableTransformer<T, Bitmap> compress(){
		return compress((CompressArgs)null);
	}

	public static <T> FlowableTransformer<T, Bitmap> compress(final CompressArgs compressArgs){
		return new FlowableTransformer<T, Bitmap>() {
			@Override
			public Publisher<Bitmap> apply(Flowable<T> upstream) {
				return upstream.flatMap(new Function<T, Publisher<Bitmap>>() {
					@Override
					public Publisher<Bitmap> apply(final T t)throws Exception {
						return new Publisher<Bitmap>() {
							@Override
							public void subscribe(Subscriber<? super Bitmap> subscriber) {
								subscriber.onNext(Light.getInstance().compressImage(t, compressArgs));
								subscriber.onComplete();
							}
						};
					}
				});
			}
		};
	}

	public static <T> FlowableTransformer<T, Boolean> compress(String outPath){
		return compress(outPath, null);
	}

	public static <T> FlowableTransformer<T, Boolean> compress(final String outPath, final CompressArgs compressArgs){
		return new FlowableTransformer<T, Boolean>() {
			@Override
			public Publisher<Boolean> apply(Flowable<T> upstream) {
				return upstream.flatMap(new Function<T, Publisher<Boolean>>() {
					@Override
					public Publisher<Boolean> apply(final T t)throws Exception {
						return new Publisher<Boolean>() {
							@Override
							public void subscribe(Subscriber<? super Boolean> subscriber) {
								subscriber.onNext(Light.getInstance().compressImage(t, compressArgs, outPath));
								subscriber.onComplete();
							}
						};
					}
				});
			}
		};
	}
}
