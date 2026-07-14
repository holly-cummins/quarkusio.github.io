package io.quarkus.tools.migration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
@Startup
public class SearchScriptDownloader {

    private static final Logger LOG = Logger.getLogger(SearchScriptDownloader.class);
    private static final String SCRIPT_URL = "https://search.quarkus.io/static/bundle/app.js";
    private static final String CACHED_FILE = "assets/javascript/search-wc.js";

    // Downloads to public/ which survives mvn clean (only target/ is deleted).
    // TODO: should mvn clean also remove this downloaded file?
    SearchScriptDownloader() {
        if (!"cached".equals("cached")) {
            return;
        }
        Path dest = Path.of("public", CACHED_FILE.split("/"));
        if (Files.exists(dest)) {
            LOG.debugf("Search script already exists at %s, skipping download", dest);
            return;
        }
        try {
            Files.createDirectories(dest.getParent());
            try (InputStream in = URI.create(SCRIPT_URL).toURL().openStream()) {
                String content = new String(in.readAllBytes());
                content = content.replaceAll("//# sourceMappingURL=.*\\.map", "");
                Files.writeString(dest, content);
                LOG.infof("Downloaded search script from %s to %s", SCRIPT_URL, dest);
            }
        } catch (IOException e) {
            LOG.errorf(e, "Failed to download search script from %s", SCRIPT_URL);
        }
    }
}
