# DDD on ZIO

Build an application with DDD mindset using ZIO.
See the presentation [video](https://youtu.be/U_ZS0JuXE7w).

The slides for the talk, press 'X' and 'Y' to navigate between pages.  
http://go.limnu.com/geum-lively

The talk mainly covering this ideas:
- Create a consistent layers. If you see from this [clip](https://youtu.be/U_ZS0JuXE7w?list=PL6xpkW5ZGL8MLLGIP9wNfRMxO7R0PhSum&t=1152),
  layer marked as I is called "nurture" layer and II is "nature" layer. A consistent layer should has identical nurture and nature layer.
- How not to leak infrastructure ideas into core, e.g. `trait InvoiceClient` in `core` should be avoided because the word client is a "hint" for "HTTP".
- How to handle previous problem, what the client will look like in `core`.
- Service comes from algebra.
- Structuring the project. Each algebra in `core` will have its own folder which consists of an algebra, repository trait, models.
- Config confusion, how to avoid funny dependencies caused by config.
