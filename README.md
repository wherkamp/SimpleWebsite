# SimpleSite

### What Is SimpleSite?

SimpleSite a framework that extends off of [JavalinVC](https://github.com/wherkamp/javalinvc). To add Email and Database support to make a simple to use web framework.


### Getting Started. 

There are two ways of creating a SimpleSite. First is via
[properties](https://kingtux.dev/SimpleWebsite/properties.html) second
is setting values in the SimpleSiteBuilder. 

`new SimpleSiteBuilder()` will be the base for everything if you pass a
Properties that will use those rules. After that just run #create() and
it will return the simple site. 

You can register a JavalinVC controller via #registerController, create
a TuxORM dao via #createDao, or send a SimpleJavaMail email via #send


### Libraries to know.
SimpleJavaMail - [http://www.simplejavamail.org/](http://www.simplejavamail.org/)

TuxORM - [https://github.com/wherkamp/tuxorm](https://github.com/wherkamp/tuxorm)

JavalinVC - [https://kingtux.dev/JavalinVC/](https://kingtux.dev/JavalinVC/)


