package fr.openium.testdrivingdistraction.ext

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers


fun <T> Observable<T>.toMainThread(): Observable<T> =
    this.observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.fromIOToMain(): Observable<T> =
    this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())