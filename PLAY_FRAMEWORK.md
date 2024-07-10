# Play Framework

Welcome to Play!

Play Framework is a high-velocity web framework for Java (we won't do Java as
part of this module! Do not fear.) and Scala. It follows the MVC
(Model-View-Controller) architectural design pattern, which helps separate
concerns in your application, making it easier to manage and scale.

Play follows the MVC architectural design pattern.

<!-- OMITTED -->

## MVC Pattern

**Models**

Models represent the data and business logic of your application. They are
responsible for managing the state of your application and handling interactions
with the database.

**Views**

Views are responsible for displaying data to the user. Play uses Twirl templates
to generate HTML, which can include dynamic content.

**Controllers**

Controllers handle user input and interactions. They receive requests, process
them (often by interacting with models), and return responses (usually rendered
views or JSON data).


## Slick

Slick is a Functional Relational Mapping (FRM) library for Scala, allowing you
to work with databases in a functional way.

## DAO

The Data Access Object (DAO) pattern is used to abstract and encapsulate all
access to the data source. The `UserDAO` class in this module's app is an
example of this pattern in action, providing methods to interact with the users
table in the database.

## Models

Models in Play are simple case classes. The `User` class is an example of a
model representing a user in the system.
