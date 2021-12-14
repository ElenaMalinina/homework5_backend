package malinina.utils;

import lombok.experimental.UtilityClass;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@UtilityClass
public class RetrofitUtils {
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    OkHttpClient client = new OkHttpClient
            .Builder()
            .addInterceptor(logging)
            .build();

            public Retrofit getRetrofit() {
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                return new Retrofit.Builder()
                        .client(client)
                        .baseUrl("http://80.78.248.82:8189/market/api/v1/")
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build();
            }

}
