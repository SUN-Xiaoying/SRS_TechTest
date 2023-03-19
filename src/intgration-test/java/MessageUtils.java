import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

@Slf4j
public class MessageUtils {

    public static JsonNode loadMessage(String filepath) throws IOException {

        var mapper = new ObjectMapper();
        InputStream message = yahoofinance.histquotes2.MessageUtils.class.getResourceAsStream(filepath);
        InputStreamReader is = new InputStreamReader(Objects.requireNonNull(message));

        return mapper.readTree(is);
    }
}
