package tkosen.com.map.net;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tctkosen on 13/04/16.
 */


public class ServiceGenerator {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://restcountries.eu/")
            .addConverterFactory(GsonConverterFactory.create());

    public static CountryAPI createService(Class<CountryAPI> serviceClass) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

}