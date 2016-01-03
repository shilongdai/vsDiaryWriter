# journal-2
This is my second attempt at a journal software, previous attempt is the journal-1 project.

# description:
This project is a opensource journal writer for writing journal entries on "soft" medias. The current plan is finish the pc version, and then develop a mobile app.

# build

requirements:
  - maven
  - access to maven central repository
  - jdk > 1.6

To build, enter the directory with the pom.xml file, and run mvn install. The finished uber-jar product should be in the target directory

# using
To use the application, it's recommended to install the unlimited strength policy files for your jvm to provide greater keylength for encryption purposes.
To run the application,
  execute: java -jar full-journal-version.jar
Or: if your operating system's graphical ui supports it, double click the jar file.(when the gui of the application is done)

This application uses SWT for it's GUI, so please use the appropriate version/release, or modify the pom file according to your operating system.
