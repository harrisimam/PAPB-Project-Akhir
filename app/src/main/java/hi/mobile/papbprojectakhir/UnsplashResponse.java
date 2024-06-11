package hi.mobile.papbprojectakhir;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UnsplashResponse {
    @SerializedName("results")
    private List<Photo> results;

    public List<Photo> getResults() {
        return results;
    }

    public static class Photo {
        @SerializedName("urls")
        private Urls urls;

        public Urls getUrls() {
            return urls;
        }

        public static class Urls {
            @SerializedName("regular")
            private String regular;

            public String getRegular() {
                return regular;
            }
        }
    }
}