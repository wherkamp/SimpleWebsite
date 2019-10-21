---
layout: default
title: Simple Site Rules
nav_order: 1
---
### Properties required when creating a SimpleSite object
| property        | value           |
|:-------------|:------------------|
| site.name           | The name of the site   |
| site.url           | The base url for site   |
| site.port           | THe port default `8080` |
| site.path           | The base path for site. Default: `/`   |
| vm.extension           | What the templates will end with by default.html |
| rg.path           | Path for templates default `templates` |
| rg.type           | What ResourceGrabber default `INTERNAL_EXTERNAL_GRABBER` |
| site.ssl           | To use SSL `false` |
| site.ssl.file           | SSL JKS file learn more [here]() |
| site.ssl.port           | SSL port default `8423` |
| site.ssl.port           | SSL jks password |
| site.http2           | To use http2  requires ssl and depends Learn more [here]() Default `false` |
| db.*           | Read [this](https://tuxjsql.dev/) |
| email.host         | Host for email|
| email.port         | Port for email server|
| email.from         | From Address for emails|
| email.password         | Email Password|
| email.ts         | Email Transport Stratergy default `SMTP` |


