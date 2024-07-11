# The Makers Store (Scala Play)

This project uses Scala 2.13.14 and Play 3.0.4.

## Setting up the project

First, one person in your team should clone this repository locally and invite
the rest of the team members as collaborators.

Then, continue through the following sections:

### Database setup

This project uses PostgreSQL as the database engine.

You should have PostgreSQL installed already as you used it as part of the
Foundations track:

In your terminal, run the following commands:
- `createdb the_makers_store_dev` to create your development database, and then
- `createdb the_makers_store_test` to create your database for testing

Make sure to update your database connection parameters with your database
username and password in the [application.conf](./conf/application.conf) file.


### Managing your application data in Play Framework

This project uses [Play Slick](https://scala-slick.org/), one of the preferred
tools for this purpose within Play Framework.

_Note that Slick is not an ORM, but a database access library for Scala._

You can find more useful information
[here](https://scala-slick.org/doc/3.0.0/orm-to-slick).


#### Evolutions

You should be familiar with the concept of database schema migrations already as
part of previous modules.

In the Play Slick world, migrations as you know them are named
[`Evolutions`](https://www.playframework.com/documentation/3.0.x/Evolutions).

If you ask me, I think this term is great. Think about it this way: "As my
project `evolves`, my database schema is likely to `evolve` as well alongside my
project.


### Running the application and tests

The project was created using `sbt`, which you should be familiar with already
to a certain extent.

This is a list of the commands you will find yourself using the most in your
Play app:
- `sbt run`: starts the application (port 9000 by default).
- `sbt test`: runs all your tests.

And [here's](https://www.scala-sbt.org/1.x/docs/Running.html#Common+commands) a
full list of `sbt` commands.

<!-- OMITTED -->

Testing can be a bit cumbersome in IntelliJ. The best process I have personally
found is to run tests in debug mode and insert breakpoints throughout key areas
of your code where you'd like to get an exact picture of what your application
state is and what's going on.

Please let your coach know if you're unsure how to do that!


## First tasks

**Fix the failing test!**

Your colleague Severus has been working for a number of days to implement the
starting codebase you're going to work on.

He has set up a number of integration tests to cover the initial functionality
of the application. However, one of the tests is failing.

:dart: Your first task is to find out which test is failing and fix it.

Before you start modifying the test, ask yourself the following questions:
- Why is the test failing at the moment?
- What validations do I need to put in place for users fields?
- Where should these validations live?


<!-- OMITTED -->


Regarding validations for newly created users, we do not have a formal User Story from the team (shame!). However, Severus had a meeting with the product owner and then sent you a Slack message that reads:
- Emails should adhere to [the following format](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/email#validation)
- Passwords should follow [this pattern](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/password#validation)


<!-- OMITTED -->



**Cleaning the messy test database**

At this point, you have probably generated a good list of users in your test
database. It's not great practice to keep all of them there and keep adding new
ones every time your test suites run.

And what's worse, the current state of your test database may affect the outcome
of future tests!

:dart: Find a way to tear down any data generated from your current tests cases.

[This](https://www.scalatest.org/scaladoc/3.0.6/org/scalatest/BeforeAndAfterEach.html)
may be a helpful starting point for your research!


<!-- OMITTED -->


## What about styling?

We'll use the default templating engine that comes with Play.

You are welcome to try to style your views as you please. However, this is not
the main focus for your training this week. Your main focus is the backend and
coming up with a fully functioning REST API to add value to your customers
visiting The Makers Store.

## What's next?

Congratulations on finishing the first tasks! You should be already somewhat
familiar with the codebase and Play Framework.

Head over to the [list of tickets](./TICKETS.md) to work through as you continue
your Play journey.

Good luck!


### Additional resources
- [Getting started with Play Framework](https://www.playframework.com/getting-started)
- [Official Play Samples](https://github.com/playframework/play-samples)
- [Using Play Slick](https://www.playframework.com/documentation/3.0.x/PlaySlick)
