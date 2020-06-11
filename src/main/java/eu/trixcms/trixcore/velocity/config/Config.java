package eu.trixcms.trixcore.velocity.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import eu.trixcms.trixcore.velocity.TrixCore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private Integer port;

    @SerializedName(value = "secret_key")
    private String secretKey;

    @SerializedName(value = "custom_motd")
    private String customMotd;

    public void save(File dest) throws IOException {
        try (Writer writer = new FileWriter(dest)) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting().create();
            gson.toJson(this, writer);
            dest.setWritable(true);
            dest.setReadable(true);
        } catch(IOException e){
            logger.error(TrixCore.getInstance().getTranslator().of("ERROR"), e);
        }
    }

    public static Config load(File source) {
        Config config = null;
        if (!source.exists()) {
            source.getParentFile().mkdirs();
            config = new Config(0, "", "");
            try {
                config.save(source);
            } catch (IOException e) {
                logger.error(TrixCore.getInstance().getTranslator().of("ERROR"), e);
            }
        } else {
            source.setWritable(true);
            source.setReadable(true);
            Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting().create();
            try {
                JsonReader reader = new JsonReader(new FileReader(source));
                config = gson.fromJson(reader, Config.class);
            } catch (FileNotFoundException e) {
                logger.error(TrixCore.getInstance().getTranslator().of("ERROR"), e);
            }
        }

        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(port, config.port) &&
                Objects.equals(secretKey, config.secretKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, secretKey);
    }
}
