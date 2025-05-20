package guru.qa.niffler.api.github;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.model.CategoryJson;
import retrofit2.Call;
import retrofit2.http.*;

public interface GhApi {
    @GET("repos/rus9313/country/issues/{issue_number}")
    @Headers({
            "Accept: application/vnd.github+json",
            "X-GitHub-Api-Version: 2022-11-28"
    })
    Call<JsonNode> getIssues(@Header("Authorization") String bearerToken,
                             @Path("issue_number") String issue_number);
}
