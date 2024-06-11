package hi.mobile.papbprojectakhir;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UnsplashApiService {
    @GET("search/photos")
    Call<UnsplashResponse> searchPhotos(
            @Query("query") String query,
            @Query("client_id") String clientId,
            @Query("per_page") int perPage,
            @Query("collections") String collections
    );
}

