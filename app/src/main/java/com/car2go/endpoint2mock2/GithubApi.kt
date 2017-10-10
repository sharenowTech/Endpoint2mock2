package com.car2go.endpoint2mock2

import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable
import rx.Single

data class Repository(val name: String)

/**
 * Loads repositories from GitHub.
 */
interface GithubApi {

    /**
     * @return [Observable] which emits list of repositories of car2go company.
     *
     * There is an annotation, so it can be mocked.
     */
    @MockedEndpoint
    @GET("/users/{user}/repos")
    fun getRepositories(@Path("user") user: String): Single<List<Repository>>
}