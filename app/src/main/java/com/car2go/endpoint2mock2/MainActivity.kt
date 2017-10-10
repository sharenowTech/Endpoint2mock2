package com.car2go.endpoint2mock2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var githubApi: GithubApi

    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val client = MockableClient.baseUrl("http://${BuildConfig.BUILD_HOST_ADDRESS}")   // Assuming that you have something running at this URL
                .shouldMock(true)
                .build()

        githubApi = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.github.com")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(GithubApi::class.java)

        requestButton.setOnClickListener {
            subscription?.unsubscribe()

            subscription = githubApi.getRepositories("car2go")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { repositories ->
                                result.text = repositories.toString()
                            },
                            { result.text = it.toString() }
                    )
        }
    }

    override fun onPause() {
        super.onPause()

        subscription?.unsubscribe()
    }
}
