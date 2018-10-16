/*
 * Copyright 2018 Babylon Healthcare Services Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.certificatetransparency.ctlog.comm

import org.certificatetransparency.ctlog.comm.model.AddChainRequest
import org.certificatetransparency.ctlog.comm.model.AddChainResponse
import org.certificatetransparency.ctlog.comm.model.GetEntriesResponse
import org.certificatetransparency.ctlog.comm.model.GetEntryAndProofResponse
import org.certificatetransparency.ctlog.comm.model.GetRootsResponse
import org.certificatetransparency.ctlog.comm.model.GetSthConsistencyResponse
import org.certificatetransparency.ctlog.comm.model.GetSthResponse
import org.certificatetransparency.ctlog.comm.model.ProofByHashResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit interface for https://tools.ietf.org/html/rfc6962#section-4.3
 */
interface CtService {

    /**
     * Retrieve Merkle Audit Proof from Log by Leaf Hash
     * @param hash A base64-encoded v1 leaf hash. The [hash] must be calculated as defined in https://tools.ietf.org/html/rfc6962#section-3.4.
     * @param treeSize The [treeSize] of the tree on which to base the proof, in decimal. The [treeSize] must designate an existing v1 STH.
     */
    @GET("get-proof-by-hash")
    fun getProofByHash(@Query("tree_size") treeSize: Long, @Query("hash") hash: String): Call<ProofByHashResponse>

    /**
     * Retrieve Entry+Merkle Audit Proof from Log
     *
     * This API is probably only useful for debugging.
     *
     * @param leafIndex The index of the desired entry.
     * @param treeSize The tree_size of the tree for which the proof is desired. The tree size must designate an existing STH.
     */
    @GET("get-entry-and-proof")
    fun getEntryAndProof(@Query("leaf_index") leafIndex: Long, @Query("tree_size") treeSize: Long): Call<GetEntryAndProofResponse>

    /**
     * Retrieve Accepted Root Certificates
     */
    @GET("get-roots")
    fun getRoots(): Call<GetRootsResponse>

    /**
     * Retrieve Latest Signed Tree Head
     */
    @GET("get-sth")
    fun getSth(): Call<GetSthResponse>

    /**
     * Retrieve Entries from Log
     *
     * The [start] and [end] parameters SHOULD be within the range 0 <= x < "tree_size" as returned by "get-sth"
     *
     * Logs MAY honor requests where 0 <= "start" < "tree_size" and "end" >= "tree_size" by returning a partial response covering only
     * the valid entries in the specified range.  Note that the following restriction may also apply:
     *
     * Logs MAY restrict the number of entries that can be retrieved per "get-entries" request.  If a client requests more than the permitted
     * number of entries, the log SHALL return the maximum number of entries permissible.  These entries SHALL be sequential beginning with
     * the entry specified by "start".
     *
     * @param start 0-based index of first entry to retrieve, in decimal.
     * @param end 0-based index of last entry to retrieve, in decimal.
     */
    @GET("get-entries")
    fun getEntries(@Query("start") start: Long, @Query("end") end: Long): Call<GetEntriesResponse>

    /**
     * Retrieve Merkle Consistency Proof between Two Signed Tree Heads
     *
     * @param first The treeSize of the first tree, in decimal. Both tree sizes must be from existing v1 STHs (Signed Tree Heads).
     * @param second The treeSize of the second tree, in decimal. Both tree sizes must be from existing v1 STHs (Signed Tree Heads).
     */
    @GET("get-sth-consistency")
    fun getSthConsistency(@Query("first") first: Long, @Query("second") second: Long): Call<GetSthConsistencyResponse>

    /**
     * Add PreCertChain to Log
     */
    @POST("add-pre-chain")
    fun addPreChain(@Body addChainRequest: AddChainRequest): Call<AddChainResponse>

    /**
     * Add Chain to Log
     */
    @POST("add-chain")
    fun addChain(@Body addChainRequest: AddChainRequest): Call<AddChainResponse>
}
