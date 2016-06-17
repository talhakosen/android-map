package tkosen.com.map.net;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tkosen.com.map.modal.MapObject;

/**
 * Created by tctkosen on 16/06/16.
 */
public interface CountryAPI {
    @GET("rest/v1/all")
    Call<List<MapObject>> getCountries();
}
