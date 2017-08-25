package car2go.com.endpoint2mock2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ReplaceEndpointClientTest {

    @Mock
    OkHttpClient realClient;

    ReplaceEndpointClient testee;

    private static final String REAL_BASE_URL = "http://real-server.com/";
    private static final String MOCK_BASE_URL = "http://mock-server.com/";
    private static final String PATH = "path1/path2";

    @Before
    public void setup() {
        testee = new ReplaceEndpointClient(
                MOCK_BASE_URL,
                realClient
        );
    }

    @Test
    public void replaceUrl() {
        // Given
        Request realRequest = new Request.Builder()
                .url(REAL_BASE_URL + PATH)
                .build();

        //When
        testee.newCall(realRequest);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        Mockito.verify(realClient).newCall(requestCaptor.capture());
        Request sentRequest = requestCaptor.getValue();
        assertEquals(MOCK_BASE_URL + PATH, sentRequest.url().toString());
    }
}