package com.ml.lib_tool.rx

import android.view.View
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

/**
 * 按钮点击事件
 */
class ClickOnSubscribe(view:View) : ObservableOnSubscribe<View> {

    private val mView = view

    override fun subscribe(emitter: ObservableEmitter<View>) {

        mView.setOnClickListener {
            emitter.onNext(mView)
        }

    }
}