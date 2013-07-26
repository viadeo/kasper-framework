
===========================
Kasper framework Quickstart
===========================

Check :ref:`modules_split` and choose one module splitting strategy, then create your project hierarchy accordingly.

ex: minimal modules strategy : api/command/query

- **mycompany-mydomain-api**
    depends on :
        * kasper-api

- **mycompany-mydomain-command**
    depends on :
        * *mycompany-mydomain-api*
        * kasper-core

- **mycompany-mydomain-query**
    depends on :
        * *mycompany-mydomain-api*
        * kasper-core

- **mycompany-mywebapp**
    depends on :
        * *mycompany-mydomain-query*
        * *mycompany-mydomain-command*
        * kasper-platform (the Kasper platform, with resources discovery, binding mechanisms, ...)
        * kasper-web (web bindings, helpers, auto-exposition)
        * kasper-documentation (auto-documentation)

TODO: domain bootstrap helpers, webapp configuration,..

