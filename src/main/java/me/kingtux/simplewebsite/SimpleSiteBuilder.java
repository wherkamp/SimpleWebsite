package me.kingtux.simplewebsite;

import io.javalin.Javalin;
import me.kingtux.javalinvc.JavalinVC;
import me.kingtux.javalinvc.JavalinVCBuilder;
import me.kingtux.javalinvc.WebsiteRules;
import me.kingtux.javalinvc.WebsiteRulesBuilder;
import me.kingtux.javalinvc.rg.ResourceGrabber;
import me.kingtux.javalinvc.rg.ResourceGrabbers;
import me.kingtux.javalinvc.rg.templategrabbers.InternalResourceGrabber;
import me.kingtux.javalinvc.view.ViewManager;
import me.kingtux.javalinvc.view.ViewManagerBuilder;
import me.kingtux.tuxorm.TOConnection;
import org.simplejavamail.mailer.Mailer;

import java.util.Properties;

public class SimpleSiteBuilder {

    private Properties properties;

    public SimpleSiteBuilder() {
        properties = new BetterProperties();
    }

    public SimpleSiteBuilder(Properties properties) {
        this.properties = BetterProperties.ptobp(properties);
    }


    public SimpleSite create() {
        Javalin javalin = Utils.createJavalin(properties);
        WebsiteRules rules = WebsiteRulesBuilder.create().setName(properties.getProperty("site.name", "SimpleSite")).setUrl(properties.getProperty("site.url", "{PFFT}")).build();
        ViewManagerBuilder viewManager = ViewManagerBuilder.create().setExtension(".html").setViewManager("me.kingtux.javalinvc.jtwig.JtwigViewManager");
        ResourceGrabber resourceGrabber = Utils.getResourceGrabber(properties);
        TOConnection connection = new TOConnection(Utils.createTuxJSQL(properties));
        Mailer mailer = Utils.createMailer(properties);

        JavalinVCBuilder javalinVCBuilder = JavalinVCBuilder.create().setJavalin(Utils.createJavalin(properties));
        javalinVCBuilder.setResourceGrabber(resourceGrabber);
        javalinVCBuilder.setRules(rules);
        javalinVCBuilder.setViewManager(viewManager);


        return new SimpleSite(javalinVCBuilder.createJavalinVC(), connection, mailer);
    }

}
