# journal-2
This is a journal writer application that support authentication, encryption, selectable modules, and rich-text/html editing.

# build

requirements:
  - maven
  - access to maven central repository
  - jdk >= 1.7

To build, enter the directory with the pom.xml file, and run mvn install. The finished uber-jar product should be in the target directory

# using
To run the application,
  execute: java -jar full-journal-version.jar or java -jar -XstartOnFirstThread the-jar-file.jar if you are a Mac user
  
Or: if your operating system's graphical ui supports it, double click the jar file.(when the gui of the application is done)

This application uses SWT for it's GUI, so please use the appropriate version/release, or modify the pom file according to your operating system.

# binary distributions
Binary distribution of this application is available at the release section of this software.
