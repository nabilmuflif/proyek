package com.example.curhatku.network;

import com.example.curhatku.models.Quote;
import com.example.curhatku.models.QuoteResponse; // Masih ada jika nanti butuh

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface QuoteApiService {

    @GET("api/random")
    Call<List<Quote>> getRandomQuote();
}