package com.ml.lib_tool

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import android.widget.TextView
import com.ml.lib_tool.rx.ClickOnSubscribe
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.components.support.RxFragment
import io.reactivex.FlowableTransformer
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit


class TololSto private constructor(){

    companion object {
        val instance   by lazy { TololSto() }
    }

    var mApplicationContext:Application? = null

}

/**
 * 获取上下文
 */
fun getApplicationContext(): Application {

    if(TololSto.instance.mApplicationContext!=null) return  TololSto.instance.mApplicationContext!!


    var method: Method
    try {
        method = Class.forName("android.app.AppGlobals").getDeclaredMethod("getInitialApplication")
        method.isAccessible = true
        TololSto.instance.mApplicationContext = method.invoke(null) as Application
    } catch (e: Exception) {
        try {
            method = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication")
            method.isAccessible = true
            TololSto.instance.mApplicationContext = method.invoke(null) as Application
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    return  TololSto.instance.mApplicationContext!!
}


/**
 * rxjava 线程切换，生命周期管理
 */
fun <T> RxAppCompatActivity.applySchedulers(): ObservableTransformer<T, T> {
    return ObservableTransformer { upstream ->
        upstream
                .compose<T>(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}

/**
 * rxjava 线程切换，生命周期管理
 */
fun <T> RxAppCompatActivity.applyFtSchedulers(): FlowableTransformer<T, T> {
    return FlowableTransformer { upstream ->
        upstream
                .compose<T>(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}


/**
 * rxjava 线程切换，生命周期管理
 */
fun <T> RxFragment.applySchedulers(): ObservableTransformer<T, T> {
    return ObservableTransformer { upstream ->
        upstream
                .compose<T>(this.bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}

/**
 * rxjava 线程切换，生命周期管理
 */
fun <T> RxFragment.applyFtSchedulers(): FlowableTransformer<T, T> {
    return FlowableTransformer { upstream ->
        upstream
                .compose<T>(this.bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}


/**
 * 防止按钮重复点击
 */
@SuppressLint("CheckResult")
fun View.rxOnClick(onNext:(View) -> Unit){
    Observable.create(ClickOnSubscribe(this@rxOnClick))
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .apply {
                when (this@rxOnClick.context){
                    is RxAppCompatActivity ->{this.compose<View>((this@rxOnClick.context as RxAppCompatActivity).bindUntilEvent(ActivityEvent.DESTROY))}
                    is RxFragment ->{this.compose<View>((this@rxOnClick.context as RxFragment).bindUntilEvent(FragmentEvent.DESTROY))}
                }
            }
            .subscribe { onNext(it) }
}

/**
 * 防止文本，不满一行时，自动换行
 */
@SuppressLint("CheckResult")
fun TextView.setTextDBC( text:String){

    Observable.create<String> {
        it.onNext(CharacterUtil.instance.toDBC(text))
    }.apply {
        when (this@setTextDBC.context){
            is RxAppCompatActivity ->{this.compose((this@setTextDBC.context as RxAppCompatActivity).applySchedulers())}
            is RxFragment ->{this.compose((this@setTextDBC.context as RxFragment).applySchedulers())}
            else ->{
                this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            }
        }
        subscribe { this@setTextDBC.text = it }
    }

}





