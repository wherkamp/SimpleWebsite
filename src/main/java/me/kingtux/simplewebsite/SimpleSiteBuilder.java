package me.kingtux.simplewebsite;

import dev.tuxjsql.core.TuxJSQL;
import io.javalin.Javalin;
import me.kingtux.javalinvc.JavalinVC;
import me.kingtux.javalinvc.JavalinVCBuilder;
import me.kingtux.javalinvc.WebsiteRules;
import me.kingtux.javalinvc.WebsiteRulesBuilder;
import me.kingtux.javalinvc.rg.ResourceGrabber;

import me.kingtux.javalinvc.rg.ResourceGrabbers;
import me.kingtux.javalinvc.view.ViewManagerBuilder;
import me.kingtux.tuxorm.TOConnection;
import org.apache.commons.lang3.Validate;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.TransportStrategy;

import static me.kingtux.simplewebsite.SimpleSiteKeys.*;

import java.io.*;
import java.util.Properties;

public class SimpleSiteBuilder {

    private Properties properties;

    public SimpleSiteBuilder() {
        properties = new BetterProperties();
    }

    public SimpleSiteBuilder(Properties properties) {
        this.properties = BetterProperties.ptobp(properties);
    }

    public SimpleSiteBuilder(File properties) {
        this();
        if (!properties.exists()) return;
        try (InputStream inputStream = new FileInputStream(properties)) {
            this.properties.load(inputStream);
        } catch (IOException e) {
            SimpleSite.LOGGER.error("Unable to load properties file", e);
        }
    }


    private SimpleSiteBuilder setProperty(String string, String value) {
        properties.setProperty(string, value);
        return this;
    }

    public SimpleSiteBuilder setName(String value) {
        return setProperty(SITE_NAME, value);
    }

    public SimpleSiteBuilder setUseHTTP2(boolean value) {
        return setProperty(SITE_NAME, String.valueOf(value));
    }

    public SimpleSiteBuilder setURL(String value) {
        return setProperty(SITE_URL, value);
    }

    public SimpleSiteBuilder setViewManagerExtension(String value) {
        return setProperty(VM_EXTENSION, value);
    }

    public SimpleSiteBuilder setSitePort(int value) {
        return setProperty(SITE_PORT, String.valueOf(value));
    }

    public SimpleSiteBuilder setSSLEnabled(boolean value) {
        return setProperty(SSL_ENABLED, String.valueOf(value));
    }

    public SimpleSiteBuilder setSSLFile(File value) {
        return setProperty(SSL_FILE, value.getAbsolutePath());
    }

    public SimpleSiteBuilder setSSLPort(int value) {
        return setProperty(SSL_PORT, String.valueOf(value));
    }

    public SimpleSiteBuilder setResourceGrabberType(ResourceGrabbers value) {
        return setProperty(RG_TYPE, value.name());
    }

    public SimpleSiteBuilder setResourceGrabberPath(String value) {
        return setProperty(RG_PATH, value);
    }

    public SimpleSiteBuilder setEmailhost(String value) {
        return setProperty(EMAIL_HOST, value);
    }

    public SimpleSiteBuilder setEmailPort(int value) {
        return setProperty(EMAIL_PORT, String.valueOf(value));
    }

    public SimpleSiteBuilder setEmailFrom(String value) {
        return setProperty(EMAIL_FROM, value);
    }

    public SimpleSiteBuilder setEmailPassword(String value) {
        return setProperty(EMAIL_PASSWORD, value);
    }

    public SimpleSiteBuilder setEmailTransportStrategy(TransportStrategy value) {
        return setProperty(EMAIL_TRANSPORT_STRATEGY, value.name());
    }

    public SimpleSiteBuilder setContextPath(String value) {
        return setProperty(CONTEXT_PATH, value);
    }


    public SimpleSiteBuilder setDatabase(Properties properties) {
        properties.keySet().forEach(o -> this.properties.setProperty((String) o, properties.getProperty((String) o)));
        return this;
    }

    //Build the simple site;
    public SimpleSite create() {
        WebsiteRules rules = WebsiteRulesBuilder.create().setName(properties.getProperty("site.name", "SimpleSite")).setUrl(properties.getProperty("site.url", "{PFFT}")).build();
        ViewManagerBuilder viewManager = ViewManagerBuilder.create().setExtension(properties.getProperty("vm.extension", ".html")).setViewManager("me.kingtux.javalinvc.jtwig.JtwigViewManager");
        ResourceGrabber resourceGrabber = Utils.getResourceGrabber(properties);
        TuxJSQL tuxJSQL = Utils.createTuxJSQL(properties);
        TOConnection connection = null;
        if (tuxJSQL != null) {
            connection = new TOConnection(tuxJSQL);
        }
        Mailer mailer = Utils.createMailer(properties);

        JavalinVCBuilder javalinVCBuilder = JavalinVCBuilder.create().setJavalin(Utils.createJavalin(properties));
        javalinVCBuilder.setResourceGrabber(resourceGrabber);
        javalinVCBuilder.setRules(rules);
        javalinVCBuilder.setViewManager(viewManager);


        return new SimpleSite(javalinVCBuilder.createJavalinVC(), connection, mailer);
    }

}
