package me.kingtux.simplewebsite;

import dev.tuxjsql.core.TuxJSQL;
import dev.tuxjsql.core.TuxJSQLBuilder;
import io.javalin.Javalin;
import io.javalin.core.compression.Brotli;
import io.javalin.core.compression.Gzip;
import io.javalin.core.util.OptionalDependency;
import me.kingtux.javalinvc.rg.ResourceGrabber;
import me.kingtux.javalinvc.rg.ResourceGrabbers;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

import java.io.File;
import java.util.Properties;

public class Utils {
    public static Server createSimpleServer(int port) {
        return new Server(port);
    }

    public static SslContextFactory getSslContextFactory(String file, String password) {

        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(new File(file).getAbsolutePath());
        sslContextFactory.setKeyStorePassword(password);
        return sslContextFactory;
    }

    public static Server createSimpleServer(SslContextFactory sslContextFactory, int port1, int sslPort) {
        Server server = new Server();
        ServerConnector sslConnector = new ServerConnector(server, sslContextFactory);
        sslConnector.setPort(sslPort);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port1);
        server.setConnectors(new Connector[]{sslConnector, connector});
        return server;

    }

    public static Javalin createJavalin(Properties p) {
        Server server = null;
        int port = Integer.parseInt(p.getProperty("site.port", "8080"));
        if (Boolean.parseBoolean(p.getProperty("site.ssl", "false"))) {
            SslContextFactory contextFactory = getSslContextFactory(p.getProperty("site.ssl.file"),
                    p.getProperty("site.ssl.password"));
            if (Boolean.parseBoolean(p.getProperty(SimpleSiteKeys.USE_HTTP2, "false"))) {
                server =
                        HTTPTwoBuilder.createHTTP2Server(contextFactory, port, Integer.parseInt(p.getProperty("site.ssl.port", "8423")));

            } else {
                server =
                        createSimpleServer(contextFactory, port, Integer.parseInt(p.getProperty("site.ssl.port", "8423")));
            }
        } else {
            server = createSimpleServer(port);
        }
        Server finalServer = server;
        return Javalin.create(javalinConfig -> {
            javalinConfig.server(() -> finalServer);
            javalinConfig.contextPath = p.getProperty(SimpleSiteKeys.CONTEXT_PATH, "/");
            if (isClassPresent(OptionalDependency.JVMBROTLI.getTestClass())) {
                //noinspection deprecation
                javalinConfig.compressionStrategy(
                        new Brotli(Integer.parseInt(p.getProperty(SimpleSiteKeys.COMPRESSION_BROTLI, "4"))),
                        new Gzip(Integer.parseInt(p.getProperty(SimpleSiteKeys.COMPRESSION_GZIP, "6"))));
                SimpleSite.LOGGER.info("Using Compression");

            }
        });

    }

    private static boolean isClassPresent(String testClass) {
        try {
            Class.forName(testClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public static ResourceGrabber getResourceGrabber(Properties properties) {
        ResourceGrabbers grabbers = ResourceGrabbers.valueOf(properties.getProperty("rg.type", ResourceGrabbers.INTERNAL_EXTERNAL_GRABBER.name()));
        return grabbers.build(properties.getProperty("rg.path", "templates"));
    }

    public static TuxJSQL createTuxJSQL(Properties p) {
        if (p.getProperty("db.type", null) == null) {
            if (!isClassPresent("dev.tuxjsql.sqlite.SQLiteBuilder")) {
                return null;
            }
            Properties tempDB = new Properties();
            tempDB.setProperty("db.type", "dev.tuxjsql.sqlite.SQLiteBuilder");
            tempDB.setProperty("db.file", "db.db");
            return TuxJSQLBuilder.create(tempDB);
        } else
            return TuxJSQLBuilder.create(p);
    }

    public static Mailer createMailer(Properties properties) {
        if (properties.getProperty(SimpleSiteKeys.EMAIL_HOST, null) == null) return null;
        return MailerBuilder.withSMTPServer(properties.getProperty("email.host", ""),
                Integer.parseInt(properties.getProperty("email.port", "")),
                properties.getProperty("email.from", ""),
                properties.getProperty(SimpleSiteKeys.EMAIL_PASSWORD, "")).
                withTransportStrategy(TransportStrategy.valueOf(properties.getProperty("email.ts", TransportStrategy.SMTP.name()))).buildMailer();
    }
}
