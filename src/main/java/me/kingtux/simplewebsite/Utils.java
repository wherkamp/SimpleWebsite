package me.kingtux.simplewebsite;

import dev.tuxjsql.core.TuxJSQL;
import dev.tuxjsql.core.TuxJSQLBuilder;
import io.javalin.Javalin;
import me.kingtux.javalinvc.rg.ResourceGrabber;
import me.kingtux.javalinvc.rg.ResourceGrabbers;
import me.kingtux.tuxorm.TOConnection;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

import java.util.Properties;

public class Utils {
    public static Server createSimpleServer(int port) {
        Server server = new Server(port);

        return server;
    }

    public static SslContextFactory getSslContextFactory(String file, String password) {
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(file);
        sslContextFactory.setKeyStorePassword(password);
        return sslContextFactory;
    }

    public static Server createSimpleServer(SslContextFactory sslContextFactory, int port1, int sslPort) {
        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port1);
        server.addConnector(connector);

        // HTTP Configuration
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSendServerVersion(false);
        httpConfig.setSecureScheme("https");
        httpConfig.setSecurePort(sslPort);

        // SSL Context Factory for HTTPS and HTTP/2
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        sslContextFactory.setProvider("Conscrypt");

        // HTTPS Configuration
        HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        // HTTP/2 Connection Factory
        HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(httpsConfig);
        ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        alpn.setDefaultProtocol("h2");

        // SSL Connection Factory
        SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

        // HTTP/2 Connector
        ServerConnector http2Connector = new ServerConnector(server, ssl, alpn, h2, new HttpConnectionFactory(httpsConfig));
        http2Connector.setPort(sslPort);
        server.addConnector(http2Connector);

        return server;
    }

    public static Javalin createJavalin(Properties p) {
        Server server = null;
        int port = Integer.parseInt(p.getProperty("site.port", "8080"));
        if (Boolean.parseBoolean(p.getProperty("site.ssl", "false"))) {
            SslContextFactory contextFactory = getSslContextFactory(p.getProperty("site.ssl.file"),
                    p.getProperty("site.ssl.password"));
            server =
                    createSimpleServer(contextFactory, port, Integer.parseInt(p.getProperty("site.ssl.port", "8423")));
        } else {
            server = createSimpleServer(port);
        }
        Server finalServer = server;
        return Javalin.create(javalinConfig -> javalinConfig.server(() -> finalServer));
    }

    public static ResourceGrabber getResourceGrabber(Properties properties) {
        ResourceGrabbers grabbers = ResourceGrabbers.valueOf(properties.getProperty("rg.type", ResourceGrabbers.INTERNAL_GRABBER.name()));
        return grabbers.build(properties.getProperty("rg.path", "templates"));
    }

    public static TuxJSQL createTuxJSQL(Properties p) {
        if (p.getProperty("db.type", null) == null) {
            Properties tempDB = new Properties();
            tempDB.setProperty("db.type", "dev.tuxjsql.sqlite.SQLiteBuilder");
            tempDB.setProperty("db.file", "db.db");
            return TuxJSQLBuilder.create(tempDB);
        } else
            return TuxJSQLBuilder.create(p);
    }

    public static Mailer createMailer(Properties properties) {
        return MailerBuilder.withSMTPServer(properties.getProperty("email.host", ""),
                Integer.parseInt(properties.getProperty("email.port", "")),
                properties.getProperty("email.from", ""),
                properties.getProperty("email.password", "")).
                withTransportStrategy(TransportStrategy.valueOf(properties.getProperty("email.ts"))).buildMailer();

    }
}
