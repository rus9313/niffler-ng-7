package guru.qa.niffler.api.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient {

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().spendUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    public SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi
                    .addSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
        return response.body();
    }

    public SpendJson editSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi
                    .editSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    public SpendJson getSpend(String id, String userName) {
        final Response<SpendJson> response;
        try {
            response = spendApi
                    .getSpend(id, userName)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    public List<SpendJson> getSpendAll(String userName,
                                       CurrencyValues filterCurrency,
                                       String from,
                                       String to) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi
                    .getSpendsAll(userName, filterCurrency, from, to)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    public void deleteSpends(String userName, List<String> ids) {
        try {
            spendApi.deleteSpends(userName, ids)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
