# vsDiaryWriter
## Overview:
The VSDiayWriter is a diary writer that supports search indexing, encryption, authentication, and HTML editing. The application is designed modularly and supports multiple types of components that users can freely choose from.

## Features:
* Authentication with password
* Entry subjects and contents are encrypted
* HTML WYSIWYG editing support
* Capable of exporting and importing backups
* Keyword search
* Date range filter
* Ability to change components and password on the run
* Selectable data storage, indexing, encryption, and authentication components
* Many selectable encryption algorithms, mac algorithms, kdf algorithms, hash algorithms, paddings, and modes.
* Capable of 3rd pary extension

## Building:
This is a maven project. To create the jar file, use the install command with maven

## Running:
On windows, it is already packaged as a executable(exe) or setup file.

On Linux and Mac, Java needs to be installed. Then, run the launch script.

## Upgrading:
For upgrading between non-major version differences, simply replace the Jar file or the executable with the new version. It should be backward compatible. For upgrading between major versions, follow instructions on the wiki or release (if it is there).
