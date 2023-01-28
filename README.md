# DDD on ZIO

Build an application with DDD mindset using ZIO.
See the presentation [video](https://youtu.be/U_ZS0JuXE7w).

The slides for the talk, press 'X' and 'Y' to navigate between pages.  
http://go.limnu.com/geum-lively

The talk mainly covering this ideas:
- Create a consistent dependency between layers.
- How not to leak infrastructure ideas into core. As in, the class name `client` is often inside the `core` but the word `Client` is like a hint for HTTP client.
- Service comes from algebra.
- Structuring the project, as in each service (algebra) will have its own folder which consists of entities, value objects, repository.
- Config confusion, which layer should config be located.
